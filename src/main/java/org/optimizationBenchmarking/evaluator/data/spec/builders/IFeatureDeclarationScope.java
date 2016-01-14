package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.utils.IScope;

/** A scope in which features can be declared. */
public interface IFeatureDeclarationScope extends IScope {

  /**
   * Define an instance feature with a given name and description
   *
   * @param name
   *          the feature name
   * @param desc
   *          the feature's description
   */
  public abstract void declareFeature(final String name,
      final String desc);
}
