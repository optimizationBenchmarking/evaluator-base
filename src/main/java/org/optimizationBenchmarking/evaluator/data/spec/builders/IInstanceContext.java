package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.evaluator.data.spec.IDimension;

/** A builder for benchmark instances. */
public interface IInstanceContext
    extends INamedContext, IFeatureDeclarationScope {

  /**
   * Set a feature value.
   *
   * @param featureName
   *          the feature name
   * @param featureDescription
   *          the feature description
   * @param featureValue
   *          the feature value
   * @param featureValueDescription
   *          the feature value description
   */
  public abstract void setFeatureValue(final String featureName,
      final String featureDescription, final Object featureValue,
      final String featureValueDescription);

  /**
   * Set a feature value.
   *
   * @param featureName
   *          the feature name
   * @param featureValue
   *          the feature value
   * @param featureValueDescription
   *          the feature value description
   */
  public abstract void setFeatureValue(final String featureName,
      final Object featureValue, final String featureValueDescription);

  /**
   * Set a feature value.
   *
   * @param featureName
   *          the feature name
   * @param featureValue
   *          the feature value
   */
  public abstract void setFeatureValue(final String featureName,
      final Object featureValue);

  /**
   * Set the lower boundary for the given dimension
   *
   * @param dim
   *          the dimension
   * @param bound
   *          the lower bound
   */
  public abstract void setLowerBound(final IDimension dim,
      final Number bound);

  /**
   * Set the lower boundary for the given dimension
   *
   * @param dim
   *          the dimension
   * @param bound
   *          the lower bound
   */
  public abstract void setLowerBound(final Object dim, final Object bound);

  /**
   * Set the upper boundary for the given dimension
   *
   * @param dim
   *          the dimension
   * @param bound
   *          the upper bound
   */
  public abstract void setUpperBound(final IDimension dim,
      final Number bound);

  /**
   * Set the upper boundary for the given dimension
   *
   * @param dim
   *          the dimension
   * @param bound
   *          the upper bound
   */
  public abstract void setUpperBound(final Object dim, final Object bound);
}
