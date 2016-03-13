package examples.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.impl.ref.DataPoint;
import org.optimizationBenchmarking.evaluator.data.impl.ref.Dimension;
import org.optimizationBenchmarking.evaluator.data.impl.ref.DimensionContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.DimensionSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.ExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.Instance;
import org.optimizationBenchmarking.evaluator.data.impl.ref.InstanceContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.InstanceRunsContext;
import org.optimizationBenchmarking.evaluator.data.impl.ref.InstanceSet;
import org.optimizationBenchmarking.evaluator.data.impl.ref.RunContext;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.parsers.LooseByteParser;
import org.optimizationBenchmarking.utils.parsers.LooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.LooseFloatParser;
import org.optimizationBenchmarking.utils.parsers.LooseIntParser;
import org.optimizationBenchmarking.utils.parsers.LooseLongParser;
import org.optimizationBenchmarking.utils.parsers.LooseShortParser;
import org.optimizationBenchmarking.utils.parsers.NumberParser;
import org.optimizationBenchmarking.utils.reflection.EPrimitiveType;

import shared.randomization.NumberRandomization;
import shared.randomization.RandomUtils;

/** A class for creating experiment sets */
public class RandomExample extends ExperimentSetCreator {

  /** naming */
  static final String NAMING = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

  /** the maximum trials */
  static final int MAX_TRIALS = 100;

  /** the parsers */
  private static final NumberParser<?>[] PARSERS = new NumberParser[] {
      LooseByteParser.INSTANCE, LooseShortParser.INSTANCE,
      LooseIntParser.INSTANCE, LooseLongParser.INSTANCE,
      LooseFloatParser.INSTANCE, LooseDoubleParser.INSTANCE };

  /** the name counter */
  final AtomicLong m_v;

  /**
   * be as random as possible ({@code true}) or create somewhat sane data (
   * {@code false})
   */
  final boolean m_fullRange;

  /**
   * create
   *
   * @param fullRange
   *          hit me with the full range of randomness, please
   * @param logger
   *          the logger, or {@code null} to use the global logger
   */
  public RandomExample(final boolean fullRange, final Logger logger) {
    super(logger);
    this.m_fullRange = fullRange;
    this.m_v = new AtomicLong();
  }

  /** {@inheritDoc} */
  @Override
  protected ExperimentSet buildExperimentSet() {
    final Random r;
    final Logger logger;
    int trials;

    r = new Random();
    this.m_v.set(r.nextLong());
    logger = this.getLogger();

    outer: for (trials = RandomExample.MAX_TRIALS; (--trials) >= 0;) {
      try (final ExperimentSetContext esb = new ExperimentSetContext(
          logger)) {

        if (!(this._createDimensionSet(esb, r))) {
          continue outer;
        }

        if (!(this._createInstanceSet(esb, esb.getDimensionSet(), r))) {
          continue outer;
        }

        if (!(this._createExperimentSet(esb, esb.getDimensionSet(),
            esb.getInstanceSet(), r))) {
          continue outer;
        }

        return esb.create();
      } catch (final Throwable error) {
        this._error(error);
      }
    }

    return new Example1(logger).buildExperimentSet();
  }

