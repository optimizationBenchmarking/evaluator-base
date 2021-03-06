package shared.junit.org.optimizationBenchmarking.evaluator.evaluation;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationJob;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationJobBuilder;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentModule;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IExperimentSetModule;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.reflection.ReflectionUtils;

import shared.DummyDocument;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.utils.tools.ToolTest;
import examples.org.optimizationBenchmarking.evaluator.dataAndIO.Example1;
import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomExample;

/**
 * Test the evaluation modules.
 *
 * @param <MT>
 *          the module type
 * @param <DT>
 *          the data element
 */
@Ignore
public abstract class EvaluationModuleTest<MT extends IEvaluationModule, DT extends IDataElement>
extends ToolTest<MT> {

  /**
   * create the module test
   *
   * @param module
   *          the module to test
   */
  protected EvaluationModuleTest(final MT module) {
    super(module);
  }

  /** test the module type */
  @Test(timeout = 3600000)
  public void testType() {
    final IEvaluationModule module;
    final EModuleType type;

    module = this.getInstance();
    Assert.assertNotNull(module);

    type = module.getType();
    Assert.assertNotNull(type);

    switch (type) {
      case DESCRIPTION:
      case APPENDIX: {
        Assert.assertTrue(module instanceof IExperimentSetModule);
        break;
      }
      case BODY: {
        Assert.assertTrue((module instanceof IExperimentSetModule)
            || (module instanceof IExperimentModule));
        break;
      }
      default: {
        throw new AssertionError("Unsupported type: " + type); //$NON-NLS-1$
      }
    }
  }

  /** test whether we can correctly obtain the list of required modules. */
  @Test(timeout = 3600000)
  public void testGetRequiredModules() {
    final Iterable<Class<? extends IEvaluationModule>> list;
    final IEvaluationModule module;
    HashSet<Class<? extends IEvaluationModule>> classes;
    IEvaluationModule other;

    module = this.getInstance();
    Assert.assertNotNull(module);
    list = module.getRequiredModules();
    Assert.assertNotNull(list);

    classes = new HashSet<>();
    Assert.assertTrue(classes.add(module.getClass()));

    for (Class<? extends IEvaluationModule> moduleClass : list) {
      Assert.assertNotNull(module);
      Assert.assertTrue(IEvaluationModule.class
          .isAssignableFrom(moduleClass));
      Assert.assertTrue(classes.add(moduleClass));

      try {
        other = ReflectionUtils.getInstance(IEvaluationModule.class,
            moduleClass, null);
      } catch (final Throwable error) {
        throw new AssertionError(//
            ("Could not instantiate required module class " + //$NON-NLS-1$
                moduleClass + '.'), error);
      }
      moduleClass = null;

      Assert.assertNotNull(other);
      Assert.assertNotNull(module.getRelationship(other));
      other = null;
    }
  }

  /**
   * set the data to the job builder.
   *
   * @param data
   *          the data
   * @param jobBuilder
   *          the job builder
   */
  abstract void _setDataToJobBuilder(final DT data,
      final IEvaluationJobBuilder jobBuilder);

  /**
   * Apply the module test to a given data set. This method should call
   * {@link #_applyToData(IDataElement)}
   *
   * @param experimentSet
   *          the data set
   */
  protected abstract void applyToExperimentSet(
      final IExperimentSet experimentSet);

  /**
   * Obtain the configuration to be used for configuring module.
   *
   * @param data
   *          the data element which the module should be applied to
   * @return the configuration
   */
  protected Configuration getConfiguration(final DT data) {
    return Configuration.createEmpty();
  }

  /**
   * apply the module to a given data element
   *
   * @param data
   *          the data
   */
  final void _applyToData(final DT data) {
    final MT module;
    IEvaluationJobBuilder jobBuilder;
    IEvaluationJob job;
    Configuration config;

    module = this.getInstance();
    Assert.assertNotNull(module);

    if (module.canUse()) {
      jobBuilder = module.use();
      Assert.assertNotNull(jobBuilder);
      config = this.getConfiguration(data);
      if (config != null) {
        jobBuilder.configure(config);
      }
      jobBuilder.setLogger(TestBase.getNullLogger());
      this._setDataToJobBuilder(data, jobBuilder);

      try {
        job = jobBuilder.create();
      } catch (final Throwable error) {
        throw new AssertionError(//
            "Failed to create evaluation module job.", //$NON-NLS-1$
            error);
      }

      try (final DummyDocument document = new DummyDocument()) {
        job.initialize(document);
        job.summary(document);
        job.main(document);
      }
    }
  }

  /** test whether we can correctly process the example data set 1 */
  @Test(timeout = 3600000)
  public void testOnExampleData1() {
    final IExperimentSet experimentSet;

    try {
      experimentSet = new Example1(TestBase.getNullLogger()).call();
    } catch (final Throwable error) {
      throw new AssertionError("Failed to load example data set 1.", //$NON-NLS-1$
          error);
    }
    Assert.assertNotNull(experimentSet);
    this.applyToExperimentSet(experimentSet);
  }

  /** test whether we can correctly process the random data set */
  @Test(timeout = 3600000)
  public void testOnRandomData() {
    final IExperimentSet experimentSet;

    try {
      experimentSet = new RandomExample(false, TestBase.getNullLogger())
      .call();
    } catch (final Throwable error) {
      throw new AssertionError(
          "Failed to generate random example data set.", //$NON-NLS-1$
          error);
    }
    Assert.assertNotNull(experimentSet);
    this.applyToExperimentSet(experimentSet);
  }
}
