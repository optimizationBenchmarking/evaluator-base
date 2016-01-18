package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Ignore;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import shared.junit.TestBase;

/**
 * A base class for testing attributes on a sub-set of the
 * {@link IExperimentSet} data structure.
 *
 * @param <AT>
 *          the attribute type
 * @param <ST>
 *          the source object type which is attributed
 * @param <RT>
 *          the result type of the attribute
 */
@Ignore
public abstract class ElementAttributeTest<ST extends IDataElement, RT, AT extends Attribute<? super ST, ? extends RT>>
    extends AttributeTest<ST, RT, AT> {

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public ElementAttributeTest(final AT attribute) {
    super(attribute);
  }

  /**
   * Obtain the selection of all eligible elements for computing the
   * attribute from the source data set. It must be possible to modify this
   * list, i.e., it should be possible to delete elements from it without
   * modifying the backing {@link IExperimentSet data} structure.
   *
   * @param data
   *          the source data set
   * @return the list of eligible elements
   */
  protected abstract List<ST> getElementSelection(
      final IExperimentSet data);

  /**
   * Get the maximum number of
   * {@linkplain #getElementSelection(IExperimentSet) eligible elements}
   * for which the attribute should be computed.
   *
   * @return the maximum number of
   *         {@linkplain #getElementSelection(IExperimentSet) eligible
   *         elements} for which the attribute should be computed
   */
  protected int getMaxAttributeComputationsPerDataset() {
    return 10;
  }

  /** {@inheritDoc} */
  @Override
  protected void testOnExperimentSet(final IExperimentSet data) {
    final Random random;
    final List<ST> all;
    final int max;
    int samples, size;

    all = this.getElementSelection(data);
    Assert.assertNotNull(all);
    Assert.assertFalse(all.isEmpty());
    size = all.size();
    Assert.assertTrue(size > 0);

    max = this.getMaxAttributeComputationsPerDataset();
    Assert.assertTrue(max > 0);

    if (size <= max) {
      for (final ST element : all) {
        this.__invoke(data, element);
      }
    } else {
      random = new Random();
      samples = max;
      while (((--samples) >= 0) && (size > 0)) {
        this.__invoke(data, all.remove(random.nextInt(size)));
        Assert.assertEquals(all.size(), --size);
      }
      // check the rest
      for (final ST element : all) {
        Assert.assertNotNull(element);
      }
    }
  }

  /**
   * invoke a single test
   *
   * @param experimentSet
   *          the experiment set
   * @param data
   *          the data element
   */
  private final void __invoke(final IExperimentSet experimentSet,
      final ST data) {
    final AT attribute;

    Assert.assertNotNull(experimentSet);
    Assert.assertNotNull(data);
    attribute = this.getAttribute(experimentSet, data);
    Assert.assertNotNull(attribute);
    this.checkResult(attribute, experimentSet, data,
        attribute.get(data, TestBase.getNullLogger()));
  }
}