  /**
   * create the dimension set
   *
   * @param dsc
   *          the context
   * @param r
   *          the randomizer
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createDimensionSet(final ExperimentSetContext dsc,
      final Random r) {
    int size, trials;

    size = 0;
    trials = RandomExample.MAX_TRIALS;
    do {
      if (!(this._createDimension(dsc, r))) {
        return false;
      }
      ++size;
    } while (((--trials) >= 0)
        && ((size < (this.m_fullRange ? 1 : 2)) || (r.nextBoolean())));

    return (size >= (this.m_fullRange ? 1 : 2));
  }

  /**
   * create the dimension set
   *
   * @param dsc
   *          the context
   * @param r
   *          the randomizer
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createDimension(final ExperimentSetContext dsc,
      final Random r) {

    try (DimensionContext dc = dsc.createDimension()) {

      dc.setName(RandomUtils.longToString(RandomExample.NAMING,
          this.m_v.incrementAndGet()));
      if (r.nextBoolean()) {
        dc.setDescription(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
      }
      dc.setParser(
          RandomExample.PARSERS[r.nextInt(RandomExample.PARSERS.length)]);
      dc.setType(EDimensionType.INSTANCES
          .get(r.nextInt(EDimensionType.INSTANCES.size())));
      dc.setDirection(EDimensionDirection.INSTANCES
          .get(r.nextInt(EDimensionDirection.INSTANCES.size())));

    }
    return true;
  }

  /**
   * create a property map
   *
   * @param r
   *          the randomizer
   * @return the property map
   */
  private final Map.Entry<String, Integer>[] __createProperties(
      final Random r) {
    final HashMap<String, Integer> features;

    features = new HashMap<>();
    do {
      features.put(RandomUtils.longToString(RandomExample.NAMING,
          this.m_v.incrementAndGet()), Integer.valueOf(r.nextInt(11)));
    } while (r.nextInt(4) > 0);

    return features.entrySet().toArray(new Map.Entry[features.size()]);
  }

  /**
   * create a property map
   *
   * @param map
   *          the map
   * @param canOmit
   *          can we skip the value?
   * @param r
   *          the randomizer
   * @return the property map
   */
  private final HashMap<String, Object> __createValues(
      final Map.Entry<String, Integer>[] map, final boolean canOmit,
      final Random r) {
    final HashMap<String, Object> res;
    Map.Entry<String, Integer> entry;
    int idx, start;
    Object o;
    String s;

    res = new HashMap<>();
    start = r.nextInt(map.length);
    for (idx = map.length; (--idx) >= 0;) {
      if (canOmit && (idx > 0) && (r.nextInt(3) <= 0)) {
        continue;
      }
      entry = map[((idx + start) % map.length)];

      switch (entry.getValue().intValue()) {
        case 0: {
          o = Boolean.valueOf(r.nextBoolean());
          break;
        }
        case 1: {
          o = Byte.valueOf((byte) (r.nextInt(256) - 128));
          break;
        }
        case 2: {
          o = Short.valueOf((short) (r.nextInt(65536) - 32768));
          break;
        }
        case 3: {
          o = Integer.valueOf(r.nextInt());
          break;
        }
        case 4: {
          o = Long.valueOf(r.nextLong());
          break;
        }
          // case 5: {
          // o = Float.valueOf((float) (r.nextDouble()));
          // break;
          // }
        case 5: {
          o = Double.valueOf(r.nextDouble());
          break;
        }
        case 6: {
          o = Character.valueOf(RandomExample.NAMING
              .charAt(r.nextInt(RandomExample.NAMING.length())));
          break;
        }
        case 7: {
          do {
            o = s = RandomUtils.longToString(RandomExample.NAMING,
                r.nextLong());
          } while (("true".equalsIgnoreCase(s)) || //$NON-NLS-1$
              ("false".equalsIgnoreCase(s))); //$NON-NLS-1$
          break;
        }
        case 8: {
          o = Integer.valueOf(r.nextInt(4));
          break;
        }
        case 9: {
          o = Double.valueOf(((r.nextInt(4) + 0.3d) * 37d)
              / ((r.nextInt(3) - 0.7d) * 71d));
          break;
        }
        default: {
          do {
            o = s = RandomUtils.longToString(RandomExample.NAMING,
                r.nextInt(10));
          } while (("true".equalsIgnoreCase(s)) || //$NON-NLS-1$
              ("false".equalsIgnoreCase(s))); //$NON-NLS-1$
          break;
        }
      }

      res.put(entry.getKey(), o);
    }

    return res;
  }

