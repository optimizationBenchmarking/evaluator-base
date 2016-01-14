package org.optimizationBenchmarking.evaluator.data.impl.partial;

import java.util.ArrayList;

import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractDimensionSet;
import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractNamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.comparison.Compare;

/** the basic dimension set */
final class _Dimensions extends AbstractDimensionSet {

  /** the internal list view with the dimensions */
  private ArrayListView<IDimension> m_dimensions;
  /** the list of dimensions */
  private final ArrayList<_Dimension> m_dimensionList;
  /** do we need a new dimension? */
  boolean m_needsNew;

  /**
   * create
   *
   * @param owner
   *          the owner
   */
  _Dimensions(final _Experiments owner) {
    super(owner);
    this.m_dimensionList = new ArrayList<>();
    this.m_needsNew = true;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final ArrayListView<IDimension> getData() {
    if (this.m_dimensions == null) {
      this.m_dimensions = ((ArrayListView) (ArrayListView
          .collectionToView(this.m_dimensionList)));
    }
    return this.m_dimensions;
  }

  /**
   * get the dimension
   *
   * @param forceNew
   *          do we need a new one
   * @return the dimension
   */
  final _Dimension _getDimension(final boolean forceNew) {
    final _Dimension dim;
    final int size;

    size = this.m_dimensionList.size();
    if (forceNew || this.m_needsNew || (size <= 0)) {
      dim = new _Dimension(this, size);
      this.m_dimensionList.add(dim);
      this.m_needsNew = false;
      this.m_dimensions = null;
      return dim;
    }
    return this.m_dimensionList.get(size - 1);
  }

  /**
   * Get a dimension of the given name
   *
   * @param name
   *          the name
   * @return the dimension
   */
  final _Dimension _getDimensionForName(final String name) {
    final String useName;
    _Dimension ndim;

    useName = AbstractNamedElement.formatName(name);
    for (final _Dimension dim : this.m_dimensionList) {
      if (Compare.equals(dim.m_name, useName)) {
        return dim;
      }
    }

    ndim = this._getDimension(false);
    if (ndim.m_name != null) {
      ndim = this._getDimension(true);
    }
    ndim.m_name = useName;
    return ndim;
  }
}
