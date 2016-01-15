package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.TestBase;

/**
 * A test for attributes processing an experiment set.
 *
 * @param <RT>
 *          the result type
 */
@Ignore
public class ExperimentSetAttributeTest<RT> extends
    AttributeTest<IExperimentSet, RT, Attribute<? super IExperimentSet, ? extends RT>> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   * @param isSingleton
   *          is the attribute a singleton?
   */
  public ExperimentSetAttributeTest(
      final Attribute<? super IExperimentSet, ? extends RT> attribute,
      final boolean isSingleton) {
    super(attribute, isSingleton);
  }

  /** {@inheritDoc} */
  @Override
  protected void testOnExperimentSet(final IExperimentSet data) {
    this.assertResult(
        this.getInstance().get(data, TestBase.getNullLogger()));
  }
}
