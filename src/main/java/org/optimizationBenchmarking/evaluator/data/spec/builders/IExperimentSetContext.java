package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.utils.tools.spec.IToolJob;

/** A builder for experiments. */
public interface IExperimentSetContext extends IToolJob,
    IFeatureDeclarationScope, IParameterDeclarationScope {

  /**
   * Create the dimension context
   *
   * @return the dimension context
   */
  public abstract IDimensionContext createDimension();

  /**
   * Create the instance context
   *
   * @return the instance context
   */
  public abstract IInstanceContext createInstance();

  /**
   * Create the experiment context
   *
   * @return the experiment context
   */
  public abstract IExperimentContext createExperiment();

  /**
   * Obtain the created dimension set. This method is only available after
   * the {@link #createDimension() dimension set creation} has been
   * completed and will throw a {@link java.lang.IllegalStateException}
   * before that.
   *
   * @return the created dimension set
   */
  public abstract IDimensionSet getDimensionSet();

  /**
   * Obtain the created instance set. This method is only available after
   * the {@link #createInstance() instance set creation} has been completed
   * and will throw a {@link java.lang.IllegalStateException} before that.
   *
   * @return the created instance set
   */
  public abstract IInstanceSet getInstanceSet();

  /**
   * Obtain the created feature set. This method is only available after
   * the {@link #createInstance() instance set creation} has been completed
   * and will throw a {@link java.lang.IllegalStateException} before that.
   *
   * @return the created feature set
   */
  public abstract IFeatureSet getFeatureSet();

  /**
   * Obtain the created experiment set
   *
   * @return the created experiment set
   */
  public abstract IExperimentSet create();
}
