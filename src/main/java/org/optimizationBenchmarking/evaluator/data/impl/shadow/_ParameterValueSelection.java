package org.optimizationBenchmarking.evaluator.data.impl.shadow;

import java.util.Collection;

import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;

/** a parameter value selection */
final class _ParameterValueSelection
    extends _PropertyValueSelection<IParameter, IParameterValue> {

  /**
   * create the parameter value selection
   *
   * @param parameter
   *          the original parameter
   */
  _ParameterValueSelection(final IParameter parameter) {
    super(parameter);
  }

  /** {@inheritDoc} */
  @Override
  final IParameter _shadow(final IParameter original,
      final Collection<IParameterValue> elements) {
    return new ShadowParameter(null, original, elements);
  }
}
