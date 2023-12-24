// This is a generated file. Not intended for manual editing.
package org.jetbrains.plugins.d2.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import org.jetbrains.plugins.d2.lang.D2CompositeElement;

public interface D2InlineShapeDefinition extends D2CompositeElement {

  @Nullable
  D2InlineShapeDefinition getInlineShapeDefinition();

  @Nullable
  D2ShapeConnection getShapeConnection();

  @NotNull
  List<D2ShapeDefinition> getShapeDefinitionList();

  @Nullable
  D2SubShapeDefinition getSubShapeDefinition();

}