  /**
   * create the instance set
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param isc
   *          the context
   * @param features
   *          the features
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createInstance(final ExperimentSetContext isc,
      final DimensionSet dims, final Map.Entry<String, Integer>[] features,
      final Random r) {

    try (final InstanceContext ic = isc.createInstance()) {
      ic.setName(RandomUtils.longToString(RandomExample.NAMING,
          this.m_v.incrementAndGet()));
      if (r.nextBoolean()) {
        ic.setDescription(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
      }

      for (final Map.Entry<String, Object> e : this
          .__createValues(features, false, r).entrySet()) {
        ic.setFeatureValue(e.getKey(), e.getValue());
      }
    }

    return true;
  }

  /**
   * create the instance set
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param isc
   *          the context
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createInstanceSet(final ExperimentSetContext isc,
      final DimensionSet dims, final Random r) {
    final Map.Entry<String, Integer>[] features;
    int size, trials;

    features = this.__createProperties(r);
    if ((features == null) || (features.length <= 0)) {
      return false;
    }

    trials = RandomExample.MAX_TRIALS;
    size = 0;
    do {
      if (!(this._createInstance(isc, dims, features, r))) {
        return false;
      }
      ++size;
    } while (((--trials) >= 0)
        && ((size <= (this.m_fullRange ? 0 : 2)) || r.nextBoolean()));
    return (size > (this.m_fullRange ? 0 : 2));
  }

  /**
   * create a random data point
   *
   * @param r
   *          the randomizer
   * @param dimss
   *          the dimensions
   * @return the random data point
   */
  DataPoint _createDataPoint(final DimensionSet dimss, final Random r) {
    final ArraySetView<Dimension> dims;
    String s;
    DataPoint p1;
    ArrayList<Number> lst;
    NumberParser<Number> np;
    Number n;

    dims = dimss.getData();

    s = ""; //$NON-NLS-1$
    if (r.nextBoolean()) {
      lst = new ArrayList<>();
    } else {
      lst = null;
    }

    for (final Dimension d : dims) {
      if (lst == null) {
        s += ' ';
      }

      np = d.getParser();
      n = NumberRandomization.forNumberParser(np).randomValue(np,
          this.m_fullRange, r);
      if (n == null) {
        return null;
      }
      if (lst != null) {
        lst.add(n);
      } else {
        s += n.toString();
      }
    }

    if (lst == null) {
      p1 = dimss.getDataFactory().parseString(s);
    } else {
      p1 = dimss.getDataFactory()
          .parseNumbers(lst.toArray(new Number[lst.size()]));
    }

    if (p1 == null) {
      throw new IllegalStateException("Data point cannot be null."); //$NON-NLS-1$
    }
    return p1;
  }

  /**
   * create a random data point
   *
   * @param r
   *          the randomizer
   * @param dimss
   *          the dimensions
   * @param lower
   *          the lower bound
   * @param upper
   *          the upper bound
   * @return the random data point
   */
  DataPoint _createDataPointBetween(final DimensionSet dimss,
      final DataPoint lower, final DataPoint upper, final Random r) {
    final ArraySetView<Dimension> dims;
    String s;
    DataPoint p1;
    ArrayList<Number> lst;
    EPrimitiveType type;
    int dimIndex;
    boolean isStrict;
    Number n;

    dims = dimss.getData();

    s = ""; //$NON-NLS-1$
    if (r.nextBoolean()) {
      lst = new ArrayList<>();
    } else {
      lst = null;
    }

    for (final Dimension d : dims) {
      if (lst == null) {
        s += ' ';
      }

      type = d.getDataType();
      dimIndex = d.getIndex();
      isStrict = d.getDirection().isStrict();
      try {
        n = NumberRandomization.forNumericalPrimitiveType(type)
            .randomNumberBetween(//
                lower.get(dimIndex), (!(isStrict)), upper.get(dimIndex),
                (!(isStrict)), this.m_fullRange, r);
      } catch (@SuppressWarnings("unused") final IllegalArgumentException exc) {
        return null;
      }

      if (lst != null) {
        lst.add(n);
      } else {
        s += n.toString();
      }
    }

    if (lst == null) {
      p1 = dimss.getDataFactory().parseString(s);
    } else {
      p1 = dimss.getDataFactory()
          .parseNumbers(lst.toArray(new Number[lst.size()]));
    }

    if (p1 == null) {
      throw new IllegalStateException("Data point cannot be null."); //$NON-NLS-1$
    }
    return p1;
  }

