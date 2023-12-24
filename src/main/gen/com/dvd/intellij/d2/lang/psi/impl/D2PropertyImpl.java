// This is a generated file. Not intended for manual editing.
package com.dvd.intellij.d2.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.dvd.intellij.d2.lang.D2PsiTreeUtil;
import static com.dvd.intellij.d2.lang.D2ElementTypes.*;
import com.dvd.intellij.d2.lang.D2CompositeElementImpl;
import com.dvd.intellij.d2.lang.psi.*;

public class D2PropertyImpl extends D2CompositeElementImpl implements D2Property {

  public D2PropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull D2Visitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof D2Visitor) accept((D2Visitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public D2AttributeValue getAttributeValue() {
    return findChildByClass(D2AttributeValue.class);
  }

  @Override
  @NotNull
  public D2PropertyKey getPropertyKey() {
    return findNotNullChildByClass(D2PropertyKey.class);
  }

}
