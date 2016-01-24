package org.optimizationBenchmarking.evaluator.evaluation.impl.abstr;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;

/**
 * The kind of job which can carry out the work of an
 * {@link org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentModule}
 * .
 */
public abstract class ExperimentJob extends EvaluationJob<IExperiment> {
  /**
   * Create the experiment evaluation job
   *
   * @param data
   *          the data
   * @param logger
   *          the logger
   */
  protected ExperimentJob(final IExperiment data, final Logger logger) {
    super(data, logger);
  }
}
