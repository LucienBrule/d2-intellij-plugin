// This is a generated file. Not intended for manual editing.
package org.jetbrains.plugins.d2.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import org.jetbrains.plugins.d2.lang.D2CompositeElement;

public interface D2BlockDefinition extends D2CompositeElement {

  @NotNull
  List<D2Property> getPropertyList();

  @NotNull
  List<D2ShapeDefinitions> getShapeDefinitionsList();

}
