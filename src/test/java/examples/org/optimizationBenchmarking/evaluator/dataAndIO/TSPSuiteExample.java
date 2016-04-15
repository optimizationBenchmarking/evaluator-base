package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.io.impl.tspSuite.TSPSuiteInput;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.io.EArchiveType;

/** A class for creating experiment sets */
public final class TSPSuiteExample extends ExperimentSetCreator {

  /** the resource name */
  public static final String RESOURCE_NAME = "tspSuiteExampleData.zip";//$NON-NLS-1$

  /**
   * create
   *
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public TSPSuiteExample(final Logger logger) {
    super(logger);
  }

  /** {@inheritDoc} */
  @Override
  protected ExperimentSet buildExperimentSet() throws Exception {
    try (final ExperimentSetContext esc = new ExperimentSetContext(
        this.getLogger())) {

      TSPSuiteInput.getInstance().use().setDestination(esc)
          .addArchiveResource(TSPSuiteExample.class,
              TSPSuiteExample.RESOURCE_NAME, EArchiveType.ZIP)
          .create().call();

      return esc.create();
    }
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    Configuration.setup(args);
    new TSPSuiteExample(null).run();
  }
}
