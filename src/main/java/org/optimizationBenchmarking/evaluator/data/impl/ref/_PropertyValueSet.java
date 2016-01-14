package org.optimizationBenchmarking.evaluator.data.impl.ref;

import java.util.AbstractSet;

import org.optimizationBenchmarking.utils.collections.iterators.ArrayIterator;

/**
 * a concrete property value setting
 */
final class _PropertyValueSet extends AbstractSet<PropertyValue<?>> {

  /** the parameter setting */
  private final _PropertySetting<?, ?> m_ps;

  /**
   * create
   *
   * @param ps
   *          the parameter setting
   */
  _PropertyValueSet(final _PropertySetting<?, ?> ps) {
    super();
    this.m_ps = ps;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public final ArrayIterator<PropertyValue<?>> iterator() {
    return ((ArrayIterator) (this.m_ps.iterator()));
  }

  /** {@inheritDoc} */
  @Override
  public final int size() {
    return this.m_ps.m_values.length;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isEmpty() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean contains(final Object o) {
    return this.m_ps.contains(o);
  }

}