  /**
   * create a random run
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param irc
   *          the instance run context
   * @return {@code true} if a run was created, {@code false} otherwise
   */
  @SuppressWarnings("unused")
  boolean _createRun(final InstanceRunsContext irc,
      final DimensionSet dims, final Random r) {
    ArrayList<DataPoint> dps;
    DataPoint p, before, after;
    int j, k;

    dps = new ArrayList<>(30);
    dps.add(this._createDataPoint(dims, r));

    j = 0;

    inner: for (j = RandomExample.MAX_TRIALS; (--j) >= 0;) {
      k = dps.size();
      if ((k > 1) && (r.nextInt(7) > 0)) {
        k = r.nextInt(k - 1);
        before = dps.get(k);
        after = dps.get(k + 1);
        p = this._createDataPointBetween(dims, dps.get(k), dps.get(k + 1),
            r);
        if (p != null) {
          try {
            after.validateAfter(p);
            p.validateAfter(before);
            dps.add(k + 1, p);
          } catch (final Throwable error) {
            //
          }
          continue inner;
        }
      }

      p = this._createDataPoint(dims, r);

      if (p == null) {
        continue;
      }

      before = null;
      for (k = dps.size(); (--k) >= 0;) {
        after = before;
        before = dps.get(k);

        try {
          if (after != null) {
            after.validateAfter(p);
          }
          p.validateAfter(before);
          dps.add((k + 1), p);
          if ((r.nextInt(4) <= 0) && (r.nextInt(dps.size()) > 0)) {
            break inner;
          }
          continue inner;
        } catch (final Throwable t) {
          //
        }
      }

      if (before != null) {
        try {
          before.validateAfter(p);
          dps.add(0, p);
          if ((r.nextInt(8) <= 0) && (r.nextInt(dps.size()) > 0)) {
            break inner;
          }
          continue inner;
        } catch (final Throwable t) {
          //
        }
      }
    }

    if (dps.size() > (this.m_fullRange ? 0 : 3)) {
      try (final RunContext rc = irc.createRun()) {
        for (final DataPoint ppp : dps) {
          if (r.nextBoolean()) {
            rc.addDataPoint(ppp);
          } else {
            rc.addDataPoint(ppp.toString());
          }
        }
      }
      return true;
    }
    return false;
  }

