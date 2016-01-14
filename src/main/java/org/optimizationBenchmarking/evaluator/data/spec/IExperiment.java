package org.optimizationBenchmarking.evaluator.data.spec;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;

/**
 * An experiment.
 */
public interface IExperiment extends INamedElement, IElementSet {

  /**
   * Get the owning experiment set
   *
   * @return the owning experiment set
   */
  @Override
  public abstract IExperimentSet getOwner();

  /**
   * Get the instance runs of this experiment
   *
   * @return the instance runs of this experiment
   */
  @Override
  public abstract ArrayListView<? extends IInstanceRuns> getData();

  /**
   * Get the parameter map.
   *
   * @return the parameter map.
   */
  public abstract IParameterSetting getParameterSetting();

}
