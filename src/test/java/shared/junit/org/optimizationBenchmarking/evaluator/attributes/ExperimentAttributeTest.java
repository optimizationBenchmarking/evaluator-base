package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

/**
 * A test for attributes processing single experiments.
 *
 * @param <RT>
 *          the result type
 * @param <AT>
 *          the attribute type
 */
@Ignore
public class ExperimentAttributeTest<RT, AT extends Attribute<? super IExperiment, ? extends RT>>
    extends ElementAttributeTest<IExperiment, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public ExperimentAttributeTest(final AT attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected List<IExperiment> getElementSelection(
      final IExperimentSet data) {
    final ArrayList<IExperiment> experiments;
    experiments = new ArrayList<>();
    experiments.addAll(data.getData());
    return experiments;
  }
}
