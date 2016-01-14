package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import java.util.Iterator;

import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSetting;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IFeatureSetting}
 * interface.
 */
public class AbstractFeatureSetting extends AbstractPropertySetting
    implements IFeatureSetting {

  /**
   * Create the abstract property setting
   */
  protected AbstractFeatureSetting() {
    super();
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Iterator<IFeatureValue> iterator() {
    return ((Iterator) (this.entrySet().iterator()));
  }

}