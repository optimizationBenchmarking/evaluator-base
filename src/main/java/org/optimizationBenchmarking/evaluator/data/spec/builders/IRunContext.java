package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;

/** A context for creating runs. */
public interface IRunContext extends IParameterSettingScope {
  /**
   * Add a data point to this run
   *
   * @param point
   *          the data point to be added
   */
  public abstract void addDataPoint(final IDataPoint point);

  /**
   * Decode a string to a data point and add it to the run
   *
   * @param string
   *          the string
   */
  public abstract void addDataPoint(final String string);

  /**
   * Decode a list of numbers to a data point and add it to the run
   *
   * @param numbers
   *          the numbers to add
   */
  public abstract void addDataPoint(final Number... numbers);

  /**
   * Decode a raw object to a data point and add it to the run
   *
   * @param rawObject
   *          the raw object to add
   */
  public abstract void addDataPoint(final Object rawObject);
}
