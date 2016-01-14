package org.optimizationBenchmarking.evaluator.data.impl.shadow;

import java.util.Collection;

import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;

/** a feature value selection */
final class _FeatureValueSelection
    extends _PropertyValueSelection<IFeature, IFeatureValue> {

  /**
   * create the feature value selection
   *
   * @param feature
   *          the original feature
   */
  _FeatureValueSelection(final IFeature feature) {
    super(feature);
  }

  /** {@inheritDoc} */
  @Override
  final IFeature _shadow(final IFeature original,
      final Collection<IFeatureValue> elements) {
    return new ShadowFeature(null, original, elements);
  }
}
