package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
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
   * @param fullRange
   *          hit me with the full range of randomness, please
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public RandomParallelExample(final boolean fullRange,
      final Logger logger) {
    super(fullRange, logger);
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
   * @return {@code true} on success, {@code false} on failure
   */
  final boolean _superCreateExperimentSet(final ExperimentSetContext isc,
      final DimensionSet dims, final InstanceSet insts, final Random r) {
    return super._createExperimentSet(isc, dims, insts, r);
  }

  /** {@inheritDoc} */
  @Override
  final boolean _createExperimentSet(final ExperimentSetContext isc,
      final DimensionSet dims, final InstanceSet insts, final Random r) {
    final _CreateExperiments task;
    ForkJoinPool fjp;

    fjp = new ForkJoinPool(2 + r.nextInt(30));

    task = new _CreateExperiments(isc, dims, insts);
    fjp.submit(task);

    fjp.shutdown();
    try {
      fjp.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
      return task.get().booleanValue();
    } catch (final Throwable error) {
      this._error(error);
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  final boolean _createExperimentSetInner(final ExperimentSetContext isc,
      final DimensionSet dims, final Instance[] is, final Instance must,
      final Map.Entry<String, Integer>[] params,
      final HashSet<HashMap<String, Object>> configs, final Random r) {
    final ArrayList<ForkJoinTask<Boolean>> tasks;
    int z;

    tasks = new ArrayList<>();
    z = 0;
    do {
      tasks.add(new _CreateExperimentOuter(isc, dims, is, must, params,
          configs));
      ++z;
    } while ((z < RandomExample.MAX_TRIALS)
        && ((z <= (this.m_fullRange ? 1 : 2)) || (r.nextInt(15) > 0)));

    if (tasks.size() > 0) {
      for (final ForkJoinTask<Boolean> t : ForkJoinTask.invokeAll(tasks)) {
        try {
          if (!(t.get().booleanValue())) {
            return false;
          }
        } catch (final Throwable error) {
          this._error(error);
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  final boolean _createExperimentInner(final ExperimentContext ec,
      final Instance[] is, final Instance must, final DimensionSet dims,
      final Random r) {
    final ArrayList<ForkJoinTask<Boolean>> tasks;
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
      for (final ForkJoinTask<Boolean> t : ForkJoinTask.invokeAll(tasks)) {
        try {
          if (!(t.get().booleanValue())) {
            return false;
          }
        } catch (final Throwable error) {
          this._error(error);
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  final boolean _createInstanceRunsInner(final InstanceRunsContext irc,
      final DimensionSet dims, final Random r) {
    final ArrayList<ForkJoinTask<Boolean>> tasks;
    int trials;

    tasks = new ArrayList<>();
    trials = RandomExample.MAX_TRIALS;
    do {
      tasks.add(new _CreateRunOuter(irc, dims));
    } while (((--trials) >= 0) && (r.nextInt(10) > 0));

    if (tasks.size() > 0) {
      for (final ForkJoinTask<Boolean> t : ForkJoinTask.invokeAll(tasks)) {
        try {
          if (!(t.get().booleanValue())) {
            return false;
          }
        } catch (final Throwable error) {
          this._error(error);
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * The main routine
   *
   * @param args
   *          the command line arguments
   */
  public static void main(final String[] args) {
    Configuration.setup(args);
    new RandomParallelExample(true, null).run();
  }

  /** an action for creating an experiment */
  private final class _CreateExperiments extends RecursiveTask<Boolean> {
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
    protected final Boolean compute() {
      return Boolean.valueOf(//
          RandomParallelExample.this._superCreateExperimentSet(this.m_isc,
              this.m_dims, this.m_insts, ThreadLocalRandom.current()));
    }
  }

  /** an action for creating an experiment */
  private final class _CreateExperimentOuter
      extends RecursiveTask<Boolean> {
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
    protected final Boolean compute() {
      return Boolean.valueOf(
          RandomParallelExample.this._createExperimentOuter(this.m_isc,
              this.m_dims, this.m_is, this.m_must, this.m_params,
              this.m_configs, ThreadLocalRandom.current()));
    }
  }

  /** an action for creating an experiment */
  private final class _CreateInstanceRunsOuter
      extends RecursiveTask<Boolean> {
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
    protected final Boolean compute() {
      return Boolean.valueOf(//
          RandomParallelExample.this._createInstanceRunsOuter(this.m_ec,
              this.m_inst, this.m_dims, ThreadLocalRandom.current()));
    }
  }

  /** an action for creating an experiment */
  private final class _CreateRunOuter extends RecursiveTask<Boolean> {
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
    protected final Boolean compute() {
      return Boolean.valueOf(//
          RandomParallelExample.this._createRun(this.m_ec, this.m_dims,
              ThreadLocalRandom.current()));
    }
  }

}
