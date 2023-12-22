package com.dvd.intellij.d2.ide.service

import com.dvd.intellij.d2.components.D2Layout
import com.dvd.intellij.d2.components.D2Theme
import com.dvd.intellij.d2.ide.action.ConversionOutput
import com.dvd.intellij.d2.ide.editor.images.D2FileEditorImpl
import com.dvd.intellij.d2.ide.execution.D2Command
import com.dvd.intellij.d2.ide.execution.D2CommandOutput
import com.dvd.intellij.d2.ide.format.D2FormatterResult
import com.dvd.intellij.d2.ide.toolWindow.D2ToolWindowService
import com.intellij.execution.process.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.BufferExposingByteArrayOutputStream
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.net.ServerSocket
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

private val LOG = logger<D2ServiceImpl>()

private class D2ServiceImpl : D2Service, Disposable {
  private val _map: MutableMap<FileEditor, D2CommandOutput.Generate> = HashMap()

  override val map: Map<FileEditor, D2CommandOutput.Generate> = _map

  override fun getCompilerVersion(): String? = simpleRun(D2Command.Version)?.version

  override fun isCompilerInstalled() = getCompilerVersion() != null

  override fun getLayoutEngines(): List<D2Layout>? = simpleRun(D2Command.LayoutEngines)?.layouts

  override fun compile(fileEditor: FileEditor) {
    val oldExec = _map[fileEditor]
    val oldCmd = oldExec?.cmd

    val cmd = if (oldCmd == null) {
      val port = ServerSocket(0).run {
        val p = localPort
        close()
        p
      }

      D2Command.Generate(
        d2 = fileEditor.file,
        dest = generateTempSvg(),
        port = port,
        theme = D2Theme.DEFAULT,
        layout = D2Layout.DEFAULT,
      )
    } else {
      oldCmd.copy(
        theme = fileEditor.getUserData(D2FileEditorImpl.D2_FILE_THEME) ?: D2Theme.DEFAULT,
        layout = fileEditor.getUserData(D2FileEditorImpl.D2_FILE_LAYOUT) ?: D2Layout.DEFAULT,
      )
    }

    oldExec?.cmd?.process?.let {
      it.destroyProcess()
      @Suppress("ControlFlowWithEmptyBody")
      val terminationTime = measureTimeMillis {
        // background process? ~5ms
        while (!it.isProcessTerminated);
      }
      "[plugin ] [info] D2 process termination ${terminationTime}ms".let { msg ->
        _map[fileEditor] = _map[fileEditor]?.appendLog(msg) ?: return
        LOG.info(msg)
      }
    }
    cmd.process = prepare(cmd, object : ProcessListener {
      override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) = onTextAvailable(fileEditor, event, outputType)

      override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) = deleteFile(fileEditor)
    })

    @Suppress("IfThenToElvis")
    _map[fileEditor] = if (oldExec == null) {
      cmd.parseOutput("[plugin ] info: starting process...\n")
    } else {
      oldExec.copy(cmd = cmd).appendLog("[plugin ] info: restarting process...\n")
    }
    cmd.process?.startNotify()
  }

  override fun closeFile(fileEditor: FileEditor) {
    fileEditor.putUserData(D2FileEditorImpl.D2_FILE_LAYOUT, null)
    fileEditor.putUserData(D2FileEditorImpl.D2_FILE_THEME, null)

    _map[fileEditor]?.cmd?.process?.destroyProcess()
    _map -= fileEditor

    LOG.info("[plugin] Closed file")
  }

  override fun format(file: File): D2FormatterResult {
    val out = simpleRun(D2Command.Format(file))?.content
    return when {
      out == null -> D2FormatterResult.Error("Unknown error")
      out.contains("err: failed") -> D2FormatterResult.Error(out)
      else -> D2FormatterResult.Success(out)
    }
  }

  override fun convert(file: VirtualFile, format: ConversionOutput): ByteArray {
    if (format == ConversionOutput.SVG) {
      return file.contentsToByteArray()
    }

    val input = file.inputStream.use { ImageIO.read(it) }
    val ext = when (format) {
      ConversionOutput.PNG -> "png"
      ConversionOutput.JPG -> "jpeg"
      ConversionOutput.TIFF -> "tiff"
      else -> {
        error("cannot be")
      }
    }
    val out = BufferExposingByteArrayOutputStream()
    ImageIO.write(input, ext, out)
    return out.toByteArray()
  }

  private fun onTextAvailable(fileEditor: FileEditor, event: ProcessEvent, outputType: Key<*>) {
    buildString {
      append("[process] ")
      if (outputType == ProcessOutputType.SYSTEM) {
        append("info: ")
        append(event.text)
      } else {
        // remove timestamp
        append(event.text.replace(Regex("\\[?\\d{2}:\\d{2}:\\d{2}]? "), ""))
      }
    }.let {
      LOG.info(it)

      // null if file editor closed
      _map[fileEditor] = _map[fileEditor]?.appendLog(it) ?: return
    }

    // update toolwindow console
    (fileEditor as D2FileEditorImpl).project.service<D2ToolWindowService>().update(fileEditor)
  }

  override fun dispose() {
    map.forEach { deleteFile(it.key) }
    _map.clear()
  }

  private fun deleteFile(fileEditor: FileEditor) {
    val app = ApplicationManager.getApplication()

    val deleteFile: () -> Unit = { map[fileEditor]?.cmd?.dest?.delete(null) }
    if (app.isDispatchThread) {
      try {
        app.runWriteAction(deleteFile)
      } catch (_: Throwable) {
        // todo
        // Already disposed: MessageBus(owner=Application  (containerState DISPOSED)  (internal) (WA inProgress allowed) (RA allowed) (exit in progress), disposeState= 1)
        // but isWriteAccessAllowed = true
      }
    } else {
      app.invokeLater(deleteFile)
    }
  }

  private fun generateTempSvg(): VirtualFile {
    val temp = File.createTempFile("d2_temp_svg", ".svg")
    temp.writeText("""<?xml version="1.0" encoding="UTF-8"?><svg xmlns="http://www.w3.org/2000/svg" width="1" height="1"/>""")

    return VfsUtil.findFile(temp.toPath(), true) ?: error("Cannot find temp file ${temp.absolutePath}")
  }

  private fun prepare(command: D2Command<*>, listener: ProcessListener?) =
    KillableColoredProcessHandler.Silent(command.createCommandLine().apply {
      withEnvironment(command.envVars())
    }).apply {
      if (listener != null) {
        addProcessListener(listener)
      }
    }

  // null if d2 executable not found
  private fun <O> simpleRun(cmd: D2Command<O>): O? {
    try {
      val processOut = ScriptRunnerUtil.getProcessOutput(
        cmd.createCommandLine(),
        ScriptRunnerUtil.STDOUT_OR_STDERR_OUTPUT_KEY_FILTER,
        500
      )
      return cmd.parseOutput(processOut)
    } catch (e: Exception) {
      LOG.error(e)
      return null
    }
  }
}