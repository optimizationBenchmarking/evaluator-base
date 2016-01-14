package org.optimizationBenchmarking.evaluator.data.impl.ref;

import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;

/**
 * A feature value.
 */
public final class FeatureValue extends PropertyValue<Feature>
    implements IFeatureValue {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /**
   * Create the feature value
   *
   * @param name
   *          the string representation of this value
   * @param desc
   *          the description
   * @param value
   *          the value
   */
  FeatureValue(final String name, final String desc, final Object value) {
    super(name, desc, value);
  }
}
