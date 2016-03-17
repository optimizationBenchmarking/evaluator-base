package shared.junit.org.optimizationBenchmarking.evaluator.evaluation;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentSetJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentSetModule;

/** Test an experiment set module. */
@Ignore
public class ExperimentSetModuleTest
    extends EvaluationModuleTest<IExperimentSetModule, IExperimentSet> {

  /**
   * create the test
   *
   * @param module
   *          the module to test
   */
  protected ExperimentSetModuleTest(final IExperimentSetModule module) {
    super(module);
  }

  /** {@inheritDoc} */
  @Override
  final void _setDataToJobBuilder(final IExperimentSet data,
      final IEvaluationJobBuilder jobBuilder) {
    Assert.assertTrue(jobBuilder instanceof IExperimentSetJobBuilder);
    ((IExperimentSetJobBuilder) jobBuilder).setData(data);
  }

  /** {@inheritDoc} */
  @Override
  protected final void applyToExperimentSet(
      final IExperimentSet experimentSet) {
    this._applyToData(experimentSet);
  }
}
