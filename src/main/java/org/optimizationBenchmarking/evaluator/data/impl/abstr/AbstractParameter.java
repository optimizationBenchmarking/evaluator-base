package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterSet;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.comparison.Compare;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IParameter}
 * interface.
 */
public abstract class AbstractParameter extends AbstractProperty
    implements IParameter {

  /** the owner */
  IParameterSet m_owner;

  /**
   * Create the abstract parameter. If {@code owner==null}, you must later
   * set it via
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractParameterSet#own(AbstractParameter)}
   * .
   *
   * @param owner
   *          the owner
   */
  protected AbstractParameter(final IParameterSet owner) {
    super();
    this.m_owner = owner;
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractParameterValue}
   * .
   *
   * @param parameterValue
   *          the parameter set to own
   */
  protected final void own(final AbstractParameterValue parameterValue) {
    if (parameterValue == null) {
      throw new IllegalArgumentException(//
          "AbstractParameterValue to be owned by AbstractParameter cannot be null."); //$NON-NLS-1$
    }
    synchronized (parameterValue) {
      if (parameterValue.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractParameterValue to be owned by AbstractParameter already owned.");//$NON-NLS-1$
      }
      parameterValue.m_owner = this;
    }
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public ArrayListView<? extends IParameterValue> getData() {
    return ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /** {@inheritDoc} */
  @Override
  public IParameterValue findValue(final Object value) {
    IParameterValue pvalue;

    pvalue = ((IParameterValue) (super.findValue(value)));
    if (pvalue == null) {
      pvalue = this.getUnspecified();
      if ((pvalue == null)
          || (!(Compare.equals(pvalue.getValue(), value)))) {
        return null;
      }
    }
    return pvalue;
  }

  /** {@inheritDoc} */
  @Override
  public IParameterValue getUnspecified() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IParameterValue getGeneralized() {
    return ((IParameterValue) (super.getGeneralized()));
  }

  /** {@inheritDoc} */
  @Override
  public final IParameterSet getOwner() {
    return this.m_owner;
  }

}
