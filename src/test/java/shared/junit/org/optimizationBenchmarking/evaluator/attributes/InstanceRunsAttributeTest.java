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
 * @param <AT>
 *          the attribute type
 */
@Ignore
public class InstanceRunsAttributeTest<RT, AT extends Attribute<? super IInstanceRuns, ? extends RT>>
    extends ElementAttributeTest<IInstanceRuns, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public InstanceRunsAttributeTest(final AT attribute) {
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
