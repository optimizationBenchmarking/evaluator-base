package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.utils.MemoryUtils;
import org.optimizationBenchmarking.utils.config.Configuration;

/** A class for creating experiment sets */
public abstract class ExperimentSetCreator
    implements Callable<IExperimentSet>, Runnable {

  /** the logger */
  private final Logger m_logger;

  /** the instance */
  private IExperimentSet m_inst;

  /**
   * create
   *
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  protected ExperimentSetCreator(final Logger logger) {
    super();
    this.m_logger = ((logger != null) ? logger//
        : Configuration.getGlobalLogger());
  }

  /**
   * Get the logger
   *
   * @return the logger
   */
  protected final Logger getLogger() {
    return this.m_logger;
  }

  /**
   * Build an experiment set
   *
   * @return the constructed experiment set
   * @throws Exception
   *           if something goes wrong
   */
  protected abstract IExperimentSet buildExperimentSet() throws Exception;

  /**
   * Get the experiment set
   *
   * @return the experiment set
   * @throws Exception
   *           if something goes wrong
   */
  @Override
  public final synchronized IExperimentSet call() throws Exception {
    if (this.m_inst == null) {
      this.m_inst = this.buildExperimentSet();
      MemoryUtils.quickGC();
    }
    return this.m_inst;
  }

  /** Load the experiment data and print the infos */
  @Override
  public final void run() {
    final IExperimentSet es;

    try {
      es = this.call();

      System.out.print("Dimensions: "); //$NON-NLS-1$
      System.out.println(es.getDimensions().getData());

      System.out.print("Instances: "); //$NON-NLS-1$
      System.out.println(es.getInstances().getData());

      System.out.print("Features: "); //$NON-NLS-1$
      System.out.println(es.getFeatures().getData());

      System.out.print("Parameters: "); //$NON-NLS-1$
      System.out.println(es.getParameters().getData());

      System.out.print("Experiments: "); //$NON-NLS-1$
      System.out.println(es.getData());
    } catch (final Throwable error) {
      error.printStackTrace();
    }
  }
}
