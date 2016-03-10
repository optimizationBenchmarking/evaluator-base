package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.DimensionSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.Instance;
import org.optimizationBenchmarking.evaluator.data.impl.ref.InstanceRunsContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.InstanceSet;
import org.optimizationBenchmarking.utils.config.Configuration;

/** A class for creating in parallel sets */
public class RandomParallelExample extends RandomExample {

  /**
   * create
   *
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public RandomParallelExample(final Logger logger) {
    super(logger);
  }

  /**
   * create the experiment set
   *
   * @param isc
   *          the context
   * @param dims
   *          the dimensions
   * @param insts
   *          the instances
   * @param r
   *          the randomizer
   */
  final void _superCreateExperimentSet(final ExperimentSetContext isc,
      final DimensionSet dims, final InstanceSet insts, final Random r) {
    super._createExperimentSet(isc, dims, insts, r);
  }

  /** {@inheritDoc} */
  @Override
  final void _createExperimentSet(final ExperimentSetContext isc,
      final DimensionSet dims, final InstanceSet insts, final Random r) {
    ForkJoinPool fjp;

    fjp = new ForkJoinPool(2 + r.nextInt(30));

    fjp.submit(new _CreateExperiments(isc, dims, insts));

    fjp.shutdown();
    try {
      fjp.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (final Throwable t) {
      throw new RuntimeException(//
          "Failed to join the fork-join pool.", //$NON-NLS-1$
          t);
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _createExperimentSetInner(final ExperimentSetContext isc,
      final DimensionSet dims, final Instance[] is, final Instance must,
      final Map.Entry<String, Integer>[] params,
      final HashSet<HashMap<String, Object>> configs, final Random r) {
    final ArrayList<ForkJoinTask<?>> tasks;
    int z;

    z = 100;
    tasks = new ArrayList<>();
    do {
      tasks.add(new _CreateExperimentOuter(isc, dims, is, must, params,
          configs));
    } while ((r.nextInt(15) > 0) && ((--z) >= 0));

    if (tasks.size() > 0) {
      for (final ForkJoinTask<?> t : ForkJoinTask.invokeAll(tasks)) {
        t.join();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _createExperimentInner(final ExperimentContext ec,
      final Instance[] is, final Instance must, final DimensionSet dims,
      final Random r) {
    final ArrayList<ForkJoinTask<?>> tasks;
    Instance pick;
    int i, s;

    tasks = new ArrayList<>();
    s = r.nextInt(is.length);
    for (i = is.length; (--i) >= 0;) {
      pick = is[(i + s) % is.length];
      if ((pick != must) && (i > 0) && (r.nextInt(3) <= 0)) {
        continue;
      }

      tasks.add(new _CreateInstanceRunsOuter(ec, pick, dims));
    }

    if (tasks.size() > 0) {
      for (final ForkJoinTask<?> t : ForkJoinTask.invokeAll(tasks)) {
        t.join();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  final void _createInstanceRunsInner(final InstanceRunsContext irc,
      final DimensionSet dims, final Random r) {
    final ArrayList<ForkJoinTask<?>> tasks;

    tasks = new ArrayList<>();
    do {
      tasks.add(new _CreateRunOuter(irc, dims));
    } while (r.nextInt(10) > 0);

    if (tasks.size() > 0) {
      for (final ForkJoinTask<?> t : ForkJoinTask.invokeAll(tasks)) {
        t.join();
      }
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
    new RandomParallelExample(null).run();
  }

  /** an action for creating an experiment */
  private final class _CreateExperiments extends RecursiveAction {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the context */
    private final ExperimentSetContext m_isc;
    /** the dims */
    private final DimensionSet m_dims;
    /** the instances */
    private final InstanceSet m_insts;

    /**
     * create the experiment set
     *
     * @param isc
     *          the context
     * @param dims
     *          the dimensions
     * @param insts
     *          the instances
     */
    _CreateExperiments(final ExperimentSetContext isc,
        final DimensionSet dims, final InstanceSet insts) {
      super();
      this.m_isc = isc;
      this.m_dims = dims;
      this.m_insts = insts;
    }

    /** {@inheritDoc} */
    @Override
    protected final void compute() {
      RandomParallelExample.this._superCreateExperimentSet(this.m_isc,
          this.m_dims, this.m_insts, ThreadLocalRandom.current());
    }
  }

  /** an action for creating an experiment */
  private final class _CreateExperimentOuter extends RecursiveAction {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the context */
    private final ExperimentSetContext m_isc;
    /** the dimensions */
    private final DimensionSet m_dims;
    /** the instance */
    private final Instance[] m_is;
    /** the necessary instance */
    private final Instance m_must;
    /** the map of parameters */
    private final Map.Entry<String, Integer>[] m_params;
    /** the context */
    private final HashSet<HashMap<String, Object>> m_configs;

    /**
     * create the experiment
     *
     * @param isc
     *          the context
     * @param dims
     *          the dimensions
     * @param is
     *          the instances
     * @param must
     *          the necessary instance
     * @param params
     *          the parameters
     * @param configs
     *          the configurations
     */
    _CreateExperimentOuter(final ExperimentSetContext isc,
        final DimensionSet dims, final Instance[] is, final Instance must,
        final Map.Entry<String, Integer>[] params,
        final HashSet<HashMap<String, Object>> configs) {
      super();
      this.m_isc = isc;
      this.m_dims = dims;
      this.m_is = is;
      this.m_must = must;
      this.m_params = params;
      this.m_configs = configs;
    }

    /** {@inheritDoc} */
    @Override
    protected final void compute() {
      RandomParallelExample.this._createExperimentOuter(this.m_isc,
          this.m_dims, this.m_is, this.m_must, this.m_params,
          this.m_configs, ThreadLocalRandom.current());
    }
  }

  /** an action for creating an experiment */
  private final class _CreateInstanceRunsOuter extends RecursiveAction {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the context */
    private final ExperimentContext m_ec;
    /** the param */
    private final Instance m_inst;
    /** the param */
    private final DimensionSet m_dims;

    /**
     * create a random run
     *
     * @param dims
     *          the dimensions
     * @param ec
     *          the experiment context
     * @param inst
     *          the instance
     */
    _CreateInstanceRunsOuter(final ExperimentContext ec,
        final Instance inst, final DimensionSet dims) {
      super();
      this.m_ec = ec;
      this.m_dims = dims;
      this.m_inst = inst;
    }

    /** {@inheritDoc} */
    @Override
    protected final void compute() {
      RandomParallelExample.this._createInstanceRunsOuter(this.m_ec,
          this.m_inst, this.m_dims, ThreadLocalRandom.current());
    }
  }

  /** an action for creating an experiment */
  private final class _CreateRunOuter extends RecursiveAction {
    /** the serial version uid */
    private static final long serialVersionUID = 1L;
    /** the context */
    private final InstanceRunsContext m_ec;
    /** the param */
    private final DimensionSet m_dims;

    /**
     * create a random run
     *
     * @param dims
     *          the dimensions
     * @param ec
     *          the experiment context
     */
    _CreateRunOuter(final InstanceRunsContext ec,
        final DimensionSet dims) {
      super();
      this.m_ec = ec;
      this.m_dims = dims;
    }

    /** {@inheritDoc} */
    @Override
    protected final void compute() {
      RandomParallelExample.this._createRun(this.m_ec, this.m_dims,
          ThreadLocalRandom.current());
    }
  }

}
