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
 * @param <AT>
 *          the attribute type
 */
@Ignore
public class ExperimentSetAttributeTest<RT, AT extends Attribute<? super IExperimentSet, ? extends RT>>
    extends AttributeTest<IExperimentSet, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public ExperimentSetAttributeTest(final AT attribute) {
    super(attribute);
  }

  /** {@inheritDoc} */
  @Override
  protected void testOnExperimentSet(final IExperimentSet data) {
    final AT attribute;

    attribute = this.getAttribute(data, data);
    Assert.assertNotNull(attribute);
    this.checkResult(attribute, data, data,
        attribute.get(data, TestBase.getNullLogger()));
  }
}
