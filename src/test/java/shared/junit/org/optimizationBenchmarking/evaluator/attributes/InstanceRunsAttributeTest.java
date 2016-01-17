package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;

import shared.junit.TestBase;

/**
 * A test for attributes processing an experiment set.
 *
 * @param <RT>
 *          the result type
 */
@Ignore
public class InstanceRunsAttributeTest<RT> extends
    AttributeTest<IInstanceRuns, RT, Attribute<? super IInstanceRuns, ? extends RT>> {

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
  protected void testOnExperimentSet(final IExperimentSet data) {
    final Random random;
    final ArrayList<IInstanceRuns> all;
    IInstanceRuns runs;
    int samples;
    Attribute<? super IInstanceRuns, ? extends RT> attribute;

    all = new ArrayList<>();
    for (final IExperiment exp : data.getData()) {
      all.addAll(exp.getData());
    }

    Assert.assertFalse(all.isEmpty());
    random = new Random();
    samples = 10;
    while (((--samples) >= 0) && (all.size() > 0)) {
      runs = all.remove(random.nextInt(all.size()));
      Assert.assertNotNull(runs);
      attribute = this.getAttribute(data, runs);
      Assert.assertNotNull(attribute);
      this.checkResult(attribute, data, runs,
          attribute.get(runs, TestBase.getNullLogger()));
    }
  }
}
