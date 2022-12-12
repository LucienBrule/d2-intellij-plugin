// This is a generated file. Not intended for manual editing.
package com.dvd.intellij.d2.lang.psi.impl;

import com.dvd.intellij.d2.lang.D2CompositeElementImpl;
import com.dvd.intellij.d2.lang.D2PsiTreeUtil;
import com.dvd.intellij.d2.lang.psi.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class D2InlineShapeDefinitionImpl extends D2CompositeElementImpl implements D2InlineShapeDefinition {

  public D2InlineShapeDefinitionImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull D2Visitor visitor) {
    visitor.visitInlineShapeDefinition(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof D2Visitor) accept((D2Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public D2InlineShapeDefinition getInlineShapeDefinition() {
    return findChildByClass(D2InlineShapeDefinition.class);
  }

  @Override
  @Nullable
  public D2ShapeConnection getShapeConnection() {
    return findChildByClass(D2ShapeConnection.class);
  }

  @Override
  @NotNull
  public List<D2ShapeDefinition> getShapeDefinitionList() {
    return D2PsiTreeUtil.getChildrenOfTypeAsList(this, D2ShapeDefinition.class);
  }

  @Override
  @Nullable
  public D2SubShapeDefinition getSubShapeDefinition() {
    return findChildByClass(D2SubShapeDefinition.class);
  }

}
