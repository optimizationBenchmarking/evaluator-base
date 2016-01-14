package org.optimizationBenchmarking.evaluator.data.spec;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;

/**
 * A set of data elements.
 */
public interface IElementSet extends IDataElement {

  /**
   * Get the data elements of this data set
   *
   * @return the data elements of this data set
   */
  public abstract ArrayListView<?> getData();

}
