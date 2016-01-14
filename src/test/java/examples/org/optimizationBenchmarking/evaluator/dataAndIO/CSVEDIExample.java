package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.io.impl.csvedi.CSVEDIInput;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.io.EArchiveType;

/** A class for using some data stored in CSV+EDI format */
public final class CSVEDIExample extends ExperimentSetCreator {

  /**
   * create
   *
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public CSVEDIExample(final Logger logger) {
    super(logger);
  }

  /** {@inheritDoc} */
  @Override
  protected final ExperimentSet buildExperimentSet() throws Exception {

    try (final ExperimentSetContext ec = new ExperimentSetContext(
        this.getLogger())) {

      CSVEDIInput.getInstance().use().setDestination(ec)
          .addArchiveResource(CSVEDIExample.class, "csvEdiExampleData.zip", //$NON-NLS-1$
              EArchiveType.ZIP)
          .create().call();
      return ec.create();
    }
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static final void main(final String[] args) {
    Configuration.setup(args);
    new CSVEDIExample(null).run();
  }
}
