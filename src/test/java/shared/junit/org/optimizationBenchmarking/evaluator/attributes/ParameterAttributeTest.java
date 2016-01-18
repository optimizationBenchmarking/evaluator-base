package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IParameter;

/**
 * A test for attributes processing single parameters.
 *
 * @param <RT>
 *          the result type
 * @param <AT>
 *          the attribute type
 */
@Ignore
public class ParameterAttributeTest<RT, AT extends Attribute<? super IParameter, ? extends RT>>
    extends ElementAttributeTest<IParameter, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public ParameterAttributeTest(final AT attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected List<IParameter> getElementSelection(
      final IExperimentSet data) {
    final ArrayList<IParameter> selection;
    selection = new ArrayList<>();
    selection.addAll(data.getParameters().getData());
    return selection;
  }
}
