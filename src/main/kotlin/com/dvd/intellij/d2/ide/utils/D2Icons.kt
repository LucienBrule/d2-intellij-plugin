package com.dvd.intellij.d2.ide.utils

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object D2Icons {
  val FILE by lazy { IconLoader.getIcon("icons/d2.svg", D2Icons::class.java.classLoader) }

  val SHAPE: Icon
    get() = AllIcons.Nodes.Gvariable

  val ATTRIBUTE: Icon
    get() = AllIcons.Nodes.Property

  val CONNECTION: Icon
    get() = AllIcons.Diff.ArrowLeftRight
}