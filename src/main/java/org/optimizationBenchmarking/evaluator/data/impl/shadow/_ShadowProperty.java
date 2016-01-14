package org.optimizationBenchmarking.evaluator.data.impl.shadow;

import java.util.Collection;

import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySet;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * A shadow property is basically a shadow of another property with a
 * different owner and potentially different attributes. If all associated
 * data of this element is the same, it will delegate attribute-based
 * computations to that property.
 *
 * @param <OT>
 *          the owner type
 * @param <ST>
 *          the shadow type
 * @param <PVT>
 *          the property value type
 */
abstract class _ShadowProperty<OT extends IPropertySet, ST extends IProperty, PVT extends IPropertyValue>
    extends _ShadowElementSet<OT, ST, PVT> implements IProperty {

  /** the general property value type */
  PVT m_general;

  /**
   * create the shadow property
   *
   * @param owner
   *          the owning property set
   * @param shadow
   *          the property to shadow
   * @param selection
   *          the selection of property value
   */
  _ShadowProperty(final OT owner, final ST shadow,
      final Collection<? extends PVT> selection) {
    super(owner, shadow, selection);
  }

  /** {@inheritDoc} */
  @Override
  void _checkDiscardOrig() {
    if ((this.m_data != null) && (this.m_general != null)) {
      this.m_orig = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final EPrimitiveType getPrimitiveType() {
    return this.m_shadowUnpacked.getPrimitiveType();
  }

  /** {@inheritDoc} */
  @Override
  public PVT findValue(final Object value) {
    PVT general;

    for (final PVT known : this.getData()) {
      if (known == value) {
        return known;
      }
      if (Compare.equals(known.getValue(), value)) {
        return known;
      }
    }

    general = this.getGeneralized();
    if ((value == general)
        || (Compare.equals(general.getValue(), value))) {
      return general;
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  @SuppressWarnings("unchecked")
  public synchronized final PVT getGeneralized() {
    if (this.m_general == null) {
      this.m_general = this._shadow((PVT) (this.m_orig.getGeneralized()));
      this._checkDiscardOrig();
    }
    return this.m_general;
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return this.m_shadowUnpacked.getName();
  }

  /** {@inheritDoc} */
  @Override
  public final String getDescription() {
    return this.m_shadowUnpacked.getDescription();
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    this.m_shadowUnpacked.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    this.m_shadowUnpacked.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return this.m_shadowUnpacked.printShortName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return this.m_shadowUnpacked.printLongName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return this.m_shadowUnpacked.printDescription(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPathComponentSuggestion() {
    return this.m_shadowUnpacked.getPathComponentSuggestion();
  }
}
