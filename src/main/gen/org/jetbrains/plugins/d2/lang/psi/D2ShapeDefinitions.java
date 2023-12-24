// This is a generated file. Not intended for manual editing.
package org.jetbrains.plugins.d2.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.d2.lang.D2CompositeElement;

public interface D2ShapeDefinitions extends D2CompositeElement {

  @Nullable
  D2BlockDefinition getBlockDefinition();

  @Nullable
  D2InlineShapeDefinition getInlineShapeDefinition();

  @Nullable
  D2LabelDefinition getLabelDefinition();

  @Nullable
  D2ShapeConnection getShapeConnection();

  @Nullable
  D2ShapeDefinition getShapeDefinition();

  @Nullable
  D2SubShapeDefinition getSubShapeDefinition();

}
