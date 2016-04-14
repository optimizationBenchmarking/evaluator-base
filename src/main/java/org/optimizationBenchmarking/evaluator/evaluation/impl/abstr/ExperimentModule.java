package org.optimizationBenchmarking.evaluator.evaluation.impl.abstr;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationJob;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentModule;
import org.optimizationBenchmarking.utils.config.Configuration;

/**
 * The abstract basic implementation for experiment modules.
 */
public abstract class ExperimentModule
    extends _EvaluationModule<IExperiment> implements IExperimentModule {

  /** create */
  protected ExperimentModule() {
    super(EModuleType.BODY); // must be body module either way
  }

  /** {@inheritDoc} */
  @Override
  public final IExperimentJobBuilder use() {
    this.checkCanUse();
    return new _ExperimentJobBuilder(this);
  }

  /**
   * Create the experiment job.
   *
   * @param data
   *          the experiment to be processed by the job
   * @param config
   *          the configuration
   * @param logger
   *          the logger to write log information to, or {@code null} if no
   *          log info should be written
   */
  @Override
  public abstract IEvaluationJob createJob(IExperiment data,
      Configuration config, Logger logger);
}
