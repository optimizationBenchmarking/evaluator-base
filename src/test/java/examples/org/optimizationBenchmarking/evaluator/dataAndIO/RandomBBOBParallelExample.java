package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.Random;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.DimensionSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.io.impl.bbob.BBOBInput;
import org.optimizationBenchmarking.utils.config.Configuration;

/** A class for creating in parallel sets */
public final class RandomBBOBParallelExample
    extends RandomParallelExample {

  /**
   * create
   *
   * @param fullRange
   *          hit me with the full range of randomness, please
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public RandomBBOBParallelExample(final boolean fullRange,
      final Logger logger) {
    super(true, logger);
  }

  /** {@inheritDoc} */
  @Override
  final void _createDimensionSet(final ExperimentSetContext dsc,
      final Random r) {
    BBOBInput.makeBBOBDimensionSet(dsc);
  }

  /** {@inheritDoc} */
  @Override
  final void _createInstanceSet(final ExperimentSetContext isc,
      final DimensionSet dims, final Random r) {
    BBOBInput.makeBBOBInstanceSet(isc);
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    Configuration.setup(args);
    new RandomBBOBParallelExample(true, null).run();
  }
}
