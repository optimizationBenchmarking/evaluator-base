package org.optimizationBenchmarking.evaluator.evaluation.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentSetJobBuilder;

/** The experiment set job builder implementation */
final class _ExperimentSetJobBuilder extends
    _EvaluationJobBuilder<IExperimentSet, ExperimentSetModule, _ExperimentSetJobBuilder>
    implements IExperimentSetJobBuilder {

  /**
   * create the job builder
   *
   * @param module
   *          the module
   */
  _ExperimentSetJobBuilder(final ExperimentSetModule module) {
    super(module);
  }
}
