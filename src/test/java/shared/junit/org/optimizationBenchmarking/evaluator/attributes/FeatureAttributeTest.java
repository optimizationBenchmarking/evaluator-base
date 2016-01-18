package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;

/**
 * A test for attributes processing single features.
 *
 * @param <RT>
 *          the result type
 */
@Ignore
public class FeatureAttributeTest<RT> extends
    ElementAttributeTest<IFeature, RT, Attribute<? super IFeature, ? extends RT>> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public FeatureAttributeTest(
      final Attribute<? super IFeature, ? extends RT> attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected List<IFeature> getElementSelection(final IExperimentSet data) {
    final ArrayList<IFeature> selection;
    selection = new ArrayList<>();
    selection.addAll(data.getFeatures().getData());
    return selection;
  }
}
