package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet}
 * interface.
 */
public abstract class AbstractExperimentSet extends AbstractNamedElementSet
    implements IExperimentSet {

  /** create */
  protected AbstractExperimentSet() {
    super();
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractExperiment}
   * .
   *
   * @param experiment
   *          the experiment to own
   */
  protected final void own(final AbstractExperiment experiment) {
    if (experiment == null) {
      throw new IllegalArgumentException(//
          "AbstractExperiment to be owned by AbstractExperimentSet cannot be null."); //$NON-NLS-1$
    }
    synchronized (experiment) {
      if (experiment.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractExperiment to be owned by AbstractExperimentSet already owned.");//$NON-NLS-1$
      }
      experiment.m_owner = this;
    }
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractFeatureSet}
   * .
   *
   * @param features
   *          the features set to own
   */
  protected final void own(final AbstractFeatureSet features) {
    if (features == null) {
      throw new IllegalArgumentException(//
          "AbstractFeatureSet to be owned by AbstractExperimentSet cannot be null."); //$NON-NLS-1$
    }
    synchronized (features) {
      if (features.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractFeatureSet to be owned by AbstractExperimentSet already owned.");//$NON-NLS-1$
      }
      features.m_owner = this;
    }
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractParameterSet}
   * .
   *
   * @param parameters
   *          the parameters set to own
   */
  protected final void own(final AbstractParameterSet parameters) {
    if (parameters == null) {
      throw new IllegalArgumentException(//
          "AbstractParameterSet to be owned by AbstractExperimentSet cannot be null."); //$NON-NLS-1$
    }
    synchronized (parameters) {
      if (parameters.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractParameterSet to be owned by AbstractExperimentSet already owned.");//$NON-NLS-1$
      }
      parameters.m_owner = this;
    }
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractDimensionSet}
   * .
   *
   * @param dimensions
   *          the dimensions set to own
   */
  protected final void own(final AbstractDimensionSet dimensions) {
    if (dimensions == null) {
      throw new IllegalArgumentException(//
          "AbstractDimensionSet to be owned by AbstractExperimentSet cannot be null."); //$NON-NLS-1$
    }
    synchronized (dimensions) {
      if (dimensions.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractDimensionSet to be owned by AbstractExperimentSet already owned.");//$NON-NLS-1$
      }
      dimensions.m_owner = this;
    }
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractInstanceSet}
   * .
   *
   * @param instances
   *          the instances set to own
   */
  protected final void own(final AbstractInstanceSet instances) {
    if (instances == null) {
      throw new IllegalArgumentException(//
          "AbstractInstanceSet to be owned by AbstractExperimentSet cannot be null."); //$NON-NLS-1$
    }
    synchronized (instances) {
      if (instances.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractInstanceSet to be owned by AbstractExperimentSet already owned.");//$NON-NLS-1$
      }
      instances.m_owner = this;
    }
  }

  /** {@inheritDoc} */
  @Override
  public IDataElement getOwner() {
    return null;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public ArrayListView<? extends IExperiment> getData() {
    final IDataElement owner;

    owner = this.getOwner();
    if (owner instanceof IExperimentSet) {// maybe...
      return ((IExperimentSet) owner).getData();
    }

    // ...ok, then we have nothing
    return ((ArraySetView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /** {@inheritDoc} */
  @Override
  public IDimensionSet getDimensions() {
    final IDataElement owner;

    owner = this.getOwner();
    if (owner instanceof IExperimentSet) {// last ditch..
      return ((IExperimentSet) owner).getDimensions();
    }
    if (owner instanceof IDimensionSet) {// this would be odd..
      return ((IDimensionSet) owner);
    }

    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  public IInstanceSet getInstances() {
    final IDataElement owner;

    owner = this.getOwner();
    if (owner instanceof IExperimentSet) {// last ditch..
      return ((IExperimentSet) owner).getInstances();
    }
    if (owner instanceof IInstanceSet) {// this would be odd..
      return ((IInstanceSet) owner);
    }
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  public IFeatureSet getFeatures() {
    final IDataElement owner;

    owner = this.getOwner();
    if (owner instanceof IExperimentSet) {// last ditch..
      return ((IExperimentSet) owner).getFeatures();
    }
    if (owner instanceof IFeatureSet) {// this would be odd..
      return ((IFeatureSet) owner);
    }
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  public IParameterSet getParameters() {
    final IDataElement owner;

    owner = this.getOwner();
    if (owner instanceof IExperimentSet) {// last ditch..
      return ((IExperimentSet) owner).getParameters();
    }
    if (owner instanceof IParameterSet) {// this would be odd..
      return ((IParameterSet) owner);
    }
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  public IExperiment find(final String name) {
    return ((IExperiment) (super.find(name)));
  }
}
