package org.jetbrains.plugins.d2.lang.psi

import com.intellij.extapi.psi.ASTDelegatePsiElement
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.SharedImplUtil
import com.intellij.ui.ColorHexUtil
import org.jetbrains.plugins.d2.NAMED_COLORS
import java.awt.Color

class ShapeProperty(node: ASTNode) : AstWrapperPsiElement(node)

class ShapePropertyKey(node: ASTNode) : AstWrapperPsiElement(node)

class IdPropertyMap(node: ASTNode) : AstWrapperPsiElement(node)

class D2Array(node: ASTNode) : AstWrapperPsiElement(node)

sealed interface PropertyValue : PsiElement

class OtherValue(node: ASTNode) : AstWrapperPsiElement(node), PropertyValue

class StringValue(node: ASTNode) : AstWrapperPsiElement(node), PropertyValue, ColorValueProvider {
  override fun getColor(): Color? = NAMED_COLORS.get(text.removeSurrounding("\""))
}

class UnquotedStringValue(node: ASTNode) : AstWrapperPsiElement(node), PropertyValue, ColorValueProvider {
  override fun getColor(): Color? = NAMED_COLORS.get(text)
}

class ColorValue(node: ASTNode) : AstWrapperPsiElement(node), PropertyValue, ColorValueProvider {
  override fun getColor(): Color? = ColorHexUtil.fromHexOrNull(text.removeSurrounding("\""))
}

// toString doesn't print node element type
sealed class AstWrapperPsiElement(private val node: ASTNode) : ASTDelegatePsiElement() {
  override fun getParent(): PsiElement = SharedImplUtil.getParent(node)

  override fun getNode(): ASTNode = node

  override fun toString(): String = javaClass.simpleName
}