package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.evaluator.data.spec.IInstance;

/** A context for creating instance run sets. */
public interface IInstanceRunsContext extends IParameterSettingScope {

  /**
   * Set the instance of this instance run context
   *
   * @param inst
   *          the instance of this instance run context
   */
  public abstract void setInstance(final IInstance inst);

  /**
   * Set the instance of this instance run context
   *
   * @param inst
   *          the instance of this instance run context
   */
  public abstract void setInstance(final String inst);

  /**
   * Create an run context
   *
   * @return the run context
   */
  public abstract IRunContext createRun();
}
