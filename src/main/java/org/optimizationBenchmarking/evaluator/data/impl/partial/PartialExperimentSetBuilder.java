package org.optimizationBenchmarking.evaluator.data.impl.partial;

import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractDimension;
import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractNamedElement;
import org.optimizationBenchmarking.evaluator.data.impl.flat.AbstractFlatExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.utils.parsers.AnyNumberParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;

/**
 * A builder which allows for the creation of partial data structures of
 * the experiment data api. You can obtain handles to the data elements at
 * any time during the build process. Of course, they might still change.
 * Or be empty.
 */
public final class PartialExperimentSetBuilder
    extends AbstractFlatExperimentSetContext {

  /** the internal experiment set */
  private final _Experiments m_set;

  /** create */
  public PartialExperimentSetBuilder() {
    super();
    this.m_set = new _Experiments();
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionBegin(final boolean forceNew) {
    this.m_set.getDimensions()._getDimension(forceNew);
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionEnd() {
    this.m_set.getDimensions().m_needsNew = true;
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionSetName(final String name) {
    this.m_set.getDimensions()._getDimension(false).m_name = //
    AbstractNamedElement.formatName(name);
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionSetDescription(final String description) {
    this.m_set.getDimensions()._getDimension(false).m_description = //
    AbstractNamedElement.formatDescription(description);
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionAddDescription(final String description) {
    final _Dimension dim;

    dim = this.m_set.getDimensions()._getDimension(false);
    dim.m_description = AbstractNamedElement
        .mergeDescriptions(dim.m_description, description);
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionSetDirection(
      final EDimensionDirection direction) {
    AbstractDimension.validateDirection(direction);
    this.m_set.getDimensions()
        ._getDimension(false).m_dimensionDirection = direction;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public final void dimensionSetParser(final NumberParser<?> parser) {
    final _Dimension dim;

    dim = this.m_set.getDimensions()._getDimension(false);
    dim.m_dataType = AbstractDimension.validateParser(parser);
    dim.m_parser = ((NumberParser) parser);
  }

  /** {@inheritDoc} */
  @Override
  public final void dimensionSetType(final EDimensionType type) {
    AbstractDimension.validateType(type);
    this.m_set.getDimensions()._getDimension(false).m_dimensionType = type;
  }

  /** {@inheritDoc} */
  @Override
  public final IDimensionSet getDimensionSet() {
    return this.m_set.getDimensions();
  }

  /** {@inheritDoc} */
  @Override
  public final IFeatureSet getFeatureSet() {
    return this.m_set.getFeatures();
  }

  /** {@inheritDoc} */
  @Override
  public final IInstanceSet getInstanceSet() {
    return this.m_set.getInstances();
  }

  /** {@inheritDoc} */
  @Override
  public final IExperimentSet getExperimentSet() {
    return this.m_set;
  }

  /** {@inheritDoc} */
  @Override
  public final void featureDeclare(final String name, final String desc) {
    final _Feature feature;

    feature = this.m_set.getFeatures()._getFeatureForName(name);
    if (desc != null) {
      feature.m_description = AbstractNamedElement
          .mergeDescriptions(feature.m_description, desc);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void parameterDeclare(final String name,
      final String desc) {
    final _Parameter parameter;

    parameter = this.m_set.getParameters()._getParameterForName(name);
    if (desc != null) {
      parameter.m_description = AbstractNamedElement
          .mergeDescriptions(parameter.m_description, desc);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceBegin(final boolean forceNew) {
    this.m_set.getInstances()._getInstance(forceNew);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceEnd() {
    this.m_set.getInstances().m_needsNew = true;
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetName(final String name) {
    this.m_set.getInstances()._getInstance(false).m_name = //
    AbstractNamedElement.formatName(name);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetDescription(final String description) {
    this.m_set.getInstances()._getInstance(false).m_description = //
    AbstractNamedElement.formatDescription(description);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceAddDescription(final String description) {
    final _Instance instance;
    instance = this.m_set.getInstances()._getInstance(false);
    instance.m_description = AbstractNamedElement
        .mergeDescriptions(instance.m_description, description);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetFeatureValue(final String featureName,
      final String featureDescription, final Object featureValue,
      final String featureValueDescription) {
    final _Feature feature;
    final _FeatureValue value;

    feature = this.m_set.getFeatures()._getFeatureForName(featureName);
    if (featureDescription != null) {
      feature.m_description = AbstractNamedElement
          .mergeDescriptions(feature.m_description, featureDescription);
    }
    value = feature._getValue(featureValue);
    if (featureValueDescription != null) {
      value.m_description = AbstractNamedElement
          .mergeDescriptions(value.m_description, featureValueDescription);
    }
    this.m_set.getInstances()._getInstance(false).getFeatureSetting()
        ._setFeatureValue(value);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetLowerBound(final IDimension dim,
      final Number bound) {
    this.m_set.getInstances()._getInstance(false)._setLower(//
        dim.getName(), bound);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetLowerBound(final Object dim,
      final Object bound) {
    final Number nbound;

    nbound = AnyNumberParser.INSTANCE.parseObject(bound);

    if (dim instanceof IDimension) {
      this.instanceSetLowerBound(((IDimension) dim), nbound);
      return;
    }
    this.instanceSetLowerBound(this.m_set.getDimensions()
        ._getDimensionForName(String.valueOf(dim)), nbound);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetUpperBound(final IDimension dim,
      final Number bound) {
    this.m_set.getInstances()._getInstance(false)._setUpper(//
        dim.getName(), bound);
  }

  /** {@inheritDoc} */
  @Override
  public final void instanceSetUpperBound(final Object dim,
      final Object bound) {
    final Number nbound;

    nbound = AnyNumberParser.INSTANCE.parseObject(bound);
    if (dim instanceof IDimension) {
      this.instanceSetUpperBound(((IDimension) dim), nbound);
      return;
    }
    this.instanceSetUpperBound(this.m_set.getDimensions()
        ._getDimensionForName(String.valueOf(dim)), nbound);
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentBegin(final boolean forceNew) {
    this.m_set._getExperiment(forceNew);
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentEnd() {
    this.m_set.m_needsNew = true;
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentSetName(final String name) {
    this.m_set._getExperiment(false).m_name = //
    AbstractNamedElement.formatName(name);
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentSetDescription(final String description) {
    this.m_set._getExperiment(false).m_description = //
    AbstractNamedElement.formatDescription(description);
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentAddDescription(final String description) {
    final _Experiment experiment;
    experiment = this.m_set._getExperiment(false);
    experiment.m_description = AbstractNamedElement
        .mergeDescriptions(experiment.m_description, description);
  }

  /** {@inheritDoc} */
  @Override
  public final void experimentSetParameterValue(final String parameterName,
      final String parameterDescription, final Object parameterValue,
      final String parameterValueDescription) {
    final _Parameter parameter;
    final _ParameterValue value;

    parameter = this.m_set.getParameters()
        ._getParameterForName(parameterName);
    if (parameterDescription != null) {
      parameter.m_description = AbstractNamedElement.mergeDescriptions(
          parameter.m_description, parameterDescription);
    }
    value = parameter._getValue(parameterValue);
    if (parameterValueDescription != null) {
      value.m_description = AbstractNamedElement.mergeDescriptions(
          value.m_description, parameterValueDescription);
    }
    this.m_set._getExperiment(false).getParameterSetting()
        ._setParameterValue(value);
  }

  /** {@inheritDoc} */
  @Override
  public final void flush() {
    this.m_set._flush();
  }
}
