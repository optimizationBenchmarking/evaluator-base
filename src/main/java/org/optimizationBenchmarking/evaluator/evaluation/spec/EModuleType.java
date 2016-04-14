package org.optimizationBenchmarking.evaluator.evaluation.spec;

/** The evaluation module types. */
public enum EModuleType {

  /**
   * A module which should be executed at the beginning of the evaluation
   * procedure and will contribute to the "description" part of the
   * generated report. This could be a summary on the used benchmark
   * instances or an overview about the applied algorithms. Such modules
   * <em>must</em> always be instances of {@link IExperimentSetModule}.
   */
  DESCRIPTION,
  /**
   * A module which should be executed in the body of the generated report.
   * It will contribute some concrete statistics and performance metrics.
   * Modules of this type might either be instances of
   * {@link IExperimentSetModule} or {@link IExperimentModule}. In the
   * former case they are executed exactly once with the whole experiment
   * set as parameter. If they are instances of {@link IExperimentModule},
   * they will be applied to each single experiment record separately.
   */
  BODY,
  /**
   * A module which should be executed at the end of the evaluation
   * procedure and will contribute to the "appendix" part of the generated
   * report. This could be a more detailed description of the evaluation
   * procedure, a concrete specification of applied statistical methods,
   * for instance. Such modules could be included via
   * {@link IEvaluationModule#getRequiredModules()} from the modules
   * selected by the user. Such modules <em>must</em> always be instances
   * of {@link IExperimentSetModule}.
   */
  APPENDIX;

}
