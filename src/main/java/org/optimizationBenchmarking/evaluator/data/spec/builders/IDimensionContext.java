package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.evaluator.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.utils.parsers.NumberParser;

/** A builder for dimensions. */
public interface IDimensionContext extends INamedContext {

  /**
   * Set the parser used for the numbers in this dimension
   *
   * @param parser
   *          the parser used for the numbers
   */
  public abstract void setParser(final NumberParser<?> parser);

  /**
   * Try to obtain the parser via reflection. Three formats are allowed:
   * <ol>
   * <li><code>qualified.class.name#publicConstant</code></li>
   * <li><code>qualified.class.name</code> + class has a
   * no-parameter-constructor</li>
   * <li><code>qualified.class.name:lowerBound:upperBound</code> + class
   * has a constructor taking two instances of {@link java.lang.Number}
   * </li>
   * </ol>
   *
   * @param parserDesc
   *          the parser constant
   */
  public abstract void setParser(final String parserDesc);

  /**
   * Try to obtain the parser via reflection.
   *
   * @param parserClass
   *          the parser class: must take two numbers as parameter
   * @param lowerBound
   *          the lower boundary
   * @param upperBound
   *          the upper boundary
   */
  public abstract void setParser(
      final Class<? extends NumberParser<?>> parserClass,
      final Number lowerBound, final Number upperBound);

  /**
   * Try to obtain the parser via reflection.
   *
   * @param parserClass
   *          the parser class: must take two numbers as parameter
   * @param lowerBound
   *          the lower boundary
   * @param upperBound
   *          the upper boundary
   */
  public abstract void setParser(final String parserClass,
      final String lowerBound, final String upperBound);

  /**
   * Set the type of this dimension
   *
   * @param type
   *          the dimension type
   */
  public abstract void setType(final EDimensionType type);

  /**
   * Set the type as constant
   *
   * @param type
   *          the type
   */
  public abstract void setType(final String type);

  /**
   * Set the direction of this dimension
   *
   * @param direction
   *          the dimension direction
   */
  public abstract void setDirection(final EDimensionDirection direction);

  /**
   * Set the direction as constant
   *
   * @param direction
   *          the direction
   */
  public abstract void setDirection(final String direction);
}
