package shared.junit.org.optimizationBenchmarking.evaluator.attributes;

import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.data.spec.Attribute;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.BBOBExample;
import examples.org.optimizationBenchmarking.evaluator.dataAndIO.Example1;
import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomExample;
import examples.org.optimizationBenchmarking.evaluator.dataAndIO.TSPSuiteExample;
import shared.junit.TestBase;

/**
 * A base class for testing attributes.
 *
 * @param <AT>
 *          the attribute type
 * @param <ST>
 *          the source object type which is attributed
 * @param <RT>
 *          the result type of the attribute
 */
@Ignore
public abstract class AttributeTest<ST extends IDataElement, RT, AT extends Attribute<? super ST, ? extends RT>>
    extends TestBase {

  /** the attribute */
  private final AT m_attribute;

  /**
   * Create the test.
   *
   * @param attribute
   *          the attribute to test
   */
  public AttributeTest(final AT attribute) {
    super();
    this.m_attribute = attribute;
  }

  /**
   * Get the instance of the attribute applicable to the given data
   *
   * @param experimentSet
   *          the experiment set
   * @param data
   *          the data
   * @return the attribute
   */
  protected AT getAttribute(final IExperimentSet experimentSet,
      final ST data) {
    return this.m_attribute;
  }

  /**
   * Test the attribute on the given data set. This method is supposed to
   * invoke the attribute and check it's output via
   * {@link #checkResult(Attribute,IExperimentSet,IDataElement,Object)}
   *
   * @param data
   *          the data set
   * @see #checkResult(Attribute,IExperimentSet,IDataElement,Object)
   */
  protected abstract void testOnExperimentSet(final IExperimentSet data);

  /**
   * Test on a given callable
   *
   * @param caller
   *          the caller
   */
  private final void __testOnCallable(
      final Callable<IExperimentSet> caller) {
    final IExperimentSet set;

    Assert.assertNotNull(caller);
    try {
      set = caller.call();
    } catch (final Throwable error) {
      throw new RuntimeException("Failed to load example data.", error); //$NON-NLS-1$
    }
    Assert.assertNotNull(set);
    this.testOnExperimentSet(set);
  }

  /**
   * This method tests whether the result produced by the attribute is OK
   *
   * @param attribute
   *          the attribute
   * @param experimentSet
   *          the experiment set
   * @param input
   *          the input data
   * @param result
   *          the result
   */
  protected void checkResult(final AT attribute,
      final IExperimentSet experimentSet, final ST input,
      final RT result) {
    Assert.assertNotNull(result);
  }

  /**
   * Check if the attribute can be used.
   *
   * @return {@code true} if the attribute can be used, {@code false}
   *         otherwise
   */
  protected boolean canUseAttribute() {
    return true;
  }

  /** Test the attribute's functionality on the example 1 */
  @Test(timeout = 3600000)
  public void testAttributeOnExample1() {
    if (this.canUseAttribute()) {
      this.__testOnCallable(new Example1(TestBase.getNullLogger()));
    }
  }

  /** Test the attribute's functionality on a subset of the BBOB data */
  @Test(timeout = 3600000)
  public void testAttributeOnBBOB() {
    if (this.canUseAttribute()) {
      this.__testOnCallable(new BBOBExample(TestBase.getNullLogger()));
    }
  }

  /**
   * Test the attribute's functionality on a subset of the TSP Suite data
   */
  @Test(timeout = 3600000)
  public void testAttributeOnTSPSuite() {
    if (this.canUseAttribute()) {
      this.__testOnCallable(new TSPSuiteExample(TestBase.getNullLogger()));
    }
  }

  /**
   * Test the attribute's functionality on random data
   */
  @Test(timeout = 3600000)
  public void testAttributeOnRandomData() {
    if (this.canUseAttribute()) {
      this.__testOnCallable(new RandomExample(TestBase.getNullLogger()));
    }
  }
}
