package org.optimizationBenchmarking.evaluator.data.impl.ref;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IRunContext;
import org.optimizationBenchmarking.utils.hierarchy.HierarchicalFSM;

/** A context for creating runs. */
public final class RunContext extends _Context<Run>
    implements IRunContext {

  /** the properties */
  private volatile _PropertyFSMSettingBuilder<ParameterSetting> m_props;
  /** the list of data points */
  private volatile ArrayList<DataPoint> m_list;

  /**
   * create the run context
   *
   * @param context
   *          the context
   * @param ownerProps
   *          the owner properties
   */
  RunContext(final InstanceRunsContext context,
      final _PropertyFSMSettingBuilder<ParameterSetting> ownerProps) {
    super(context);

    this.m_list = new ArrayList<>();
    (this.m_props = new _PropertyFSMSettingBuilder<>(ownerProps))._begin();

    this.open();
  }

  /** {@inheritDoc} */
  @Override
  public final InstanceRunsContext getOwner() {
    return ((InstanceRunsContext) (super.getOwner()));
  }

  /**
   * get the dimension set
   *
   * @return the dimension set
   */
  final DimensionSet _getDimensionSet() {
    return this.getOwner()._getDimensionSet();
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void addDataPoint(final IDataPoint point) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    if (point == null) {
      throw new IllegalArgumentException(//
          "Data point must not be null."); //$NON-NLS-1$
    }
    this.m_list.add((DataPoint) point);
  }

  /** {@inheritDoc} */
  @Override
  public final void addDataPoint(final String string) {
    this.addDataPoint(//
        this._getDimensionSet().getDataFactory().parseString(string));
  }

  /** {@inheritDoc} */
  @Override
  public final void addDataPoint(final Number... numbers) {
    this.addDataPoint(//
        this._getDimensionSet().getDataFactory().parseNumbers(numbers));
  }

  /** {@inheritDoc} */
  @Override
  public final void addDataPoint(final Object rawObject) {
    this.addDataPoint(//
        this._getDimensionSet().getDataFactory().parseObject(rawObject));
  }

  /** {@inheritDoc} */
  @Override
  final Run _doCompile() {
    final ArrayList<DataPoint> l;
    l = this.m_list;
    this.m_list = null;
    return this._getDimensionSet().getDataFactory()
        .createRun(this.getOwner().getInstance(), l);
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void beforeChildOpens(
      final HierarchicalFSM child, final boolean hasOtherChildren) {
    super.beforeChildOpens(child, hasOtherChildren);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void afterChildOpened(
      final HierarchicalFSM child, final boolean hasOtherChildren) {
    super.afterChildOpened(child, hasOtherChildren);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  protected synchronized final void afterChildClosed(
      final HierarchicalFSM child) {
    super.afterChildClosed(child);
    this.throwChildNotAllowed(child);
  }

  /** {@inheritDoc} */
  @Override
  protected final synchronized void onClose() {
    super.onClose();
    this.m_props._end();
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
