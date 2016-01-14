package org.optimizationBenchmarking.evaluator.data.spec.builders;

/** A scope in which parameters can be set. */
public interface IParameterSettingScope
    extends IParameterDeclarationScope {

  /**
   * Set a parameter value.
   *
   * @param parameterName
   *          the parameter name
   * @param parameterDescription
   *          the parameter description
   * @param parameterValue
   *          the parameter value
   * @param parameterValueDescription
   *          the parameter value description
   */
  public abstract void setParameterValue(final String parameterName,
      final String parameterDescription, final Object parameterValue,
      final String parameterValueDescription);

  /**
   * Set a parameter value.
   *
   * @param parameterName
   *          the parameter name
   * @param parameterValue
   *          the parameter value
   * @param parameterValueDescription
   *          the parameter value description
   */
  public abstract void setParameterValue(final String parameterName,
      final Object parameterValue, final String parameterValueDescription);

  /**
   * Set a parameter value.
   *
   * @param parameterName
   *          the parameter name
   * @param parameterValue
   *          the parameter value
   */
  public abstract void setParameterValue(final String parameterName,
      final Object parameterValue);
}
