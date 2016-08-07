package org.optimizationBenchmarking.evaluator.io.impl.edi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.evaluator.io.spec.IExperimentSetOutput;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.io.encoding.StreamEncoding;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOJob;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOTool;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.XMLOutputTool;
import org.optimizationBenchmarking.utils.io.xml.XMLBase;
import org.optimizationBenchmarking.utils.io.xml.XMLElement;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.numbers.XMLNumberAppender;

/**
 * A driver for Experiment Data Interchange (EDI) output. EDI is our
 * default, canonical format for storing and exchanging
 * {@link org.optimizationBenchmarking.evaluator.data experiment data
 * structures}.
 */
public final class EDIOutput extends XMLOutputTool<Object>
    implements IExperimentSetOutput<Object> {

  /** create */
  EDIOutput() {
    super();
  }

  /**
   * Get the instance of the {@link EDIOutput}
   *
   * @return the instance of the {@link EDIOutput}
   */
  public static final EDIOutput getInstance() {
    return __EDIOutputLoader.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  protected final String getDefaultPlainOutputFileName() {
    return ("experiments." + //$NON-NLS-1$
        EDI.EDI_XML.getDefaultSuffix());
  }

  /** {@inheritDoc} */
  @Override
  protected final void file(final IOJob job, final Object data,
      final Path file, final StreamEncoding<?, ?> encoding)
          throws Throwable {
    super.file(job, data, file, encoding);
    if (Files.exists(file)) {
      this.addFile(job, file, EDI.EDI_XML);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void xml(final IOJob job, final Object data,
      final XMLBase xmlBase) throws Throwable {
    try (final XMLElement root = xmlBase.element()) {
      root.namespaceSetPrefix(EDI.NAMESPACE_URI, "e"); //$NON-NLS-1$
      EDIOutput.__write(data, root, job, new HashSet<>());
    }
  }

  /**
   * Write some experiment data structure
   *
   * @param data
   *          the data
   * @param root
   *          the root
   * @param job
   *          the job
   * @param described
   *          the set of described things
   */
  @SuppressWarnings("rawtypes")
  private static final void __write(final Object data,
      final XMLElement root, final IOJob job,
      final HashSet<Object> described) {
    final Logger logger;

    if (data == null) {
      throw new IllegalArgumentException(//
          "Data to store to EDI stream must not be null."); //$NON-NLS-1$
    }

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          "Begin writing instance of " + //$NON-NLS-1$
              TextUtils.className(data));
    }
    write: {
      if (data instanceof IExperimentSet) {
        EDIOutput.__writeExperimentSet(((IExperimentSet) data), root, job);
        break write;
      }

      if (data instanceof IDimensionSet) {
        EDIOutput.__writeDimensionSet(((IDimensionSet) data), root, job);
        break write;
      }

      if (data instanceof IInstanceSet) {
        EDIOutput.__writeInstanceSet(((IInstanceSet) data), root, job);
        break write;
      }

      if (data instanceof IExperiment) {
        EDIOutput.__writeExperiment(((IExperiment) data), root, job,
            described);
        break write;
      }

      if (data instanceof Iterable) {
        for (final Object element : ((Iterable) data)) {
          EDIOutput.__write(element, root, job, described);
        }
        break write;
      }

      if (data instanceof Object[]) {
        for (final Object element : ((Object[]) data)) {
          EDIOutput.__write(element, root, job, described);
        }
        break write;
      }

      throw new IllegalArgumentException((((("Cannot deal with input " //$NON-NLS-1$
          + data) + ' ') + '(') + TextUtils.className(data)) + ')');
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          "Finished writing instance of " + //$NON-NLS-1$
              TextUtils.className(data));
    }
  }

  /**
   * Write an experiment set
   *
   * @param experimentSet
   *          the experiment set
   * @param dest
   *          the destination
   * @param job
   *          the job
   */
  private static final void __writeExperimentSet(
      final IExperimentSet experimentSet, final XMLElement dest,
      final IOJob job) {
    final Logger logger;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Beginning to write experiment set " + experimentSet)); //$NON-NLS-1$
    }

    dest.name(EDI.NAMESPACE_URI, EDI.ELEMENT_EXPERIMENT_DATA);
    try (final XMLElement dims = dest.element()) {
      EDIOutput.__writeDimensionSet(experimentSet.getDimensions(), dims,
          job);
    }
    try (final XMLElement insts = dest.element()) {
      EDIOutput.__writeInstanceSet(experimentSet.getInstances(), insts,
          job);
    }
    try (final XMLElement exps = dest.element()) {
      EDIOutput.__writeExperiments(experimentSet.getData(), exps, job);
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Finished writing experiment set " + experimentSet)); //$NON-NLS-1$
    }
  }

  /**
   * Write an dimension set
   *
   * @param dimensionSet
   *          the dimension set
   * @param dest
   *          the destination
   * @param job
   *          the job
   */
  private static final void __writeDimensionSet(
      final IDimensionSet dimensionSet, final XMLElement dest,
      final IOJob job) {
    final Logger logger;
    EPrimitiveType type;
    NumberParser<?> parser;
    String string;
    long integer;
    double floating;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Beginning to write dimension set " + dimensionSet)); //$NON-NLS-1$
    }

    dest.name(EDI.NAMESPACE_URI, EDI.ELEMENT_DIMENSIONS);

    for (final IDimension dimension : dimensionSet.getData()) {
      try (final XMLElement dim = dest.element()) {
        dim.name(EDI.NAMESPACE_URI, EDI.ELEMENT_DIMENSION);

        dim.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_NAME,
            dimension.getName());

        string = dimension.getDescription();
        if (string != null) {
          dim.attributeEncoded(EDI.NAMESPACE_URI,
              EDI.ATTRIBUTE_DESCRIPTION, string);
        }

        dim.attributeRaw(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_DIMENSION_TYPE,
            EDI._getDimensionTypeName(dimension.getDimensionType()));

        dim.attributeRaw(EDI.NAMESPACE_URI,
            EDI.ATTRIBUTE_DIMENSION_DIRECTION,
            EDI._getDimensionDirectionName(dimension.getDirection()));

        type = dimension.getDataType();
        dim.attributeRaw(EDI.NAMESPACE_URI,
            EDI.ATTRIBUTE_DIMENSION_DATA_TYPE, EDI._getDataTypeName(type));

        parser = dimension.getParser();
        switch (type) {
          case BYTE: {
            if ((integer = parser.getLowerBoundLong()) > Byte.MIN_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_LOWER_BOUND,
                  Long.toString(integer));
            }
            if ((integer = parser.getUpperBoundLong()) < Byte.MAX_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_UPPER_BOUND,
                  Long.toString(integer));
            }
            break;
          }
          case SHORT: {
            if ((integer = parser.getLowerBoundLong()) > Short.MIN_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_LOWER_BOUND,
                  Long.toString(integer));
            }
            if ((integer = parser.getUpperBoundLong()) < Short.MAX_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_UPPER_BOUND,
                  Long.toString(integer));
            }
            break;
          }
          case INT: {
            if ((integer = parser
                .getLowerBoundLong()) > Integer.MIN_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_LOWER_BOUND,
                  Long.toString(integer));
            }
            if ((integer = parser
                .getUpperBoundLong()) < Integer.MAX_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_UPPER_BOUND,
                  Long.toString(integer));
            }
            break;
          }
          case LONG: {
            if ((integer = parser.getLowerBoundLong()) > Long.MIN_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_LOWER_BOUND,
                  Long.toString(integer));
            }
            if ((integer = parser.getUpperBoundLong()) < Long.MAX_VALUE) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_INTEGER_UPPER_BOUND,
                  Long.toString(integer));
            }
            break;
          }
          case FLOAT:
          case DOUBLE: {
            if ((floating = parser
                .getLowerBoundDouble()) > Double.NEGATIVE_INFINITY) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_FLOAT_LOWER_BOUND,
                  XMLNumberAppender.INSTANCE.toString(floating,
                      ETextCase.IN_SENTENCE));
            }
            if ((floating = parser
                .getUpperBoundDouble()) < Double.POSITIVE_INFINITY) {
              dim.attributeRaw(EDI.NAMESPACE_URI,
                  EDI.ATTRIBUTE_FLOAT_UPPER_BOUND,
                  XMLNumberAppender.INSTANCE.toString(floating,
                      ETextCase.IN_SENTENCE));
            }
            break;
          }
          default: {
            throw new IllegalArgumentException(type + //
                " cannot be the data type of dimension " //$NON-NLS-1$
                + dimension);
          }
        }

      }
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Finished to write dimension set " + dimensionSet)); //$NON-NLS-1$
    }
  }

  /**
   * Write an instance set
   *
   * @param instances
   *          the instance set
   * @param dest
   *          the destination
   * @param job
   *          the job
   */
  @SuppressWarnings({ "incomplete-switch", "null" })
  private static final void __writeInstanceSet(
      final IInstanceSet instances, final XMLElement dest,
      final IOJob job) {
    final HashSet<Object> hashSet;
    final ArrayListView<? extends IDimension> dimensions;
    final Logger logger;
    String string;
    IFeature feature;
    Number lower, upper;
    NumberParser<?> parser;
    EPrimitiveType dataType;
    boolean x, y, isInt;
    double d;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Beginning to write instance set " + instances)); //$NON-NLS-1$
    }

    dest.name(EDI.NAMESPACE_URI, EDI.ELEMENT_INSTANCES);

    hashSet = new HashSet<>();
    dimensions = instances.getOwner().getDimensions().getData();

    for (final IInstance i : instances.getData()) {

      try (XMLElement inst = dest.element()) {
        inst.name(EDI.NAMESPACE_URI, EDI.ELEMENT_INSTANCE);

        inst.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_NAME,
            i.getName());

        string = i.getDescription();
        if (string != null) {
          inst.attributeEncoded(EDI.NAMESPACE_URI,
              EDI.ATTRIBUTE_DESCRIPTION, string);
        }

        for (final IFeatureValue featureValue : i.getFeatureSetting()) {
          try (final XMLElement feat = inst.element()) {
            feat.name(EDI.NAMESPACE_URI, EDI.ELEMENT_FEATURE);

            feature = featureValue.getOwner();

            feat.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_NAME,
                feature.getName());
            string = feature.getDescription();
            if (string != null) {
              if (hashSet.add(feature)) {
                feat.attributeEncoded(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_FEATURE_DESCRIPTION, string);
              }
            }

            feat.attributeEncoded(EDI.NAMESPACE_URI,
                EDI.ATTRIBUTE_FEATURE_VALUE, featureValue.getName());
            string = featureValue.getDescription();
            if (string != null) {
              if (hashSet.add(featureValue)) {
                feat.attributeEncoded(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_FEATURE_VALUE_DESCRIPTION, string);
              }
            }

          }
        }

        dimLoop: for (final IDimension dimension : dimensions) {
          parser = dimension.getParser();
          dataType = dimension.getDataType();
          lower = i.getLowerBound(dimension);
          upper = i.getUpperBound(dimension);

          isInt = true;
          x = (lower != null);
          y = (upper != null);

          if (!(x || y)) {
            continue dimLoop;
          }

          // We need to check whether a dimension bound is actually
          // relevant and should be stored. Sometimes, the available data
          // may be incomplete (e.g., when generated via the
          // PartialExperimentSetBuilder). Therefore, the check is a bit
          // more complex.
          if (dataType != null) {

            isInt = dataType.isInteger();

            if (parser != null) {
              if (isInt) {
                if (x) {
                  x = (lower.longValue() > parser.getLowerBoundLong());
                }
                if (y) {
                  y = (upper.longValue() < parser.getUpperBoundLong());
                }
              } else {
                if (x) {
                  x = (lower.doubleValue() > parser.getLowerBoundDouble());
                }
                if (y) {
                  y = (upper.doubleValue() < parser.getUpperBoundDouble());
                }
              }

              if (!(x || y)) {
                continue dimLoop;
              }
            }

            switch (dataType) {
              case BYTE: {
                if (x) {
                  x = (lower.byteValue() > Byte.MIN_VALUE);
                }
                if (y) {
                  y = (upper.byteValue() < Byte.MAX_VALUE);
                }
                break;
              }
              case SHORT: {
                if (x) {
                  x = (lower.shortValue() > Short.MIN_VALUE);
                }
                if (y) {
                  y = (upper.shortValue() < Short.MAX_VALUE);
                }
                break;
              }
              case INT: {
                if (x) {
                  x = (lower.intValue() > Integer.MIN_VALUE);
                }
                if (y) {
                  y = (upper.intValue() < Integer.MAX_VALUE);
                }
                break;
              }
              case LONG: {
                if (x) {
                  x = (lower.longValue() > Long.MIN_VALUE);
                }
                if (y) {
                  y = (upper.longValue() < Integer.MAX_VALUE);
                }
                break;
              }
              case FLOAT: {
                if (x) {
                  x = (lower.floatValue() > Float.NEGATIVE_INFINITY);
                }
                if (y) {
                  y = (upper.floatValue() < Float.POSITIVE_INFINITY);
                }
                isInt = false;
                break;
              }
              case DOUBLE: {
                if (x) {
                  x = (lower.doubleValue() > Double.NEGATIVE_INFINITY);
                }
                if (y) {
                  y = (upper.doubleValue() < Double.POSITIVE_INFINITY);
                }
                isInt = false;
                break;
              }
            }

            if (!(x || y)) {
              continue dimLoop;
            }
          } else {

            // Check based finiteness
            if (x) {
              d = lower.doubleValue();
              if ((d <= Double.NEGATIVE_INFINITY) || (d != d)) {
                x = false;
              }
            }

            if (y) {
              d = upper.doubleValue();
              if ((d >= Double.POSITIVE_INFINITY) || (d != d)) {
                y = false;
              }
            }
          }

          // done checking bounds
          if (!(x || y)) {
            continue dimLoop;
          }

          try (final XMLElement dim = inst.element()) {
            dim.name(EDI.NAMESPACE_URI, EDI.ELEMENT_BOUNDS);
            dim.attributeEncoded(EDI.NAMESPACE_URI,
                EDI.ATTRIBUTE_DIMENSION, dimension.getName());

            if (isInt) {
              if (x) {
                dim.attributeRaw(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_INTEGER_LOWER_BOUND,
                    Long.toString(lower.longValue()));
              }
              if (y) {
                dim.attributeRaw(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_INTEGER_UPPER_BOUND,
                    Long.toString(upper.longValue()));
              }
            } else {
              if (x) {
                dim.attributeRaw(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_FLOAT_LOWER_BOUND,
                    XMLNumberAppender.INSTANCE.toString(
                        lower.doubleValue(), ETextCase.IN_SENTENCE));
              }
              if (y) {
                dim.attributeRaw(EDI.NAMESPACE_URI,
                    EDI.ATTRIBUTE_FLOAT_UPPER_BOUND,
                    XMLNumberAppender.INSTANCE.toString(
                        upper.doubleValue(), ETextCase.IN_SENTENCE));
              }
            }
          }
        }
      }
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Finished to write instance set " + instances)); //$NON-NLS-1$
    }
  }

  /**
   * Write the experiments
   *
   * @param experiment
   *          the experiment
   * @param dest
   *          the dest
   * @param job
   *          the job
   * @param described
   *          the described elements
   */
  private static final void __writeExperiment(final IExperiment experiment,
      final XMLElement dest, final IOJob job,
      final HashSet<Object> described) {
    final boolean[] isInt;
    final int dimensionSize;
    final ArrayListView<? extends IDimension> dimensions;
    final Logger logger;
    String string;
    IParameter parameter;
    int i;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Beginning to write experiment " + experiment)); //$NON-NLS-1$
    }

    dest.name(EDI.NAMESPACE_URI, EDI.ELEMENT_EXPERIMENT);

    dest.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_NAME,
        experiment.getName());

    string = experiment.getDescription();
    if (string != null) {
      dest.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_DESCRIPTION,
          string);
    }

    for (final IParameterValue parameterValue : experiment
        .getParameterSetting()) {
      if (parameterValue.isUnspecified()) {
        continue;
      }
      try (final XMLElement point = dest.element()) {
        point.name(EDI.NAMESPACE_URI, EDI.ELEMENT_PARAMETER);
        parameter = parameterValue.getOwner();

        point.attributeEncoded(EDI.NAMESPACE_URI, EDI.ATTRIBUTE_NAME,
            parameter.getName());
        string = parameter.getDescription();
        if (string != null) {
          if (described.add(parameter)) {
            point.attributeEncoded(EDI.NAMESPACE_URI,
                EDI.ATTRIBUTE_PARAMETER_DESCRIPTION, string);
          }
        }

        point.attributeEncoded(EDI.NAMESPACE_URI,
            EDI.ATTRIBUTE_PARAMETER_VALUE, parameterValue.getName());
        string = parameterValue.getDescription();
        if (string != null) {
          if (described.add(parameterValue)) {
            point.attributeEncoded(EDI.NAMESPACE_URI,
                EDI.ATTRIBUTE_PARAMETER_VALUE_DESCRIPTION, string);
          }
        }

      }
    }

    dimensions = experiment.getOwner().getDimensions().getData();
    dimensionSize = dimensions.size();
    isInt = new boolean[dimensionSize];
    for (i = dimensionSize; (--i) >= 0;) {
      isInt[i] = dimensions.get(i).getDataType().isInteger();
    }

    for (final IInstanceRuns instanceRuns : experiment.getData()) {
      try (final XMLElement instanceRunsXML = dest.element()) {
        instanceRunsXML.name(EDI.NAMESPACE_URI, EDI.ELEMENT_INSTANCE_RUNS);

        instanceRunsXML.attributeEncoded(EDI.NAMESPACE_URI,
            EDI.ATTRIBUTE_INSTANCE, instanceRuns.getInstance().getName());
        for (final IRun run : instanceRuns.getData()) {
          try (final XMLElement runXML = instanceRunsXML.element()) {
            runXML.name(EDI.NAMESPACE_URI, EDI.ELEMENT_RUN);

            for (final IDataPoint point : run.getData()) {
              try (final XMLElement pointXML = runXML.element()) {
                pointXML.name(EDI.NAMESPACE_URI, EDI.ELEMENT_POINT);
                for (i = 0; i < dimensionSize; i++) {
                  if (isInt[i]) {
                    try (final XMLElement valueXML = pointXML.element()) {
                      valueXML.name(EDI.NAMESPACE_URI, EDI.ELEMENT_INT);
                      valueXML.textRaw().append(point.getLong(i));
                    }
                  } else {
                    try (final XMLElement valueXML = pointXML.element()) {
                      valueXML.name(EDI.NAMESPACE_URI, EDI.ELEMENT_FLOAT);
                      valueXML.textRaw()
                          .append(XMLNumberAppender.INSTANCE.toString(
                              point.getDouble(i), ETextCase.IN_SENTENCE));

                    }
                  }

                }

              }
            }
          }
        }

      }
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Finished writing experiment " + experiment)); //$NON-NLS-1$
    }
  }

  /**
   * Write the experiments
   *
   * @param es
   *          the experiments
   * @param dest
   *          the dest
   * @param job
   *          the job
   */
  private static final void __writeExperiments(
      final ArrayListView<? extends IExperiment> es, final XMLElement dest,
      final IOJob job) {
    final HashSet<Object> described;
    final Logger logger;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Beginning to write experiments.")); //$NON-NLS-1$
    }

    dest.name(EDI.NAMESPACE_URI, EDI.ELEMENT_EXPERIMENTS);

    described = new HashSet<>();
    for (final IExperiment e : es) {
      try (final XMLElement exp = dest.element()) {
        EDIOutput.__writeExperiment(e, exp, job, described);
      }
    }

    if ((logger != null) && (logger.isLoggable(IOTool.FINE_LOG_LEVEL))) {
      logger.log(IOTool.FINE_LOG_LEVEL, //
          ("Finished to write experiments.")); //$NON-NLS-1$
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "EDI Experiment Data Output"; //$NON-NLS-1$
  }

  /** the loader */
  private static final class __EDIOutputLoader {
    /** create */
    static final EDIOutput INSTANCE = new EDIOutput();
  }
}
