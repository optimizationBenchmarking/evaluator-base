package org.optimizationBenchmarking.evaluator.data.spec.builders;

/** A context for creating experiment sets. */
public interface IExperimentContext
    extends INamedContext, IParameterSettingScope {
  /**
   * Create an instance runs context
   *
   * @return the instance runs context
   */
  public abstract IInstanceRunsContext createInstanceRuns();
}
