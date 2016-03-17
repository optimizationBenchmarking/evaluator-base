package shared.junit.org.optimizationBenchmarking.evaluator.evaluation;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentModule;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;

/** Test an experiment module. */
@Ignore
public class ExperimentModuleTest
    extends EvaluationModuleTest<IExperimentModule, IExperiment> {

  /**
   * create the test
   *
   * @param module
   *          the module to test
   */
  protected ExperimentModuleTest(final IExperimentModule module) {
    super(module);
  }

  /** {@inheritDoc} */
  @Override
  final void _setDataToJobBuilder(final IExperiment data,
      final IEvaluationJobBuilder jobBuilder) {
    Assert.assertTrue(jobBuilder instanceof IExperimentJobBuilder);
    ((IExperimentJobBuilder) jobBuilder).setData(data);
  }

  /** {@inheritDoc} */
  @Override
  protected final void applyToExperimentSet(
      final IExperimentSet experimentSet) {
    final ArrayListView<? extends IExperiment> data;
    final int size;
    boolean[] done;
    int next;

    data = experimentSet.getData();
    Assert.assertNotNull(data);
    size = data.size();
    Assert.assertTrue(size > 0);

    done = new boolean[size];
    this._applyToData(data.get(0));
    done[0] = true;

    next = size - 1;
    if (!(done[next])) {
      this._applyToData(data.get(next));
      done[next] = true;
    }

    next = (size >>> 1);
    if (!(done[next])) {
      this._applyToData(data.get(next));
      done[next] = true;
    }
  }
}
