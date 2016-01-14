package org.optimizationBenchmarking.evaluator.data.impl.ref;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IInstanceRunsContext;
import org.optimizationBenchmarking.utils.hierarchy.FSM;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/** A context for creating instance run sets . */
public final class InstanceRunsContext
    extends _HierarchicalCollection<Run, RunContext, InstanceRuns>
    implements IInstanceRunsContext {

  /** we have an instance */
  private static final int FLAG_HAS_INSTANCE = (FSM.FLAG_NOTHING + 1);

  /** the properties */
  private volatile _PropertyFSMSettingBuilder<ParameterSetting> m_props;
  /** the instance */
  private volatile Instance m_instance;

  /**
   * create the experiment set context
   *
   * @param context
   *          the context
   * @param ownerProps
   *          the owner properties
   */
  InstanceRunsContext(final ExperimentContext context,
      final _PropertyFSMSettingBuilder<ParameterSetting> ownerProps) {
    super(context);
    this.m_props = new _PropertyFSMSettingBuilder<>(ownerProps);
    this.open();
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    if (flagValue == InstanceRunsContext.FLAG_HAS_INSTANCE) {
      append.append("instanceSet"); //$NON-NLS-1$
      return;
    }
    super.fsmFlagsAppendName(flagValue, flagIndex, append);
  }

  /** {@inheritDoc} */
  @Override
  final Class<RunContext> _getChildType() {
    return RunContext.class;
  }

  /**
   * Get the instance belonging to this run context.
   *
   * @return the instance belonging to this run context
   */
  public final Instance getInstance() {
    return this.m_instance;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setInstance(final IInstance inst) {
    if (inst == null) {
      throw new IllegalArgumentException(//
          "Cannot set dimension direction to null."); //$NON-NLS-1$
    }
    if (this.m_instance != inst) {
      if (!(this._getInstanceSet().getData().contains(inst))) {
        throw new IllegalArgumentException("Instance '" + //$NON-NLS-1$
            inst + //
            "' is not contained in the current instance set."); //$NON-NLS-1$
      }

      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          InstanceRunsContext.FLAG_HAS_INSTANCE,
          InstanceRunsContext.FLAG_HAS_INSTANCE, FSM.FLAG_NOTHING);
      this.fsmStateAssert(_FSM.STATE_OPEN);
      this.m_instance = ((Instance) inst);
    }
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setInstance(final String inst) {
    final Instance i;
    i = this._getInstanceSet().find(this.normalizeLocal(inst));
    if (i == null) {
      throw new IllegalArgumentException(
          (("No instance matches the string '" + //$NON-NLS-1$
              inst) + '\'') + '.');
    }
    this.setInstance(i);
  }

  /** {@inheritDoc} */
  @Override
  public final ExperimentContext getOwner() {
    return ((ExperimentContext) (super.getOwner()));
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
  public final RunContext createRun() {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    return new RunContext(this, this.m_props);
  }

  /** {@inheritDoc} */
  @Override
  final InstanceRuns _doCompile(final ArrayList<Run> data) {
    final Instance inst;

    this.fsmFlagsAssertTrue(InstanceRunsContext.FLAG_HAS_INSTANCE);

    inst = this.m_instance;
    this.m_instance = null;
    if (inst == null) {
      throw new IllegalStateException(//
          "Instance must not be null."); //$NON-NLS-1$
    }
    return new InstanceRuns(inst, data.toArray(new Run[data.size()]),
        false, false, true);
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
}
