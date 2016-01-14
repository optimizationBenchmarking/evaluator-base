package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.io.impl.bbob.BBOBInput;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.io.EArchiveType;

/** A class for creating experiment sets */
public final class BBOBExample extends ExperimentSetCreator {

  /**
   * create
   *
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public BBOBExample(final Logger logger) {
    super(logger);
  }

  /** {@inheritDoc} */
  @Override
  protected final ExperimentSet buildExperimentSet() throws Exception {

    try (final ExperimentSetContext ec = new ExperimentSetContext(
        this.getLogger())) {

      BBOBInput.getInstance().use().setDestination(ec)
          .addArchiveResource(BBOBExample.class, "bbobExampleData.zip", //$NON-NLS-1$
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
    new BBOBExample(null).run();
  }
}
