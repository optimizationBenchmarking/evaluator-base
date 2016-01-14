package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue}
 * interface.
 */
public abstract class AbstractPropertyValue extends AbstractNamedElement
    implements IPropertyValue {

  /** Create the property. */
  protected AbstractPropertyValue() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public abstract IProperty getOwner();

  /** {@inheritDoc} */
  @Override
  public Object getValue() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isGeneralized() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return String.valueOf(this.getValue());
  }
}
