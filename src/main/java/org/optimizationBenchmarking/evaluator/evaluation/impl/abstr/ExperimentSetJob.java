package org.optimizationBenchmarking.evaluator.evaluation.impl.abstr;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

/**
 * The kind of job which can carry out the work of an
 * {@link org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentSetModule}
 * .
 */
public abstract class ExperimentSetJob
    extends EvaluationJob<IExperimentSet> {
  /**
   * Create the experiment set evaluation job
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   */
  protected ExperimentSetJob(final IExperimentSet data,
      final Logger logger) {
    super(data, logger);
  }
}
