package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

/**
 * A test for attributes processing single dimensions.
 *
 * @param <RT>
 *          the result type
 * @param <AT>
 *          the attribute type
 */
@Ignore
public class DimensionAttributeTest<RT, AT extends Attribute<? super IDimension, ? extends RT>>
    extends ElementAttributeTest<IDimension, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public DimensionAttributeTest(final AT attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected List<IDimension> getElementSelection(
      final IExperimentSet data) {
    final ArrayList<IDimension> selection;
    selection = new ArrayList<>();
    selection.addAll(data.getDimensions().getData());
    return selection;
  }
}
