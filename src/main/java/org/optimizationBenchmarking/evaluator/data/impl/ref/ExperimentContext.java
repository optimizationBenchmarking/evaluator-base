package org.optimizationBenchmarking.evaluator.data.impl.ref;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractNamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IExperimentContext;
import org.optimizationBenchmarking.utils.hierarchy.FSM;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/** A context for creating experiment sets. */
public final class ExperimentContext extends
    _HierarchicalCollection<InstanceRuns, InstanceRunsContext, Experiment>
    implements IExperimentContext {

  /** we have a name */
  private static final int FLAG_HAS_NAME = (FSM.FLAG_NOTHING + 1);

  /** the name of this context has been set */
  private static final String FLAG_HAS_NAME_NAME = _NamedContext.FLAG_HAS_NAME_NAME;

  /** the experiment's name */
  private volatile String m_name;

  /** the description */
  private volatile String m_description;

  /** the parameter settings builder */
  private volatile _PropertyFSMSettingBuilder<ParameterSetting> m_props;

  /**
   * create the experiment set context
   *
   * @param context
   *          the context
   * @param pb
   *          the parameters builder
   */
  ExperimentContext(final _ExperimentSetContext context,
      final _ParametersBuilder pb) {
    super(context);
    this.m_props = new _PropertyFSMSettingBuilder<>(pb);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  final Class<InstanceRunsContext> _getChildType() {
    return InstanceRunsContext.class;
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    if (flagValue == ExperimentContext.FLAG_HAS_NAME) {
      append.append(ExperimentContext.FLAG_HAS_NAME_NAME);
      return;
    }
    super.fsmFlagsAppendName(flagValue, flagIndex, append);
  }

  /** {@inheritDoc} */
  @Override
  protected final _ExperimentSetContext getOwner() {
    return ((_ExperimentSetContext) (super.getOwner()));
  }

  /**
   * Get the experiment set builder owning this dimension context
   *
   * @return the experiment set builder owning this dimension context
   */
  public final ExperimentSetContext getBuilder() {
    return this.getOwner().getOwner();
  }

  /**
   * get the dimension set
   *
   * @return the dimension set
   */
  final DimensionSet _getDimensionSet() {
    return this.getOwner()._getDimensionSet();
  }

  /**
   * get the instance set
   *
   * @return the instance set
   */
  final InstanceSet _getInstanceSet() {
    return this.getOwner()._getInstanceSet();
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setName(final String name) {
    final String n;

    this.fsmStateAssert(_FSM.STATE_OPEN);
    n = this.normalizeLocal(name);
    if (n == null) {
      throw new IllegalArgumentException(((//
      "Specified name must not be empty or null, but is '" + name) + '\'') //$NON-NLS-1$
          + '.');
    }
    if (!(n.equals(this.m_name))) {
      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          ExperimentContext.FLAG_HAS_NAME, ExperimentContext.FLAG_HAS_NAME,
          FSM.FLAG_NOTHING);
      this.m_name = n;
    }
  }

  /**
   * Get the name of this object
   *
   * @return the name of this object
   */
  public synchronized final String getName() {
    this.fsmFlagsAssertTrue(ExperimentContext.FLAG_HAS_NAME);
    return this.m_name;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setDescription(final String description) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_description = this.normalizeLocal(description);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void addDescription(final String description) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_description = this.normalizeLocal(
        AbstractNamedElement.mergeDescriptions(this.m_description,
            this.normalizeLocal(description)));
  }

  /**
   * Get the description of this object
   *
   * @return the description of this object
   */
  public synchronized final String getDescription() {
    return this.m_description;
  }

  /** {@inheritDoc} */
  @Override
  final Experiment _doCompile(final ArrayList<InstanceRuns> data) {
    final String a, b, c, d;
    final _PropertyFSMSettingBuilder<ParameterSetting> f;

    a = this.m_name;
    this.m_name = null;
    c = this.m_description;
    this.m_description = null;
    f = this.m_props;
    this.m_props = null;

    this.fsmFlagsAssertTrue(ExperimentContext.FLAG_HAS_NAME);

    b = this.normalize(a);
    if (b == null) {
      throw new IllegalArgumentException(//
          "A name must not normalize to null, buth the name '" //$NON-NLS-1$
              + a + "' of " + this + //$NON-NLS-1$
              " does."); //$NON-NLS-1$
    }

    if (c != null) {
      d = this.normalize(c);

      if (d == null) {
        throw new IllegalArgumentException(//
            "A non-empty descriptionmust not normalize to null, buth the description '" //$NON-NLS-1$
                + c + "' of " + this + //$NON-NLS-1$
                " does."); //$NON-NLS-1$
      }
    } else {
      d = null;
    }

    return new Experiment(b, //
        d, //
        f._finalize(), //
        data.toArray(new InstanceRuns[data.size()]), //
        false, true, true);

  }

  /** {@inheritDoc} */
  @Override
  protected final synchronized void onClose() {
    super.onClose();
    this.m_props._compile();
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void declareParameter(final String name,
      final String desc) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_props._declareProperty(name, desc);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setParameterValue(
      final String parameterName, final String parameterDescription,
      final Object parameterValue,
      final String parameterValueDescription) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_props._setPropertyValue(parameterName, parameterDescription,
        parameterValue, parameterValueDescription);
  }

  /** {@inheritDoc} */
  @Override
  public final void setParameterValue(final String parameterName,
      final Object parameterValue,
      final String parameterValueDescription) {
    this.setParameterValue(parameterName, null, parameterValue,
        parameterValueDescription);
  }

  /** {@inheritDoc} */
  @Override
  public final void setParameterValue(final String parameterName,
      final Object parameterValue) {
    this.setParameterValue(parameterName, parameterValue, null);
  }

  /** {@inheritDoc} */
  @Override
  public final InstanceRunsContext createInstanceRuns() {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    return new InstanceRunsContext(this, this.m_props);
  }

  /**
   * Does an instance of the given name exist? This method may come in
   * handy when parsing a directory structure of experiments and wanting to
   * check whether a directory name may represent a set of runs for a
   * problem instance or not.
   *
   * @param name
   *          the instance name
   * @return the instance
   */
  public final boolean doesInstanceExist(final String name) {
    return (this._getInstanceSet().find(name) != null);
  }
}
