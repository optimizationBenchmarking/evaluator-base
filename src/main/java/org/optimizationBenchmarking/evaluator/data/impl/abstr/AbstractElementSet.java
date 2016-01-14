package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.DataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IElementSet;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IElementSet}
 * interface.
 */
public abstract class AbstractElementSet extends DataElement
    implements IElementSet {

  /** create */
  protected AbstractElementSet() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public IDataElement getOwner() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public ArrayListView<?> getData() {
    return ArraySetView.EMPTY_SET_VIEW;
  }
}
