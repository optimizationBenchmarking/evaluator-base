package org.optimizationBenchmarking.evaluator.data.impl.partial;

import java.util.ArrayList;
import java.util.Iterator;

import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractFeatureSetting;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.utils.comparison.Compare;

/** the internal feature setting container */
final class _FeatureSetting extends AbstractFeatureSetting {

  /** the feature list */
  private final ArrayList<_FeatureValue> m_features;

  /** create */
  _FeatureSetting() {
    super();
    this.m_features = new ArrayList<>();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final Iterator<IFeatureValue> iterator() {
    return ((Iterator) (this.m_features.iterator()));
  }

  /**
   * Set the given feature
   *
   * @param featureValue
   *          the feature value
   */
  final void _setFeatureValue(final _FeatureValue featureValue) {
    final IFeature feature;

    feature = featureValue.getOwner();

    for (final _FeatureValue fvalue : this.m_features) {
      if (Compare.equals(fvalue.getOwner(), feature)) {
        if (fvalue == featureValue) {
          return;
        }
        throw new IllegalStateException("Feature '" + //$NON-NLS-1$
            feature.getName() + //
            "' has already been set to '" + fvalue.getName() + //$NON-NLS-1$
            "' and cannot be set to '" + featureValue.getName() + //$NON-NLS-1$
            '\'' + '.');
      }
    }

    this.m_features.add(featureValue);
  }
}
