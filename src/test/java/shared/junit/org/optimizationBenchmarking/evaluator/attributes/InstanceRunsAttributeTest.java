package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;

/**
 * A test for attributes processing single instance runs sets.
 *
 * @param <RT>
 *          the result type
 */
@Ignore
public class InstanceRunsAttributeTest<RT> extends
    ElementAttributeTest<IInstanceRuns, RT, Attribute<? super IInstanceRuns, ? extends RT>> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public InstanceRunsAttributeTest(
      final Attribute<? super IInstanceRuns, ? extends RT> attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected List<IInstanceRuns> getElementSelection(
      final IExperimentSet data) {
    final ArrayList<IInstanceRuns> all;
    all = new ArrayList<>();
    for (final IExperiment exp : data.getData()) {
      all.addAll(exp.getData());
    }
    return all;
  }
}