  /**
   * create a random run
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param irc
   *          the run context
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createInstanceRunsInner(final InstanceRunsContext irc,
      final DimensionSet dims, final Random r) {
    int count, trials;

    count = 0;
    trials = RandomExample.MAX_TRIALS;
    do {
      if (this._createRun(irc, dims, r)) {
        ++count;
      }
    } while (((--trials) >= 0) && (count < (this.m_fullRange ? 100 : 30))
        && ((count < (this.m_fullRange ? 1 : 3)) || (r.nextInt(5) > 0)));
    return (count >= (this.m_fullRange ? 1 : 3));
  }

  /**
   * create a random run
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param ec
   *          the experiment context
   * @param inst
   *          the instance
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createInstanceRunsOuter(final ExperimentContext ec,
      final Instance inst, final DimensionSet dims, final Random r) {
    try (final InstanceRunsContext irc = ec.createInstanceRuns()) {

      if (r.nextBoolean()) {
        irc.setInstance(inst);
      } else {
        irc.setInstance(inst.getName());
      }

      return this._createInstanceRunsInner(irc, dims, r);
    }
  }

  /**
   * create a random run
   *
   * @param r
   *          the randomizer
   * @param dims
   *          the dimensions
   * @param ec
   *          the experiment context
   * @param is
   *          the instances
   * @param must
   *          the necessary instance
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createExperimentInner(final ExperimentContext ec,
      final Instance[] is, final Instance must, final DimensionSet dims,
      final Random r) {
    int i, s;
    Instance pick;

    s = r.nextInt(is.length);
    for (i = is.length; (--i) >= 0;) {
      pick = is[(i + s) % is.length];
      if ((pick != must) && (i > 0) && (r.nextInt(3) <= 0)) {
        continue;
      }

      if (!(this._createInstanceRunsOuter(ec, pick, dims, r))) {
        return false;
      }
    }
    return true;
  }

  /**
   * create the experiment set
   *
   * @param isc
   *          the context
   * @param dims
   *          the dimensions
   * @param is
   *          the instances
   * @param must
   *          the necessary instance
   * @param r
   *          the randomizer
   * @param params
   *          the parameters
   * @param configs
   *          the configurations
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createExperimentOuter(final ExperimentSetContext isc,
      final DimensionSet dims, final Instance[] is, final Instance must,
      final Map.Entry<String, Integer>[] params,
      final HashSet<HashMap<String, Object>> configs, final Random r) {
    HashMap<String, Object> config;
    int trials;

    trials = 100;

    find: for (;;) {
      config = this.__createValues(params, true, r);
      synchronized (configs) {
        if (configs.add(config)) {
          break find;
        }
      }
      if ((--trials) <= 0) {
        return false;
      }
    }
    try (final ExperimentContext ec = isc.createExperiment()) {
      ec.setName(RandomUtils.longToString(RandomExample.NAMING,
          this.m_v.incrementAndGet()));
      if (r.nextBoolean()) {
        ec.setDescription(RandomUtils.longToString(RandomExample.NAMING,
            this.m_v.incrementAndGet()));
      }

      for (final Map.Entry<String, Object> e : config.entrySet()) {
        ec.setParameterValue(e.getKey(), e.getValue());
      }

      return this._createExperimentInner(ec, is, must, dims, r);
    }
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
  boolean _createExperimentSet(final ExperimentSetContext isc,
      final DimensionSet dims, final InstanceSet insts, final Random r) {
    final Map.Entry<String, Integer>[] params;
    final Instance[] is;
    final Instance must;
    final HashSet<HashMap<String, Object>> configs;

    params = this.__createProperties(r);
    is = insts.getData().toArray(new Instance[insts.getData().size()]);
    must = (this.m_fullRange ? null : is[r.nextInt(is.length)]);
    configs = new HashSet<>();

    return this._createExperimentSetInner(isc, dims, is, must, params,
        configs, r);
  }

  /**
   * create the experiment set
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
   * @param r
   *          the randomizer
   * @return {@code true} on success, {@code false} on failure
   */
  boolean _createExperimentSetInner(final ExperimentSetContext isc,
      final DimensionSet dims, final Instance[] is, final Instance must,
      final Map.Entry<String, Integer>[] params,
      final HashSet<HashMap<String, Object>> configs, final Random r) {
    int trials, size;

    size = 0;
    trials = RandomExample.MAX_TRIALS;
    do {
      if (!(this._createExperimentOuter(isc, dims, is, must, params,
          configs, r))) {
        return false;
      }
      ++size;
    } while (((--trials) >= 0)
        && ((size <= (this.m_fullRange ? 1 : 2)) || (r.nextInt(4) > 0)));
    return (size >= (this.m_fullRange ? 1 : 2));
  }

  /**
   * handle an error.
   *
   * @param error
   *          the error
   */
  final void _error(final Throwable error) {
    final Logger logger;
    logger = this.getLogger();
    if ((logger != null) && (logger.isLoggable(Level.FINE))) {
      logger.log(Level.FINE,
          "Error when creating random experiment. Will probably make another attempt.", //$NON-NLS-1$
          error);
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
    new RandomExample(false, null).run();
  }
}
