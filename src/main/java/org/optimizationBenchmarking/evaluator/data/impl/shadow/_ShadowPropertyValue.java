package org.optimizationBenchmarking.evaluator.data.impl.shadow;

import java.util.Map;

import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.utils.comparison.Compare;

/**
 * A shadow property value is basically a shadow of another property value
 * with a different owner and potentially different attributes. If all
 * associated data of this element is the same, it will delegate
 * attribute-based computations to that property value.
 *
 * @param <OT>
 *          the owner type
 * @param <ST>
 *          the shadow type
 */
class _ShadowPropertyValue<OT extends IProperty, ST extends IPropertyValue>
    extends _ShadowNamedElement<OT, ST>
    implements IPropertyValue, Map.Entry<IProperty, Object> {

  /**
   * create the shadow property value
   *
   * @param owner
   *          the owning property
   * @param shadow
   *          the property value to shadow
   */
  _ShadowPropertyValue(final OT owner, final ST shadow) {
    super(owner, shadow);
  }

  /** {@inheritDoc} */
  @Override
  public final Object getValue() {
    return this.m_shadowUnpacked.getValue();
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isGeneralized() {
    return this.m_shadowUnpacked.isGeneralized();
  }

  /** {@inheritDoc} */
  @Override
  public final IProperty getKey() {
    return this.getOwner();
  }

  /** {@inheritDoc} */
  @Override
  public final Object setValue(final Object value) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  public final int compareTo(final IDataElement o) {
    final IPropertyValue pv;
    int res;
    final _ShadowDataElement other;

    if (o == null) {
      return (-1);
    }
    if (o == this) {
      return 0;
    }

    if ((this.m_shadowUnpacked == o) || //
        (this.m_shadowDelegate == o)) {
      return 0;
    }

    if (o instanceof _ShadowDataElement) {
      other = ((_ShadowDataElement) o);
      if ((this.m_shadowUnpacked == other.m_shadowUnpacked) || //
          (this == other.m_shadowUnpacked) || //
          (this == other.m_shadowDelegate)) {
        return 0;
      }
    } else {
      other = null;
    }

    findWayToCompare: {
      if (other instanceof _ShadowPropertyValue) {
        res = Compare.compare(//
            this.m_shadowUnpacked.getOwner(), //
            other.m_shadowUnpacked.getOwner());
        if (res != 0) {
          return res;
        }

        break findWayToCompare;
      }

      if (o instanceof IPropertyValue) {
        pv = ((IPropertyValue) o);

        res = Compare.compare(this.getOwner(), pv.getOwner());
        if (res != 0) {
          return res;
        }

        res = Compare.compare(this.m_shadowUnpacked.getValue(),
            pv.getValue());
        if (res != 0) {
          return res;
        }
      }
    }

    if (other != null) {
      return Compare.compare(this.m_shadowUnpacked,
          other.m_shadowUnpacked);
    }

    return ((this != this.m_shadowUnpacked)
        ? Compare.compare(this.m_shadowUnpacked, o) : (-1));
  }
}
