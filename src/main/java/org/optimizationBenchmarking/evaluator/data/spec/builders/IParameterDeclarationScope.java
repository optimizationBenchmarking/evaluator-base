package org.optimizationBenchmarking.evaluator.data.spec.builders;

import org.optimizationBenchmarking.utils.IScope;

/** A scope in which parameters can be decleared. */
public interface IParameterDeclarationScope extends IScope {
  /**
   * Define a parameter with a given name and description
   *
   * @param name
   *          the parameter name
   * @param desc
   *          the parameter's description
   */
  public abstract void declareParameter(final String name,
      final String desc);
}
