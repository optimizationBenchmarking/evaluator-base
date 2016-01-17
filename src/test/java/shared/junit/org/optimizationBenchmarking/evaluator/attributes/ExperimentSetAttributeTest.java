package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import org.junit.Assert;
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
   */
  public ExperimentSetAttributeTest(
      final Attribute<? super IExperimentSet, ? extends RT> attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected void testOnExperimentSet(final IExperimentSet data) {
    final Attribute<? super IExperimentSet, ? extends RT> attribute;

    attribute = this.getAttribute(data, data);
    Assert.assertNotNull(attribute);
    this.checkResult(attribute, data, data,
        attribute.get(data, TestBase.getNullLogger()));
  }
}
