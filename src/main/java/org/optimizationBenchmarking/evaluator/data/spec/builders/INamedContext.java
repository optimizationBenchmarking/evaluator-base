package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.utils.IScope;

/** A builder for named objects. */
public interface INamedContext extends IScope {

  /**
   * Set the name of this dimension
   *
   * @param name
   *          the name of this dimension
   */
  public abstract void setName(final String name);

  /**
   * Set the description of this object
   *
   * @param description
   *          the description of this object
   */
  public abstract void setDescription(final String description);

  /**
   * Add a string to the description of this object
   *
   * @param description
   *          the description of this object
   */
  public abstract void addDescription(final String description);
}
