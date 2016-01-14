package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IParameterValue}
 * interface.
 */
public abstract class AbstractParameterValue extends AbstractPropertyValue
    implements IParameterValue {

  /** the owning feature */
  IParameter m_owner;

  /**
   * Create the abstract feature value. If {@code owner==null}, you must
   * later set it via
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractParameter#own(AbstractParameterValue)}
   * .
   *
   * @param owner
   *          the owner
   */
  protected AbstractParameterValue(final IParameter owner) {
    super();
    this.m_owner = owner;
  }

  /** {@inheritDoc} */
  @Override
  public final IParameter getOwner() {
    return this.m_owner;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isUnspecified() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return String.valueOf(this.getValue());
  }
}
