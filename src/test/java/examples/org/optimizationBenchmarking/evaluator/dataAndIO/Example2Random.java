package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.Random;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.DimensionContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;

import shared.randomization.RandomUtils;

/** A class for creating experiment sets */
public final class Example2Random extends RandomExample {

  /**
   * create
   *
   * @param fullRange
   *          hit me with the full range of randomness, please
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public Example2Random(final boolean fullRange, final Logger logger) {
    super(fullRange, logger);
  }

  /** {@inheritDoc} */
  @Override
  final boolean _createDimensionSet(final ExperimentSetContext dsc,
      final Random r) {

    do {
      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseLongParser.INSTANCE);
        dc.setType(EDimensionType.ITERATION_FE);
        dc.setDirection(EDimensionDirection.INCREASING_STRICTLY);
      }

      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseLongParser.INSTANCE);
        dc.setType(EDimensionType.ITERATION_SUB_FE);
        dc.setDirection(EDimensionDirection.DECREASING);
      }

      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseLongParser.INSTANCE);
        dc.setType(EDimensionType.RUNTIME_CPU);
        dc.setDirection(EDimensionDirection.INCREASING);
      }

      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseDoubleParser.INSTANCE);
        dc.setType(EDimensionType.RUNTIME_NORMALIZED);
        dc.setDirection(EDimensionDirection.INCREASING);
      }

      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseLongParser.INSTANCE);
        dc.setType(EDimensionType.QUALITY_PROBLEM_DEPENDENT);
        dc.setDirection(EDimensionDirection.DECREASING);
      }

      try (DimensionContext dc = dsc.createDimension()) {
        dc.setName(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
        dc.setParser(LooseDoubleParser.INSTANCE);
        dc.setType(EDimensionType.QUALITY_PROBLEM_INDEPENDENT);
        dc.setDirection(EDimensionDirection.INCREASING);
      }
    } while (r.nextBoolean());

    return true;
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    Configuration.setup(args);
    new Example2Random(true, null).run();
  }
}
