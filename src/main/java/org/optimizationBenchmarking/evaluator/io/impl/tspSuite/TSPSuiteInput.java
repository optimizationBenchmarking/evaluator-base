package org.optimizationBenchmarking.evaluator.io.impl.tspSuite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.logging.Logger;

import org.optimizationBenchmarking.evaluator.data.spec.EDimensionDirection;
import org.optimizationBenchmarking.evaluator.data.spec.EDimensionType;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IDimensionContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IInstanceContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IRunContext;
import org.optimizationBenchmarking.evaluator.io.spec.IExperimentSetInput;
import org.optimizationBenchmarking.utils.io.encoding.StreamEncoding;
import org.optimizationBenchmarking.utils.io.paths.PathUtils;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.FileInputTool;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOJob;
import org.optimizationBenchmarking.utils.io.structured.impl.abstr.IOTool;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseDoubleParser;
import org.optimizationBenchmarking.utils.parsers.BoundedLooseLongParser;
import org.optimizationBenchmarking.utils.text.TextUtils;

/**
 * A class for loading
 * <a href="http://www.logisticPlanning.org/tsp/">TSPSuite</a> data sets
 * into the {@link org.optimizationBenchmarking.evaluator.data experiment
 * data structures}.
 */
public class TSPSuiteInput extends FileInputTool<IExperimentSetContext>
    implements IExperimentSetInput {
  /** the string indicating the begin of a comment: {@value} */
  private static final String COMMENT_START = "//"; //$NON-NLS-1$
  /**
   * the identifier of the section in the job files which holds the
   * algorithm information
   */
  private static final String ALGORITHM_DATA_SECTION = "ALGORITHM_DATA_SECTION"; //$NON-NLS-1$
  /**
   * the identifier to begin the section in the job files which holds the
   * logged information: {@value}
   */
  private static final String LOG_DATA_SECTION = "LOG_DATA_SECTION"; //$NON-NLS-1$
  /**
   * the identifier beginning the section in the job files which holds the
   * infos about the deterministic initializer: {@value}
   */
  private static final String DETERMINISTIC_INITIALIZATION_SECTION = "DETERMINISTIC_INITIALIZATION_SECTION"; //$NON-NLS-1$
  /** the string used to end sections: {@value} */
  private static final String SECTION_END = "SECTION_END"; //$NON-NLS-1$

  /** the tour length dimension */
  private static final String LENGTH = "L"; //$NON-NLS-1$

  /** Euclidean distance based on a list of 2D coordinates */
  private static final String EUC_2D = "euc2d"; //$NON-NLS-1$
  /** rounded-up Euclidean distance based on a list of 2D coordinates */
  private static final String CEIL_2D = "ceil2d"; //$NON-NLS-1$
  /**
   * geographical distance based on a list of longitude-latitude coordinate
   * pairs
   */
  private static final String GEO = "geo"; //$NON-NLS-1$
  /** pseudo-Euclidean distance based on a list of 2D coordinates */
  private static final String ATT = "att"; //$NON-NLS-1$
  /** full distance matrix */
  private static final String MATRIX = "mat"; //$NON-NLS-1$
  /** bridge */
  private static final String BRIDGE = "bridge"; //$NON-NLS-1$

  /** an instance name */
  private static final String BURMA14 = "burma14"; //$NON-NLS-1$
  /** an instance name */
  private static final String ULYSSES16 = "ulysses16"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR17 = "gr17"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR21 = "gr21"; //$NON-NLS-1$
  /** an instance name */
  private static final String ULYSSES22 = "ulysses22"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR24 = "gr24"; //$NON-NLS-1$
  /** an instance name */
  private static final String FRI26 = "fri26"; //$NON-NLS-1$
  /** an instance name */
  private static final String BAYG29 = "bayg29"; //$NON-NLS-1$
  /** an instance name */
  private static final String BAYS29 = "bays29"; //$NON-NLS-1$
  /** an instance name */
  private static final String DANTZIG42 = "dantzig42"; //$NON-NLS-1$
  /** an instance name */
  private static final String SWISS42 = "swiss42"; //$NON-NLS-1$
  /** an instance name */
  private static final String ATT48 = "att48"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR48 = "gr48"; //$NON-NLS-1$
  /** an instance name */
  private static final String HK48 = "hk48"; //$NON-NLS-1$
  /** an instance name */
  private static final String EIL51 = "eil51"; //$NON-NLS-1$
  /** an instance name */
  private static final String BERLIN52 = "berlin52"; //$NON-NLS-1$
  /** an instance name */
  private static final String BRAZIL58 = "brazil58"; //$NON-NLS-1$
  /** an instance name */
  private static final String ST70 = "st70"; //$NON-NLS-1$
  /** an instance name */
  private static final String EIL76 = "eil76"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR76 = "pr76"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR96 = "gr96"; //$NON-NLS-1$
  /** an instance name */
  private static final String RAT99 = "rat99"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROA100 = "kroA100"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROB100 = "kroB100"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROC100 = "kroC100"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROD100 = "kroD100"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROE100 = "kroE100"; //$NON-NLS-1$
  /** an instance name */
  private static final String RD100 = "rd100"; //$NON-NLS-1$
  /** an instance name */
  private static final String EIL101 = "eil101"; //$NON-NLS-1$
  /** an instance name */
  private static final String LIN105 = "lin105"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR107 = "pr107"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR120 = "gr120"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR124 = "pr124"; //$NON-NLS-1$
  /** an instance name */
  private static final String BIER127 = "bier127"; //$NON-NLS-1$
  /** an instance name */
  private static final String CH130 = "ch130"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR136 = "pr136"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR137 = "gr137"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR144 = "pr144"; //$NON-NLS-1$
  /** an instance name */
  private static final String CH150 = "ch150"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROA150 = "kroA150"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROB150 = "kroB150"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR152 = "pr152"; //$NON-NLS-1$
  /** an instance name */
  private static final String U159 = "u159"; //$NON-NLS-1$
  /** an instance name */
  private static final String SI175 = "si175"; //$NON-NLS-1$
  /** an instance name */
  private static final String BRG180 = "brg180"; //$NON-NLS-1$
  /** an instance name */
  private static final String RAT195 = "rat195"; //$NON-NLS-1$
  /** an instance name */
  private static final String D198 = "d198"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROA200 = "kroA200"; //$NON-NLS-1$
  /** an instance name */
  private static final String KROB200 = "kroB200"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR202 = "gr202"; //$NON-NLS-1$
  /** an instance name */
  private static final String TS225 = "ts225"; //$NON-NLS-1$
  /** an instance name */
  private static final String TSP225 = "tsp225"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR226 = "pr226"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR229 = "gr229"; //$NON-NLS-1$
  /** an instance name */
  private static final String GIL262 = "gil262"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR264 = "pr264"; //$NON-NLS-1$
  /** an instance name */
  private static final String A280 = "a280"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR299 = "pr299"; //$NON-NLS-1$
  /** an instance name */
  private static final String LIN318 = "lin318"; //$NON-NLS-1$
  /** an instance name */
  private static final String RD400 = "rd400"; //$NON-NLS-1$
  /** an instance name */
  private static final String FL417 = "fl417"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR431 = "gr431"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR439 = "pr439"; //$NON-NLS-1$
  /** an instance name */
  private static final String PCB442 = "pcb442"; //$NON-NLS-1$
  /** an instance name */
  private static final String D493 = "d493"; //$NON-NLS-1$
  /** an instance name */
  private static final String ATT532 = "att532"; //$NON-NLS-1$
  /** an instance name */
  private static final String ALI535 = "ali535"; //$NON-NLS-1$
  /** an instance name */
  private static final String SI535 = "si535"; //$NON-NLS-1$
  /** an instance name */
  private static final String PA561 = "pa561"; //$NON-NLS-1$
  /** an instance name */
  private static final String U574 = "u574"; //$NON-NLS-1$
  /** an instance name */
  private static final String RAT575 = "rat575"; //$NON-NLS-1$
  /** an instance name */
  private static final String P654 = "p654"; //$NON-NLS-1$
  /** an instance name */
  private static final String D657 = "d657"; //$NON-NLS-1$
  /** an instance name */
  private static final String GR666 = "gr666"; //$NON-NLS-1$
  /** an instance name */
  private static final String U724 = "u724"; //$NON-NLS-1$
  /** an instance name */
  private static final String RAT783 = "rat783"; //$NON-NLS-1$
  /** an instance name */
  private static final String DSJ1000 = "dsj1000"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR1002 = "pr1002"; //$NON-NLS-1$
  /** an instance name */
  private static final String SI1032 = "si1032"; //$NON-NLS-1$
  /** an instance name */
  private static final String U1060 = "u1060"; //$NON-NLS-1$
  /** an instance name */
  private static final String VM1084 = "vm1084"; //$NON-NLS-1$
  /** an instance name */
  private static final String PCB1173 = "pcb1173"; //$NON-NLS-1$
  /** an instance name */
  private static final String D1291 = "d1291"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL1304 = "rl1304"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL1323 = "rl1323"; //$NON-NLS-1$
  /** an instance name */
  private static final String NRW1379 = "nrw1379"; //$NON-NLS-1$
  /** an instance name */
  private static final String FL1400 = "fl1400"; //$NON-NLS-1$
  /** an instance name */
  private static final String U1432 = "u1432"; //$NON-NLS-1$
  /** an instance name */
  private static final String FL1577 = "fl1577"; //$NON-NLS-1$
  /** an instance name */
  private static final String D1655 = "d1655"; //$NON-NLS-1$
  /** an instance name */
  private static final String VM1748 = "vm1748"; //$NON-NLS-1$
  /** an instance name */
  private static final String U1817 = "u1817"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL1889 = "rl1889"; //$NON-NLS-1$
  /** an instance name */
  private static final String D2103 = "d2103"; //$NON-NLS-1$
  /** an instance name */
  private static final String U2152 = "u2152"; //$NON-NLS-1$
  /** an instance name */
  private static final String U2319 = "u2319"; //$NON-NLS-1$
  /** an instance name */
  private static final String PR2392 = "pr2392"; //$NON-NLS-1$
  /** an instance name */
  private static final String PCB3038 = "pcb3038"; //$NON-NLS-1$
  /** an instance name */
  private static final String FL3795 = "fl3795"; //$NON-NLS-1$
  /** an instance name */
  private static final String FNL4461 = "fnl4461"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL5915 = "rl5915"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL5934 = "rl5934"; //$NON-NLS-1$
  /** an instance name */
  private static final String PLA7397 = "pla7397"; //$NON-NLS-1$
  /** an instance name */
  private static final String RL11849 = "rl11849"; //$NON-NLS-1$
  /** an instance name */
  private static final String USA13509 = "usa13509"; //$NON-NLS-1$
  /** an instance name */
  private static final String BRD14051 = "brd14051"; //$NON-NLS-1$
  /** an instance name */
  private static final String D15112 = "d15112"; //$NON-NLS-1$
  /** an instance name */
  private static final String D18512 = "d18512"; //$NON-NLS-1$
  /** an instance name */
  private static final String PLA33810 = "pla33810"; //$NON-NLS-1$
  /** an instance name */
  private static final String PLA85900 = "pla85900"; //$NON-NLS-1$ //
                                                     // Asymmetric
                                                     // Instances
  /** an instance name */
  private static final String BR17 = "br17"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV33 = "ftv33"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV35 = "ftv35"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV38 = "ftv38"; //$NON-NLS-1$
  /** an instance name */
  private static final String P43 = "p43"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV44 = "ftv44"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV47 = "ftv47"; //$NON-NLS-1$
  /** an instance name */
  private static final String RY48P = "ry48p"; //$NON-NLS-1$
  /** an instance name */
  private static final String FT53 = "ft53"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV55 = "ftv55"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV64 = "ftv64"; //$NON-NLS-1$
  /** an instance name */
  private static final String FT70 = "ft70"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV70 = "ftv70"; //$NON-NLS-1$
  /** an instance name */
  private static final String KRO124P = "kro124p"; //$NON-NLS-1$
  /** an instance name */
  private static final String FTV170 = "ftv170"; //$NON-NLS-1$
  /** an instance name */
  private static final String RBG323 = "rbg323"; //$NON-NLS-1$
  /** an instance name */
  private static final String RBG358 = "rbg358"; //$NON-NLS-1$
  /** an instance name */
  private static final String RBG403 = "rbg403"; //$NON-NLS-1$
  /** an instance name */
  private static final String RBG443 = "rbg443"; //$NON-NLS-1$

  /** whether the TSP is symmetric, i.e., whether dist(i,j)=dist(j,i) */
  public static final String SYMMETRIC = "symmetric"; //$NON-NLS-1$
  /** the distance measure */
  public static final String DISTANCE_TYPE = "type"; //$NON-NLS-1$
  /** the number of cities in the TSP */
  public static final String SCALE = "n"; //$NON-NLS-1$
  /**
   * The coefficient of variation of the city distances, i.e., the standard
   * deviation of the distances divided by the mean distance.
   */
  public static final String DIST_CV = "distCv"; //$NON-NLS-1$
  /**
   * The median of the median of the city distances starting at each city,
   * divided by the mean distance.
   */
  public static final String MED_MED_DIST = "medMedDist"; //$NON-NLS-1$
  /**
   * The maximum of the median of the city distances starting at each city,
   * divided by the mean distance.
   */
  public static final String MAX_MED_DIST = "maxMedDist"; //$NON-NLS-1$
  /**
   * The minimum of the median of the city distances starting at each city,
   * divided by the mean distance.
   */
  public static final String MIN_MED_DIST = "minMedDist"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors
   * and this is the coefficient of variation over all these averages.
   */
  public static final String MEAN_DIST_CV = "meanDistCv"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 1th smallest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String SMALLEST_MEAN_DIST_1 = "smallestMeanDist1"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 1th largest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String LARGEST_MEAN_DIST_1 = "largestMeanDist1"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 2th smallest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String SMALLEST_MEAN_DIST_2 = "smallestMeanDist2"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 2th largest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String LARGEST_MEAN_DIST_2 = "largestMeanDist2"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 3th smallest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String SMALLEST_MEAN_DIST_3 = "smallestMeanDist3"; //$NON-NLS-1$
  /**
   * We compute the average distance from a city to all of its neighbors.
   * This is the 3th largest such average distance among all cities,
   * divided by the mean city distance.
   */
  public static final String LARGEST_MEAN_DIST_3 = "largestMeanDist3"; //$NON-NLS-1$
  /** The fraction of distances shorter than the mean. */
  public static final String FRAC_DIST_BELOW_MEAN = "fracDistBelowMean"; //$NON-NLS-1$
  /**
   * The average of the distance to the 1th nearest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String NEAR_1_MEAN = "near1Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 1th nearest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String NEAR_1_STDDEV = "near1Stddev"; //$NON-NLS-1$
  /**
   * The average of the distance to the 1th farthest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String FAR_1_MEAN = "far1Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 1th farthest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String FAR_1_STDDEV = "far1Stddev"; //$NON-NLS-1$
  /**
   * The average of the distance to the 2th nearest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String NEAR_2_MEAN = "near2Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 2th nearest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String NEAR_2_STDDEV = "near2Stddev"; //$NON-NLS-1$
  /**
   * The average of the distance to the 2th farthest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String FAR_2_MEAN = "far2Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 2th farthest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String FAR_2_STDDEV = "far2Stddev"; //$NON-NLS-1$
  /**
   * The average of the distance to the 3th nearest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String NEAR_3_MEAN = "near3Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 3th nearest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String NEAR_3_STDDEV = "near3Stddev"; //$NON-NLS-1$
  /**
   * The average of the distance to the 3th farthest neighbor of each city
   * divided by the mean city distance.
   */
  public static final String FAR_3_MEAN = "far3Mean"; //$NON-NLS-1$
  /**
   * The standard deviation of the distance to the 3th farthest neighbor of
   * each city divided by the mean city distance.
   */
  public static final String FAR_3_STDDEV = "far3Stddev"; //$NON-NLS-1$
  /**
   * The result of the double-minimum spanning tree heuristic, divided by n
   * times the mean city distance.
   */
  public static final String DMST = "dmst"; //$NON-NLS-1$
  /**
   * The result of the iterated nearest neighbor heurist, divided by n
   * times the mean city distance.
   */
  public static final String EDGE_GREEDY = "edgeGreedy"; //$NON-NLS-1$

  /** create */
  TSPSuiteInput() {
    super();
  }

  /**
   * Get the instance of the {@link TSPSuiteInput}
   *
   * @return the instance of the {@link TSPSuiteInput}
   */
  public static final TSPSuiteInput getInstance() {
    return __TSPSuiteInputLoader.INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  protected _TSPSuiteInputToken createToken(final IOJob job,
      final IExperimentSetContext data) {
    return new _TSPSuiteInputToken(data);
  }

  /**
   * get the instance name
   *
   * @param n
   *          the string
   * @return the instance name, or {@code null} if none found
   */
  static final String _instanceName(final String n) {
    int i;
    i = Arrays.binarySearch(TSPSuiteInput.__TSPSuiteInputLoader.ALL, n);
    if ((i >= 0) && (i < TSPSuiteInput.__TSPSuiteInputLoader.ALL.length)) {
      return TSPSuiteInput.__TSPSuiteInputLoader.ALL[i];
    }
    for (final String t : TSPSuiteInput.__TSPSuiteInputLoader.ALL) {
      if (t.equalsIgnoreCase(n)) {
        return t;
      }
    }
    return null;
  }

  /**
   * fill in the dimension set
   *
   * @param esb
   *          the experiment set builder
   */
  public static final void makeTSPSuiteDimensionSet(
      final IExperimentSetContext esb) {
    final BoundedLooseLongParser p1, p2;
    final BoundedLooseDoubleParser bd;

    p1 = new BoundedLooseLongParser(0L, Long.MAX_VALUE);
    p2 = new BoundedLooseLongParser(1L, Long.MAX_VALUE);
    bd = new BoundedLooseDoubleParser(0d, Double.MAX_VALUE);

    try (final IDimensionContext d = esb.createDimension()) {
      d.setName("FEs"); //$NON-NLS-1$
      d.setDescription(
          "the number of fully constructed candidate solutions"); //$NON-NLS-1$
      d.setDirection(EDimensionDirection.INCREASING_STRICTLY);
      d.setType(EDimensionType.ITERATION_FE);
      d.setParser(p1);
    }

    try (final IDimensionContext d = esb.createDimension()) {
      d.setName("DEs"); //$NON-NLS-1$
      d.setDescription(
          "the number of calls to the distance function (between two nodes)"); //$NON-NLS-1$
      d.setDirection(EDimensionDirection.INCREASING);
      d.setType(EDimensionType.ITERATION_SUB_FE);
      d.setParser(p2);
    }

    try (final IDimensionContext d = esb.createDimension()) {
      d.setName("AT"); //$NON-NLS-1$
      d.setDescription("the consumed clock runtime in milliseconds"); //$NON-NLS-1$
      d.setDirection(EDimensionDirection.INCREASING);
      d.setType(EDimensionType.RUNTIME_CPU);
      d.setParser(p1);
    }

    try (final IDimensionContext d = esb.createDimension()) {
      d.setName("NT"); //$NON-NLS-1$
      d.setDescription(
          "the consumed normalized runtime, i.e., AT divided by a performance factor based on the clock runtime of the Double-Ended Nearest Neighbor heuristic on the same instance"); //$NON-NLS-1$
      d.setDirection(EDimensionDirection.INCREASING);
      d.setType(EDimensionType.RUNTIME_NORMALIZED);
      d.setParser(bd);
    }

    try (final IDimensionContext d = esb.createDimension()) {
      d.setName(TSPSuiteInput.LENGTH);
      d.setDescription("the best tour length discovered so far"); //$NON-NLS-1$
      d.setDirection(EDimensionDirection.DECREASING);
      d.setType(EDimensionType.QUALITY_PROBLEM_DEPENDENT);
      d.setParser(p2);
    }
  }

  /**
   * make instance BURMA14
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BURMA14(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BURMA14);
      i.setDescription("14 cities in Burma (Zaw Win).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(3323L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC,
          "whether the TSP is symmetric, i.e., whether dist(i,j)=dist(j,i)", //$NON-NLS-1$
          Boolean.TRUE, "the TSP is symmetric");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE,
          "the distance measure", //$NON-NLS-1$
          TSPSuiteInput.GEO, "geographical distances");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE,
          "the number of cities in the TSP", //$NON-NLS-1$
          Long.valueOf(14L), null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV,
          "The coefficient of variation of the city distances, i.e., the standard deviation of the distances divided by the mean distance.", //$NON-NLS-1$
          Double.valueOf(0.5315384694836834d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST,
          "The median of the median of the city distances starting at each city, divided by the mean distance.", //$NON-NLS-1$
          Double.valueOf(0.9012082362978164d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST,
          "The maximum of the median of the city distances starting at each city, divided by the mean distance.", //$NON-NLS-1$
          Double.valueOf(1.588392630680901d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST,
          "The minimum of the median of the city distances starting at each city, divided by the mean distance.", //$NON-NLS-1$
          Double.valueOf(0.7176093523023358d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV,
          "We compute the average distance from a city to all of its neighbors and this is the coefficient of variation over all these averages.", //$NON-NLS-1$
          Double.valueOf(0.24088502797792535d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1,
          "We compute the average distance from a city to all of its neighbors. This is the 1th smallest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.7839470589591644d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1,
          "We compute the average distance from a city to all of its neighbors. This is the 1th largest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.7839470589591644d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2,
          "We compute the average distance from a city to all of its neighbors. This is the 2th smallest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.7986349696788029d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2,
          "We compute the average distance from a city to all of its neighbors. This is the 2th largest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.7986349696788029d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3,
          "We compute the average distance from a city to all of its neighbors. This is the 3th smallest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.8173580207060342d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3,
          "We compute the average distance from a city to all of its neighbors. This is the 3th largest such average distance among all cities, divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.8173580207060342d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN,
          "The fraction of distances shorter than the mean.", //$NON-NLS-1$
          Double.valueOf(0.20563075007493833d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN,
          "The average of the distance to the 1th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.3030505660725403d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV,
          "The standard deviation of the distance to the 1th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.23898882527667012d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN,
          "The average of the distance to the 1th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(1.9836173303511724d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV,
          "The standard deviation of the distance to the 1th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.36590064159114977d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN,
          "The average of the distance to the 2th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.452927206068851d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV,
          "The standard deviation of the distance to the 2th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.17439055064168116d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN,
          "The average of the distance to the 2th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(1.5378842030021445d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV,
          "The standard deviation of the distance to the 2th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.31566225920099944d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN,
          "The average of the distance to the 3th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.5391062740667296d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV,
          "The standard deviation of the distance to the 3th nearest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.21456981097545122d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN,
          "The average of the distance to the 3th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(1.3430445710069405d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV,
          "The standard deviation of the distance to the 3th farthest neighbor of each city divided by the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.3157341351044436d), null);
      i.setFeatureValue(TSPSuiteInput.DMST,
          "The result of the double-minimum spanning tree heuristic, divided by n times the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.6320297908644423d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY,
          "The result of the iterated nearest neighbor heurist, divided by n times the mean city distance.", //$NON-NLS-1$
          Double.valueOf(0.5828702529456524d), null);
    }
  }

  /**
   * make instance ULYSSES16
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ULYSSES16(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ULYSSES16);
      i.setDescription("Odyssey of Ulysses (Gr&ouml,tschel/Padberg).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6859L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(16L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.7088864314398818d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7380874406418864d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.260930080235795d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5501883084984445d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.4264480047803395d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6826592434910758d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6826592434910758d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.688308498444408d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.688308498444408d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6907646962502046d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6907646962502046d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.1817586376289504d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.29443671196987065d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.39895399785524716d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.532263181594891d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.5765522543608889d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.43098595873587686d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.4160435781752875d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.5834032667430817d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.5333471490034573d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.5224793270018012d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.4085469864114136d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.50012280989029d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.4976544070474596d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.5820421237923694d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.6142797199934501d), null);
    }
  }

  /**
   * make instance GR17
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR17(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR17);
      i.setDescription("17-city problem (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2085L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX,
          "distances provided as matrix, origin unclear");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(17L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5940523166570454d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8302897231296523d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.9300594441171743d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5462432389010871d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.31354758370135d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6980533390456809d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6980533390456809d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7067021903282814d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7067021903282814d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7365179671182991d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7365179671182991d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.5462432389010871d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.26947999785786964d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.2059972023148723d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1052857066352484d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3308065028756743d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3926524929041932d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.27346200606708304d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7531194773201948d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3366507781844411d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.4866920152091255d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.2999614923637089d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5035612917046d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.29442257878489364d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.5393884217854656d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.46891233331548227d), null);
    }
  }

  /**
   * make instance BR17
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BR17(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BR17);
      i.setDescription("17 city problem (Repetto).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(39L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          "the TSP is asymmetric");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(17L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(1.2870022599516384d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.5506072874493927d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(3.3036437246963564d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.3441295546558705d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.5637067377340765d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.5936234817813765d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.5936234817813765d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.666751012145749d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.666751012145749d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7097672064777328d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7097672064777328d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(14.591093117408906d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(3.473684210526316d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(1.070159271371193d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.09716599190283401d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.14813333098360892d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(3.473684210526316d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(1.070159271371193d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.15789473684210528d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.16640488765285355d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.2469635627530364d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(1.1805906819086134d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.18218623481781376d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.4008097165991903d), null);
    }
  }

  /**
   * make instance GR21
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR21(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR21);
      i.setDescription("21-city problem (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2707L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(21L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5151914513668063d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9618404522613065d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6076476130653266d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6595477386934673d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.23340090529059515d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7469378140703518d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7469378140703518d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7558691896984924d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7558691896984924d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7626020728643216d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7626020728643216d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.5826005025125628d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.25963149078726966d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.173241648604399d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8811505443886096d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2713861450161236d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3660228224455611d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.19630399367203813d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7470163316582914d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.251204135410076d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.46011306532663315d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.22589204383607292d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.584092336683417d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2152996788508094d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.44480213567839194d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.39625209380234505d), null);
    }
  }

  /**
   * make instance ULYSSES22
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ULYSSES22(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ULYSSES22);
      i.setDescription("Odyssey of Ulysses (Gr&ouml,tschel/Padberg).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(7013L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(22L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.7165453032468988d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7956569581513703d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.437278635535229d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5931020253773941d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.41551769815920414d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7142693396604884d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7142693396604884d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7152149742672764d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7152149742672764d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7320472702681017d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7320472702681017d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.35480210446683397d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.228912348268629d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.3817097436387072d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.7693339293696915d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.6004771007499953d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3321756473298717d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.400092458919143d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6636463670437742d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.5024453878634382d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.4154000894054537d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.40413936202812323d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.573922836216086d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.4966962929337075d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.49393074516007013d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.496458168563667d), null);
    }
  }

  /**
   * make instance GR24
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR24(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR24);
      i.setDescription("24-city problem (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1272L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(24L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4989070065427199d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9620265593166253d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5243378580721176d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7045828321755565d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.22139490048819813d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6972188811703772d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6972188811703772d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7366896585581383d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7366896585581383d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7372787746385526d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7372787746385526d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.9782517980313703d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2605488598149193d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08636783390643737d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9062691769557425d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.38840349631492854d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.36414737720611695d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.1279872480085207d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6987898573848155d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.29524231376774995d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.43838827658999974d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.16711698990401105d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5392989518643068d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3069516737754488d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4923046711995876d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.45222023122806154d), null);
    }
  }

  /**
   * make instance FRI26
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FRI26(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FRI26);
      i.setDescription("26 cities (Fricker).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(937L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(26L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5438789918315017d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8447200356453289d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.8921728798455368d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6081984256646369d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.26017338633377407d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7140056438437546d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7140056438437546d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7302242685281449d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7302242685281449d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7321550571810486d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7321550571810486d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.707114213574929d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2517451358978167d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.17305991786497585d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0908213277885044d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.36737964666634904d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3248923214020496d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.20098651682557575d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.788207336996881d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3360050224547693d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.42403089261844645d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.2002242370138176d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6266894400712906d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3623946801047376d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.44705183424922024d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3668498440516857d), null);
    }
  }

  /**
   * make instance BAYG29
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BAYG29(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BAYG29);
      i.setDescription(
          "29 Cities in Bavaria, geographical distances (Gr&ouml,tschel,J&uuml,nger,Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1610L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(29L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.46968220332189436d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9367394025304239d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4877625804894967d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7408200503671979d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18352614803708567d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7377588104896474d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7377588104896474d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7659659493613619d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7659659493613619d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7978903080843875d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7978903080843875d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.706136051754558d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2632666294693348d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0746985543964036d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8069760077209596d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29813362085477607d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3413810263447589d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.09707634095279191d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6722814531087418d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.27068168860820735d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3988056640477734d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.11492294695812007d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5929003362839864d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2639779354977272d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4577081416916743d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.4167508633299654d), null);
    }
  }

  /**
   * make instance BAYS29
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BAYS29(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BAYS29);
      i.setDescription(
          "29 cities in Bavaria, street distances (Gr&ouml,tschel,J&uuml,nger,Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2020L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(29L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48367796073124086d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9366692167925792d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5384670555608684d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6843023811800708d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2026092226035093d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7378610021994837d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7378610021994837d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7432341971884863d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7432341971884863d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7647269771444966d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7647269771444966d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.135411685951994d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.24299512288419242d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.07943912654345156d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8654728889738932d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.320947747387498d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.34190016257052697d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.11818040169159896d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6865735870708618d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30968212721893895d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3874199101080616d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.1302321509749363d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5869991393325047d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2915389542724323d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.40532657549966533d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.4150329922539926d), null);
    }
  }

  /**
   * make instance FTV33
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV33(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV33);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1286L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(34L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.45698992556930795d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0003746799608668d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7360587831227494d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5605212214566725d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.23701106280946974d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6228013571740806d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6228013571740806d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6482795945130202d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6482795945130202d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6725782838270089d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6725782838270089d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(4.44524468683d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.22897108719635315d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.15209781517382853d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.765596053371079d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3194173748876024d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.331321163173123d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.16648744671711532d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6506525675985098d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30304037604464745d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.42497033783643146d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.17890781044720383d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5599800170687539d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.28475930777700154d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4142086967382028d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3952040965009055d), null);
    }
  }

  /**
   * make instance FTV35
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV35(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV35);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1473L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(36L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.45289051718421325d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.016958106608907d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7010935601458081d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5399123038723651d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2379658382407336d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.5984468276189973d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.5984468276189973d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6424005494215226d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6424005494215226d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.675154524803212d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.675154524803212d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(4.837022557979819d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2292778276718263d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.14589986702147914d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7317050263851468d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.31690144411211224d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.32933006967557127d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.16846588757500633d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6464449022957133d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3124569016469804d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.4141793016007185d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.17509412274748784d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5743333274634452d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2867216424311675d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4199317919007285d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.37041928610421404d), null);
    }
  }

  /**
   * make instance FTV38
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV38(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV38);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1530L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(39L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4533369464224169d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0318760736321038d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7022189243858064d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5573637186042021d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.22910191663182528d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6199977637958549d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6199977637958549d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6433864262408392d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6433864262408392d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6663786706782814d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6663786706782814d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(5.709212144621421d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2261513910206239d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.14364301349566674d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7528181254510526d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.31424373901905905d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.31556906313210886d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.16038089963756924d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.6680354946585216d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30917607851323314d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.39957918703814765d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.16113454601451582d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5958061007714905d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.28292012162773217d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4219818867464246d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.33546111545928586d), null);
    }
  }

  /**
   * make instance DANTZIG42
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __DANTZIG42(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.DANTZIG42);
      i.setDescription("42 cities (Dantzig).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(699L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(42L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5740192577271624d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8371677252411198d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7553516819571866d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5671136203246295d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.25559249349940893d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7182780522230064d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7182780522230064d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.731780757468831d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.731780757468831d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7321100917431194d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7321100917431194d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(13.232651140908022d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.1459578138477221d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08837918506536863d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0440523798321966d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3654922182791387d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.23533286285579866d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.10301253619043386d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.998400376382028d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.37217221062657535d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.2887006978750098d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.11891480159555935d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.897130087038344d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.38466673230002907d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3198855171332236d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3128126715282679d), null);
    }
  }

  /**
   * make instance SWISS42
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __SWISS42(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.SWISS42);
      i.setDescription("42 cities in Switzerland (Fricker).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1273L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(42L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.508717358487248d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9294585296461829d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6504403797455585d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.651489623583773d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.22852109846139396d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.708905457076847d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.708905457076847d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7095410567096118d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7095410567096118d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7116597221521606d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7116597221521606d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(8.408559408387898d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.18986269030155672d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08557542740108073d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0438160191285224d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.35244809512810144d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.2589412726117092d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.12122090620991555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.893456350447442d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.35136135904089905d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.34353151262623716d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.1331854834391055d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6986299296804852d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3119030010100222d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3505634641188874d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.28748272278776016d), null);
    }
  }

  /**
   * make instance P43
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __P43(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.P43);
      i.setDescription("Asymmetric TSP (Repetto,Pekny).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(5620L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(43L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(2.5703134577288d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.026949743429041485d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(8.458850718790396d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.011790512750205649d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(2.4288368833348555d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.097171470727035d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.097171470727035d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.09853499941243293d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.09853499941243293d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.10110164164377021d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.10110164164377021d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.72192408633319d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0032512045125151793d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.00803930451290687d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.562458380665126d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(2.6032524908011476d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.006228211054095343d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.010943920244898436d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.558462924517216d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(2.5920685235409d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.012299737553370677d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013602275107572111d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.545614791021975d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(2.5884567287173d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2281718829566376d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.04011124603392221d), null);
    }
  }

  /**
   * make instance FTV44
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV44(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV44);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1613L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(45L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.44485566367835916d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0296221989467007d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6795255727499372d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6572056589021493d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.19936476321038393d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6455883871538791d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6455883871538791d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6728059952498266d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6728059952498266d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6960405387463673d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6960405387463673d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(7.331494239308422d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2073848968091227d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.1345340301166858d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7994453213743047d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2676767165453326d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.292415949960907d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.1304968845487549d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.68098602976972d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.26022544769940414d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.36462743593904434d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.13940915766376957d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6206204729520408d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.26406621877205577d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.41006387655450155d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3187041763169929d), null);
    }
  }

  /**
   * make instance ATT48
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ATT48(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ATT48);
      i.setDescription("48 capitals of the US (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(10628L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.ATT,
          "pseudo-Euclidean distance in two dimensions");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(48L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.593127602346306d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.868929193869116d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.835047588824368d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5985315155997677d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.24886730281124758d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7264519134059984d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7264519134059984d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7335563273046478d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7335563273046478d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.742810491806635d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.742810491806635d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.2317047266361778d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.15009396628133242d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.07628910073047865d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0329743591056015d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3706451307986351d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.21967806631639383d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.1066100010230473d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.003785523135838d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.36625541127425404d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.25794831897180504d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.12263192261619309d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9669386271795017d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.37124211588199796d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.29042490844365737d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2551417001285585d), null);
    }
  }

  /**
   * make instance GR48
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR48(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR48);
      i.setDescription("48-city problem (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(5046L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(48L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48614529156082176d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9751325568541864d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4980959187268061d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7399132281516543d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1805113868347296d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7355888075248158d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7355888075248158d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7554617068099502d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7554617068099502d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7730995122879546d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7730995122879546d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.690178341860027d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.17403566027383946d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08143002660375138d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9105861250073388d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29527609409316774d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.23360172005045154d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.09087594681304133d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7875051372740358d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30593144155954805d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.28374758826494767d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.09808303518860559d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7301751835752996d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3035105808589571d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.34164846266441806d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.30477650074199447d), null);
    }
  }

  /**
   * make instance HK48
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __HK48(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.HK48);
      i.setDescription("48-city problem (Held/Karp).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(11461L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(48L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.567206809200697d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8427365954112729d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.8252775216158312d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.580725681756724d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2567314923951538d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7173058388745208d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7173058388745208d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7418095588082345d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7418095588082345d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7448257212788529d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7448257212788529d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.2611719351282389d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.17836050768601402d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08034486894405075d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.064904262842959d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.36168106656244436d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.23907681160425176d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0932978651329997d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9927209078995722d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3450593679325976d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.29932465695485466d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.12455057740117348d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9464249807589638d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.34323615865813695d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.30606638677603437d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2729687705844422d), null);
    }
  }

  /**
   * make instance FTV47
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV47(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV47);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1776L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(48L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4416076678920327d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9903300726645539d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5732903282046813d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6391491958331518d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20744662434208863d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6651515245857748d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6651515245857748d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6771066182651416d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6771066182651416d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.679497637001015d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.679497637001015d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(8.154419960025155d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.1870038169127216d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.12160624075060338d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7667324611926454d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.30388456017431825d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.2932360321542207d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.12671949918798978d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.655525183529368d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2727093234738646d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3500102739086307d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.13994333797555003d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5961170851987223d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2772725308142989d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.34254768027596344d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2986500706720382d), null);
    }
  }

  /**
   * make instance RY48P
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RY48P(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RY48P);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(14422L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(48L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5427871037528256d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8794597554980248d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.782629359621558d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.636653342721985d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.22729805656771018d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7556696829461683d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7556696829461683d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7571830775392859d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7571830775392859d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7648621538080675d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7648621538080675d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.1213880257323792d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2095101747371314d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08212949605831268d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.962794937757366d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3404566835512158d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.27626730254151427d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.10788754623863162d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9194366497616444d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.33452054160067957d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3173936449104517d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.10990455929133948d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.87002283715555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.33394065239234666d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3339868800210506d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2840974068715433d), null);
    }
  }

  /**
   * make instance EIL51
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __EIL51(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.EIL51);
      i.setDescription("51-city problem (Christofides/Eilon).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(426L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D,
          "nodes are points in the two-dimensional Euclidean plane");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(51L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4653997143886653d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9877738772545696d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5742646168744703d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7408304079409272d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18243637474795296d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7303353104950974d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7303353104950974d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7395956905943589d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7395956905943589d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7488560706936206d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7488560706936206d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(41.98038978331921d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.21062825323810674d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05580385686778742d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9307589880159786d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3091921656977905d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.2693378525602227d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05455104741450213d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.752209175644595d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.27757727292691825d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3044425614332405d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.06416361910845547d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6614211354557558d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.28024996003752917d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3365210022999637d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3153371262559012d), null);
    }
  }

  /**
   * make instance BERLIN52
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BERLIN52(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BERLIN52);
      i.setDescription("52 locations in Berlin (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(7542L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(52L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5909111668607689d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7596682149444862d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.9000397229618384d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5006509059588375d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.317329983309729d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6803507681739106d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6803507681739106d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6816801108572162d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6816801108572162d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6822936536341266d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6822936536341266d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.5901730898564863d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.17711328123463685d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.13965119851196192d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.2082997392443198d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3872415996587179d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.24126586460369465d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.17069649871588088d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.1089451390500313d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3946910722503447d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.30943007382178156d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.19832764332237837d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9745222428921463d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.37274028225261596d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3180550693971942d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3160492564726796d), null);
    }
  }

  /**
   * make instance FT53
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FT53(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FT53);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6905L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(53L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.7630577750476125d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7851198726815131d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(3.146565691289475d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.43820644056642594d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.6675828734914383d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.43508531207236306d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.43508531207236306d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.4394158778578753d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.4394158778578753d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.4515882789847204d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.4515882789847204d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.629404269321d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.14905449681740587d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.13809300001520253d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.5307368586872268d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.7746157620266334d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.21994533608444125d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.16105002771917531d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.447290838383697d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.7769132897310962d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.26591720323331247d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.17755357140963673d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.3979505080299128d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.7639507293528284d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.4472400464341475d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.31728626710823765d), null);
    }
  }

  /**
   * make instance FTV55
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV55(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV55);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1608L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(56L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4446134481674803d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0089754578862846d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5324288909250339d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5689711228682056d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20099839034660774d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6089715169607584d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6089715169607584d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6189026492871852d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6189026492871852d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6848343333431858d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6848343333431858d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(11.887703327126376d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.15565424289894483d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08772207629412064d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7909043438851615d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29677731620337655d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.24655415324288912d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.09599242375864608d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7004108414861232d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.29253562120031634d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.32173962305047343d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.10931309231681112d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.630915082907221d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2741061993362548d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3416536123508606d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.26186218583434323d), null);
    }
  }

  /**
   * make instance BRAZIL58
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BRAZIL58(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BRAZIL58);
      i.setDescription("58 cities in Brazil (Ferreira).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(25395L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(58L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6620715702859107d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7210318516672787d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.404690482528608d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.4503517095644682d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.352266571699688d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6696759549625586d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6696759549625586d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6717910936569678d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6717910936569678d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6768443822109258d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6768443822109258d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.8669270409116012d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.11830629410559404d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08882465887138447d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.5602268218771127d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4699766193013042d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.17353261366209885d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.15157242442729815d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.1844403495697353d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3778994903762993d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.22367144713174933d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.2137580274662256d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0182599216833927d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.38326340600772224d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2413846339842311d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.24789564558982372d), null);
    }
  }

  /**
   * make instance FTV64
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV64(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV64);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1839L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(65L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4495632115053916d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0128705938387497d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6708668190332658d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.613636929113987d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20883877202525156d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6150231571165036d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6150231571165036d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6223008541297154d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6223008541297154d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6556858451903219d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6556858451903219d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(15.821482268722075d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.15343766772470221d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.10699453977929582d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8613843086098976d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29769106183348804d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.24795709091167592d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.12138501314104348d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7231880400513258d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2801516383968342d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3053966922467201d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.1213276296552056d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6525543916769447d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2750562894160377d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3337183966673657d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.26126487973583473d), null);
    }
  }

  /**
   * make instance ST70
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ST70(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ST70);
      i.setDescription("70-city problem (Smith/Thompson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(675L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(70L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4667577953181139d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0142636396053726d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5118269345061215d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7272078925472484d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15287092327543872d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7563294900748841d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7563294900748841d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7627085066761757d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7627085066761757d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7688101747295852d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7688101747295852d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(46.656127421847145d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.11837632235825508d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05894319911030654d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8945679305836207d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.25218258919427766d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.1856293830975871d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.06072242276899879d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7477594199453228d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.24942557328735923d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.23456555331035303d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07905096720951872d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6930821347913942d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2427174672329745d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.24440746463806015d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.22609057411149414d), null);
    }
  }

  /**
   * make instance FT70
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FT70(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FT70);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(38673L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(70L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4108442195212075d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8909891922392466d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.132356487308959d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6531979590163539d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.3478187061551173d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6590073514636212d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6590073514636212d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6780390899167755d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6780390899167755d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6844392680366463d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6844392680366463d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.9126999628649006d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.4681228930653819d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.13130209363286d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.4454933904345773d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3663159186048356d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.5340527656003589d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.13926329360784712d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.3607620652902566d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3724934769244963d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.5782139946274675d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.15111494091081226d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.3173772974328555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3809673492638365d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.6812753495280949d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.5645843280234963d), null);
    }
  }

  /**
   * make instance FTV70
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV70(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV70);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1950L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(71L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.44870132747824887d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0019356205307253d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6408510886952459d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6026134529279d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2114172630730211d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6070734147998277d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6070734147998277d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6136077775424194d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6136077775424194d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6485614322131082d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6485614322131082d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(18.65923583162293d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.14694646730992572d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.10077858477397884d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8496643707042009d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29675544411711136d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.2309012687444762d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.10924861089476466d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7225561877771043d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2906279403132349d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.2899048259037157d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.11094968264068597d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.6619164834522706d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2814355358359d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2982900801273858d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2559547722176368d), null);
    }
  }

  /**
   * make instance EIL76
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __EIL76(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.EIL76);
      i.setDescription("76-city problem (Christofides/Eilon).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(538L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(76L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4748289043805438d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9639674872369436d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.47607521483157d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7229756154277077d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18877065823495365d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7221723091883435d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7221723091883435d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7273937997442104d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7273937997442104d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7306070247016668d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7306070247016668d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(90.97443160798656d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.17043833039140038d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0566357884880508d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9390332843598388d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.31363073390196977d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.21760614740669493d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.07273116528462692d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8371666543352112d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.29725696224849285d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.26358485979135177d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07265232151577226d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7681985857582259d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2858243750023293d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2948979483981439d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.23385724402540983d), null);
    }
  }

  /**
   * make instance PR76
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR76(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR76);
      i.setDescription("76-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(108159L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(76L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5178710023755757d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9044551834304617d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6604720262174688d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6796147556911112d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2310542112865413d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7237564198178112d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7237564198178112d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7266563916777974d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7266563916777974d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7282633712182156d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7282633712182156d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.40641941395427167d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.1138232688297867d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.07124008849762867d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1853903983599134d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3571232503212848d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.191278517527255d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.09105409514609468d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0613923699248518d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.35682711924537047d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.24168230954962144d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.10569730509672076d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9643918345125804d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.31474890657727955d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.26922988367339795d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2567555761744417d), null);
    }
  }

  /**
   * make instance GR96
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR96(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR96);
      i.setDescription(
          "Africa-Subproblem of 666-city TSP (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(55209L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(96L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5064552394988407d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9841829644372679d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.474267495472008d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7320839995958838d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17086793447891238d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7457626156105251d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7457626156105251d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7462123907485115d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7462123907485115d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7483849409604822d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7483849409604822d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.263213919359293d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.10333370833524762d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.09466532664252132d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9873995949411427d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3014226679043386d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.14304382712301958d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.09991169366200818d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8403067977549177d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.28999711015763474d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.17783367849221288d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.10358291920654772d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7896123977563716d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.30401445681126227d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.20500844492574422d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1693311111109849d), null);
    }
  }

  /**
   * make instance RAT99
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RAT99(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RAT99);
      i.setDescription("Rattled grid (Pulleyblank).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1211L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(99L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5505690928751771d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8941610176685301d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.411833185792416d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.705916592896208d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18635858156851326d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7395316687484084d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7395316687484084d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7467348992881656d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7467348992881656d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7516571068236664d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7516571068236664d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(64.00310442258952d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.11563246546936201d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03099341001868718d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9908274013800127d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3559718795794174d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.15496889514085105d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0334482844107638d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9314068127523858d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3505196100160828d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.18681833064525907d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.03416822930669192d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9079951008331009d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.35455954567340003d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.20155463662491055d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1787371305919018d), null);
    }
  }

  /**
   * make instance KROA100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROA100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROA100);
      i.setDescription("100-city problem A (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(21282L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5355011046454974d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9282747559124876d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3777982365779178d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7061435170921191d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16516586152987456d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7523175279261244d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7523175279261244d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7572006362329943d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7572006362329943d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7590960144270756d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7590960144270756d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.1858296093973912d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08307123775990152d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0453324442576901d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9263922497572321d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3007371596310259d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.12012039017157247d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04372107160813d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8871627038697718d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3103345452703859d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.16293910923365668d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0399666911418775d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.863119920046925d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.30941385422133705d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.16738173401006406d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.14197108940079714d), null);
    }
  }

  /**
   * make instance KROB100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROB100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROB100);
      i.setDescription("100-city problem B (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(22141L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5409912433758274d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9433838034011329d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.519369391909865d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6174660321256159d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17505644854888674d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7498083998556743d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7498083998556743d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7506882880536082d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7506882880536082d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7509576415835879d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7509576415835879d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.25917771275517d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08491046808376151d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.048729164547831876d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9663093203264708d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32011383188792414d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.1308293191676521d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05002287151790235d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8820862424512175d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3225885075881364d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.17636892048133115d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04984280441618004d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.848587821341754d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3212310181524353d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.1767422444738831d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.15296209872608946d), null);
    }
  }

  /**
   * make instance KROC100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROC100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROC100);
      i.setDescription("100-city problem C (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(20749L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5355814923297139d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9529251263314295d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3624976968280913d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7038885382404942d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15293873763119661d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7691257711839248d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7691257711839248d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7724580199029693d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7724580199029693d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7780355271171986d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7780355271171986d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.1789652779683477d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08243787316285288d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.046727093445802524d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9475837276494319d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29262433584352304d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.12411254376026627d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05091078524401036d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8726610554211767d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.29940747115127886d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.15977740578556784d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05179200733369711d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.845540471140766d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3037165731499584d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.17327301309769763d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.13698482454730423d), null);
    }
  }

  /**
   * make instance KROD100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROD100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROD100);
      i.setDescription("100-city problem D (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(21294L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5316972242881749d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9183963517305314d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.435837286884449d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6823599061923107d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18212059406653242d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379769419362109d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379769419362109d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7392031052896563d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7392031052896563d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7493158565834753d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7493158565834753d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.317998034422988d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.084960858760225d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.04912343263493642d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9341132888240165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3358226734716633d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.1327812295445918d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.054914301529246305d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9059789706792138d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3351405702312619d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.17114175005712806d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05837421490186335d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.883160070671597d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.334492758353474d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.17526165892470427d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.14988620832515376d), null);
    }
  }

  /**
   * make instance KROE100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROE100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROE100);
      i.setDescription("100-city problem E (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(22068L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5389829835400343d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9352515300617062d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.473309817726836d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6633358074326546d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.162431797047473d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7514668485464006d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7514668485464006d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7591993808375954d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7591993808375954d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7645585038961233d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7645585038961233d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.1186597317242826d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08354336352791945d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.04696026993191247d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9097547585980394d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3037349277251809d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.13113727472439296d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.053129487943493585d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.893953626266071d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30784088210343424d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.16635353604153127d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05825377699254289d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.834721029362163d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.295973173268928d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.18308414674596846d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.14343987355501947d), null);
    }
  }

  /**
   * make instance RD100
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RD100(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RD100);
      i.setDescription("100-city random TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(7910L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.47300559177537044d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9844120667872497d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5189100262677124d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7306605102662219d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.14938337018320777d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7594004780905463d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7594004780905463d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7601276120013452d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7601276120013452d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7740704047409132d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7740704047409132d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(8.91549795039129d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.09908908299324674d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05246282617626497d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8828005562574417d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.23944442812305705d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.139941283936703d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05950677008579261d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8272091691586152d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2383439159113969d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.17649230601430635d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07278676842709457d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7674065860153971d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.23231573175822637d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.20008580180147426d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.16519046363875986d), null);
    }
  }

  /**
   * make instance KRO124P
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KRO124P(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KRO124P);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(36230L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(100L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48316665751482823d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9295335666353323d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3302983907746433d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7240467038336607d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1478377189305185d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7671932601895466d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7671932601895466d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7752313488843556d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7752313488843556d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7791446289068285d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7791446289068285d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.862680677196281d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.15614907439293393d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05217522793406529d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.862307805068734d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2826733584398087d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.20960707072587326d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.044879485752474385d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8158756270766825d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.277543424844305d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.2445738670737042d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04894258751210861d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7853379233640163d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2786269986049817d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2571700808800946d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.23192529992647265d), null);
    }
  }

  /**
   * make instance EIL101
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __EIL101(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.EIL101);
      i.setDescription("101-city problem (Christofides/Eilon).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(629L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(101L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48215554177820685d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0024755365608724d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6806207524696979d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7371143651182885d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18507428197921533d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7329865246736261d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7329865246736261d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7453700460076134d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7453700460076134d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7486133492141339d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7486133492141339d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(149.4867932459889d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.14946635839230246d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.06449745481810039d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0431934421635254d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2832528484327084d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.18887643335902285d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.07053881368976339d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8823419510030592d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2621221026135323d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.21690137555757955d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07706527362825415d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.823080875312361d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.264444561027691d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.25572759756183d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.2376281557252621d), null);
    }
  }

  /**
   * make instance LIN105
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __LIN105(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.LIN105);
      i.setDescription("105-city problem (Subproblem of lin318).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(14379L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(105L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5698252519450293d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.854461423106779d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4991296339795874d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7168642555687091d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20310603708348116d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7328388555036845d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7328388555036845d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7335167142943813d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7335167142943813d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7352481126513418d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7352481126513418d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(5.148852034665302d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.06114620749090355d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.042900624550606524d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.060754192768573d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3624341347991939d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.1256332515598919d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.08016064385686059d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0074625913728763d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3595529493644984d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.15845919546756312d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07665292029077131d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.963069101928495d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3515791836936694d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.16785074817254247d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.13562340452341431d), null);
    }
  }

  /**
   * make instance PR107
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR107(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR107);
      i.setDescription("107-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(44303L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(107L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5746219368833695d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.3422796161767507d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.416110546264223d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(1.304531471470374d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.07290951757077363d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8916637572338831d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8916637572338831d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8921141374287705d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8921141374287705d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8955443586805293d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8955443586805293d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.9640580094128579d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.037151520649033d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0014847442339013324d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7619207672810517d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.11126715333523572d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.044918009465064614d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.00801883468119018d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7441949800701135d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.11179716575667384d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.060463092512784096d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.014677228950152128d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.720241821176581d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.10935136133362827d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.09837898369325464d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08222171248234764d), null);
    }
  }

  /**
   * make instance GR120
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR120(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR120);
      i.setDescription("120 cities in Germany (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6942L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(120L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5504532274357751d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9090497493459103d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5269284566566868d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7107324648101695d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18667032104930945d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7188435617926873d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7188435617926873d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7316170945703815d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7316170945703815d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.736009802674928d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.736009802674928d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(17.979238674326933d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.09661758264905987d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.04557765385349607d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.076829042736426d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3381001293188265d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.13400745989727228d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05348311413876516d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.043107462080782d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3419946853798415d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.15905504756840486d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0585911653850864d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9642008739177175d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3364410742116451d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.18796198894630237d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1574501678091561d), null);
    }
  }

  /**
   * make instance PR124
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR124(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR124);
      i.setDescription("124-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(59030L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(124L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5065562484552287d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0316804064353917d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4701201275534574d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7267909714903812d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15101569268051382d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7534307642114654d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7534307642114654d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.757306878630505d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.757306878630505d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7573719383652521d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7573719383652521d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.2643708850737974d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.05057397493549234d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.031505236953005084d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8388576821100677d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2224543988772273d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07684261236335035d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.052474566363877734d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8107677218928129d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.21802017366829388d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.10620176208345242d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05189568193011499d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7864451750359873d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.215417072451554d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.11997497788610287d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.09321443989386448d), null);
    }
  }

  /**
   * make instance BIER127
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BIER127(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BIER127);
      i.setDescription(
          "127 Bierg&auml,rten (&quot,Beer Gardens&quot,, open-air beer restaurants) in Augsburg, Germany (J&uuml,nger/Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(118282L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(127L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6223451993175857d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7646689906374078d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.525002832828303d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5264040292029897d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.36255919038382206d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6746433601659166d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6746433601659166d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6756128940754392d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6756128940754392d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6771513280475743d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6771513280475743d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.856043665682348d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.12180513119212732d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.15378747615188845d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.7299240549646924d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4543074052226427d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.18022822122862223d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.20925974486404603d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.5451852392105834d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.4340782552501222d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.2160856193016933d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.22914064480935267d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.386889922979878d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.40294778536156023d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.24350532786233636d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.21644812037741093d), null);
    }
  }

  /**
   * make instance CH130
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __CH130(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.CH130);
      i.setDescription("130 city problem (Churritz).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6110L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(130L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.47719058647551854d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9755088901990364d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.546778125178904d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7214554957731003d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17263489426728557d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7353175037605364d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7353175037605364d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.747242747680654d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.747242747680654d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7478955895010984d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7478955895010984d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(24.36666810626658d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08080474304648134d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0453095532710308d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9707762891868303d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.27349411287663944d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.13196092591048836d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.05703873504684778d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8981556716386432d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2689201558182224d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.16435192392484485d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.06464795140034924d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8568463521710006d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.267033521323549d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.18311710877449539d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.17026867956213398d), null);
    }
  }

  /**
   * make instance PR136
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR136(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR136);
      i.setDescription("136-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(96772L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(136L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48493853623880706d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0606700967473872d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4226232527736395d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7973341526655174d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.12599337130819527d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7889267197941321d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7889267197941321d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7924535976392215d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7924535976392215d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8023317824750528d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8023317824750528d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.4705324492274212d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08981400604656822d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03887126820530995d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8961974763210514d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.212039050031588d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.1188481218679626d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0192429334699265d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8186391986173698d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.21026289616264623d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.1339075114951368d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.040497511514385985d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7819107515684578d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.20674296565773892d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.16994351715604694d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1404832835574206d), null);
    }
  }

  /**
   * make instance GR137
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR137(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR137);
      i.setDescription(
          "America-Subproblem of 666-city TSP (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(69853L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(137L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5954283934701101d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8292104104756398d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7352703264792317d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6007848084091297d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2250293570239643d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7393237122646875d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7393237122646875d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7415715041065841d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7415715041065841d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.743619492229201d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.743619492229201d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2.249015639677145d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.07465376109650677d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.04217723245813643d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.3154923059502655d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.40785544715340866d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.10776657884919484d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.053549167346459516d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.253679853324746d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3969306668007346d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.1349832727051644d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.057285122707694244d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.134877218130629d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.41995212983881913d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.14896506663948533d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.12312904200611138d), null);
    }
  }

  /**
   * make instance PR144
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR144(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR144);
      i.setDescription("144-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(58537L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(144L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49889296420582785d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0531944821875068d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3041915003769866d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7190341990521659d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1538297344318261d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7734740401203236d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7734740401203236d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7760445658368872d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7760445658368872d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7783013708557708d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7783013708557708d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.8728580050281076d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.022908809835536538d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.009706243025990416d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8267411571915846d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2729449361937468d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.04180082018260177d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04081629505464892d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8137782975302916d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2754963523986742d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.058656108778001904d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04104070372458122d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.802507369965001d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2763800264551569d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10655231751607055d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08107975031235583d), null);
    }
  }

  /**
   * make instance CH150
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __CH150(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.CH150);
      i.setDescription("150 city Problem (churritz).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6528L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(150L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.47132148576760213d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.017230820496524d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.419391842553289d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7458764976554538d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15952829227275506d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.741262867110505d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.741262867110505d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7450172790114553d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7450172790114553d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7451853870070203d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7451853870070203d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(31.855605940911662d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08931963830132723d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03990539244915916d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8622235432881826d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2772091199330329d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.12590840579825646d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.03960769933238847d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.832202941964637d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2765608136631729d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.15919453606676104d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04110747110141485d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8032213725781243d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.27471261991269874d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.17210821871273607d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.14624374513732058d), null);
    }
  }

  /**
   * make instance KROA150
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROA150(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROA150);
      i.setDescription("150-city problem A (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(26524L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(150L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5351550674599609d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9229319664725174d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.438842201358732d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.70573725133419d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1657972421445648d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7484282038535971d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7484282038535971d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7567248801808308d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7567248801808308d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7574869394760784d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7574869394760784d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(7.1633495593343275d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.06797282327206637d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.038368519179869456d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9404668857228906d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3055804466314673d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.10624490006471642d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0380070467648841d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9185455531690723d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30676956258127175d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.13266928658744376d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.03698611972345591d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9036621834132128d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3060808614399244d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.1495479750846993d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.12380292860038496d), null);
    }
  }

  /**
   * make instance KROB150
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROB150(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROB150);
      i.setDescription("150-city problem B (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(26130L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(150L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.538918665405434d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9444300031823639d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.412117115800664d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6812281990167871d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.14520926975548315d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7574425986408635d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7574425986408635d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7611441160172656d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7611441160172656d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7661082272445168d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7661082272445168d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(7.081034109848593d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.06705560133946831d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.04065238964660448d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9462171616461525d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2873704810966402d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.10600128894363556d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04198702212466241d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8795510561323086d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.290846033009454d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.13009939567500697d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04653379256072693d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8501869658384975d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.29569804785570786d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.14476391350975942d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.119781651254221d), null);
    }
  }

  /**
   * make instance PR152
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR152(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR152);
      i.setDescription("152-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(73682L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(152L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5305275376718183d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0324907391645994d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.2775436921044991d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.8162165042152811d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1992178517547997d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7201192036576273d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7201192036576273d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7213652055913271d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7213652055913271d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7221802306840086d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7221802306840086d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1.5832633395019309d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03055598079163943d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0221981462105775d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8319507377844655d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.37835671887366884d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.04304629452412923d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.023115006409161944d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8068369107519546d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.38369831375189145d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.05700180349946138d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.025759216796429573d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7974444383900459d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3813135222792641d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.08960430050366827d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.07719105223525004d), null);
    }
  }

  /**
   * make instance U159
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U159(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U159);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(42080L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(159L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5225336865341965d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9619393662008077d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4238117236486953d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6974060404955855d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15277924524254702d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7485180225953515d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7485180225953515d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7488425786454704d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7488425786454704d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7513942606946815d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7513942606946815d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(4.666820542788918d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.06890251037312388d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03466190548593521d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8965405971121818d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.27511894062512343d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.09591368236522203d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04856880000501437d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8434057249350382d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2681289038474648d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.1253937608882185d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05195899538603522d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8202691792283678d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2668484165553707d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.1299134168078449d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.10758871874421769d), null);
    }
  }

  /**
   * make instance FTV170
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FTV170(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FTV170);
      i.setDescription("Asymmetric TSP (Fischetti).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2755L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(171L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.44122655877404093d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0414800696469646d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5101461009880985d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6964897965764075d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1601921699608933d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6593870690952344d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6593870690952344d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6701464771676902d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6701464771676902d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6778044188562709d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6778044188562709d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(96.51265620409714d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0803568869526587d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03220229031763705d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8419275442279721d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.24845384621467573d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.12740620588846455d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.046730893674696204d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7872650668883143d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2500681838466024d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.16573845845185978d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.05373814958525167d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7460398141314553d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.24920523340693668d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.19181352598505313d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1559174841108906d), null);
    }
  }

  /**
   * make instance SI175
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __SI175(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.SI175);
      i.setDescription("TSP (M. Hofmeister).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(21407L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(175L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.2368289189356893d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0146515999165877d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.2364929891456624d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.8728185805734088d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.07979864260536111d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8752430766305571d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8752430766305571d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8777302751719421d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8777302751719421d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8781900933896771d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8781900933896771d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(50.027051643199215d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.409809104973991d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.07210287165753206d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.3484008000120389d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.10913605726443772d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.4531175316862525d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.046857949616327735d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.3464057861135854d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.11047602339939044d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.47830458215422805d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04209626189581609d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.3424573211062294d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.11291513951907894d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.48628463774804204d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.452577215422088d), null);
    }
  }

  /**
   * make instance BRG180
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BRG180(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BRG180);
      i.setDescription("Bridge tournament problem (Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1950L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.BRIDGE, "Bridge tournament problem");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(180L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.738826358724814d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.6921744331130062d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(0.6921744331130062d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.0059329237123971965d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16688332932987643d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.10502490281200698d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.10502490281200698d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(1.0044649762314777d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(1.0044649762314777d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(1.0287711813846878d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(1.0287711813846878d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.6594273458065905d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9776412374657322d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.003955282474931464d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9776412374657322d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0059329237123971965d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9776412374657322d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.13523769995536497d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02566538850399972d), null);
    }
  }

  /**
   * make instance RAT195
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RAT195(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RAT195);
      i.setDescription("Rattled grid (Pulleyblank).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2323L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(195L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5488454797113805d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9183192879243058d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3817701435122733d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7123411298852093d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18238250346871313d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7402118858054478d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7402118858054478d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7444146188410394d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7444146188410394d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7446800546117084d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7446800546117084d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(180.96897668384966d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.08432781000062162d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.016210060603303617d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0041058603227064d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3433802656479232d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.11095532829413732d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.01841338127386517d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9831999575302766d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3467391008823278d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.13978346793422455d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.020472810228707887d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.957892812044704d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.34691094569264863d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.1448448970313391d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1135080490561603d), null);
    }
  }

  /**
   * make instance D198
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D198(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D198);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(15780L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(198L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.8365408475579752d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.6128097315299704d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.4959013302822357d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.40403895858501443d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.4551116414630285d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.685943034343807d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.685943034343807d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6866284448668982d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6866284448668982d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6868235232465473d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6868235232465473d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(26.174246160263145d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.04574303080757869d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08453529573490429d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.97967584715569d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.5246316217011043d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.058049584738149744d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.08854846430470069d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.6767122002221004d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.44567138832248754d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.07266427325075459d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.09030420103646924d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.659721182242084d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.4523042067257203d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10370343761869531d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.09874619402773638d), null);
    }
  }

  /**
   * make instance KROA200
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROA200(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROA200);
      i.setDescription("200-city problem A (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(29368L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(200L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.539261373422585d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9346510788099526d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.386693015668351d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6959917467364679d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15943968513333587d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7560627442987421d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7560627442987421d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7565796809909824d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7565796809909824d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.757613554375463d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.757613554375463d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(12.778267390673427d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.05686381893627721d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.030415295725894548d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9838615761695138d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.29718510698477385d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.08793068026653256d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.033408362337669924d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9387484962680863d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3003044348227228d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.11292818789319496d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.03703537434421722d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9122872897821555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.30437131750848634d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.12226588121828687d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.10423416936766089d), null);
    }
  }

  /**
   * make instance KROB200
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __KROB200(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.KROB200);
      i.setDescription("200-city problem B (Krolak/Felts/Nelson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(29437L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(200L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.536222186535054d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9220758483708968d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4091025509480306d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.697640964456573d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17139687034226084d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7445048608912269d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7445048608912269d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7471681312942788d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7471681312942788d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7472647578848658d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7472647578848658d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(13.151223211108144d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.059521813724587286d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03562983061372351d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9684862188438883d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3271233248208221d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.08956363975215521d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.03512047992619829d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.919441037518354d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.32756629533501497d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.116249458211687d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0354858615746717d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9065277693648899d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.32554693036874766d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.12393492585630335d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.10034973390546743d), null);
    }
  }

  /**
   * make instance GR202
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR202(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR202);
      i.setDescription(
          "Europe-Subproblem of 666-city TSP (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(40160L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(202L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6238463282840224d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8184688021381655d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(3.385776716835755d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5957999224802535d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.35776070837931667d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6880129645016247d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6880129645016247d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6894988155091486d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6894988155091486d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6902661917256926d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6902661917256926d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(17.557951522635083d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.10799378903478084d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.14313274540330223d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(3.4795956660301535d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4885300523062259d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.14257935019800608d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.17458413489249752d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(3.0988213639117834d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.4712977108287189d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.1756489857907653d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.21614726157360475d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.616940440782478d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3795581065166435d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.194143277750451d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.1779060305290147d), null);
    }
  }

  /**
   * make instance TS225
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __TS225(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.TS225);
      i.setDescription(
          "225-city problem (J&uuml,nger,R&auml,cke,Tsch&ouml,cke).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(126643L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(225L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.46917756686128753d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0426516271128075d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3910967048813385d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7638390679255029d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15681372203766622d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7338250512956639d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7338250512956639d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7363838092680145d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7363838092680145d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7436628388473183d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7436628388473183d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.7101561827416996d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0706212155996212d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8868588938789568d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.28239178847788143d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.0706212155996212d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8531138799609723d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.271307422183828d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.11993679513301002d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.024965266025725318d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.83615223333429d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.27881194285379457d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10740074468390394d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08609573636181021d), null);
    }
  }

  /**
   * make instance TSP225
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __TSP225(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.TSP225);
      i.setDescription("A TSP problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(3916L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(225L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.518636541880969d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.942381079402026d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5688193691779393d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6754638950627239d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20475572458024896d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.726653825186105d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.726653825186105d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.729304515154489d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.729304515154489d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7293774699242611d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7293774699242611d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(145.3118940701066d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.07519680494855986d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.02767884057145055d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.079339338897927d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3617612365254864d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.09630807794120128d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.029413381617768995d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0419346178272493d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3638622633399634d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.11787934426739793d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.029720015441821596d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9816512809560598d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3519848436378791d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.13124336111595075d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.10800159268368498d), null);
    }
  }

  /**
   * make instance PR226
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR226(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR226);
      i.setDescription("226-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(80369L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(226L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49432840931423877d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.077167511771688d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4390222252658889d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7201108718265813d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.12896187327996522d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7811915328160709d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7811915328160709d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7819769952267005d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7819769952267005d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7829946607029158d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7829946607029158d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3.081962576368289d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.02551659861384164d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03645248333783184d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.855889659132966d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.23954301888951726d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03019495621769681d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04001136273280959d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8052097124037965d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2472833893028279d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.042138833256758816d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04391639789849252d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7936733413655241d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.24924650645450946d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.06918284220641097d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.05658200180283433d), null);
    }
  }

  /**
   * make instance GR229
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR229(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR229);
      i.setDescription(
          "Asia/Australia-Subproblem of 666-city TSP (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(134602L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(229L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6309343980777933d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.878295518656919d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.6695801419468097d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5967628783919152d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.3039161514393775d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7353843293838679d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7353843293838679d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7379922456301161d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7379922456301161d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7404996893911655d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7404996893911655d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(4.920181283876597d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.07031357137705302d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.06367763996903161d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.7773186553509337d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.34313020214224366d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.09643470070253864d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.08431316591493673d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.5839258890125136d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3839859771493408d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.11729318540743246d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.09294381155759555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.4390299151054236d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.4200369253845453d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.12898408519268104d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.11879586616479978d), null);
    }
  }

  /**
   * make instance GIL262
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GIL262(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GIL262);
      i.setDescription("262-city problem (Gillet/Johnson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2378L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(262L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4734111445496872d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0154868285123968d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6090805785123967d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7456714876033058d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15918591543553226d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7370629591368227d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7370629591368227d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7391305096418733d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7391305096418733d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7394312442607898d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7394312442607898d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(340.1831818181818d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.05898114669421488d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.03226758687488594d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9827401859504132d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.26486167538170513d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.0921978305785124d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.035860973549606566d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9124496384297522d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2599003458859523d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.11608987603305786d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.039154651802414606d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8709943181818183d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.25408953324358835d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.1278486570247934d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.10418130165289256d), null);
    }
  }

  /**
   * make instance PR264
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR264(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR264);
      i.setDescription("264-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(49135L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(264L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6020955937954181d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.3063598986135005d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4475879957609061d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(1.107934422121396d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1041736029497958d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8743844975897026d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8743844975897026d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8784289194592154d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8784289194592154d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8795440665380853d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8795440665380853d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(7.358925382027475d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.026733480207498772d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.013687867132725353d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8568980486790665d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.12698284035884705d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03380915052922737d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.018661697560483675d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8333199108693246d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.12418822146760265d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.05397516758276283d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.025166488678346255d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8203178316982347d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.12250637264238398d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.06092779827440088d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.050374921015330865d), null);
    }
  }

  /**
   * make instance A280
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __A280(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.A280);
      i.setDescription("Drilling problem (Ludwig).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2579L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(280L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5140740935425148d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9851591383430611d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3874324531664777d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7060307158125272d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15054939362868025d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7478440711167532d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7478440711167532d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7490210832294689d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7490210832294689d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7523461424478909d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7523461424478909d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(339.0917754176816d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.06840405564745124d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.011939600417254238d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.917717815549591d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3005283411302176d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07500110344885567d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.01886258669977735d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.8822110382718098d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3028924616406077d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0970499032117718d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0302704274082327d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8626837767796527d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.30134471354228837d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.11610804130471793d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.09124450114653591d), null);
    }
  }

  /**
   * make instance PR299
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR299(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR299);
      i.setDescription("299-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(48191L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(299L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5763077321264468d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8844343061403311d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4236755163816546d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7175450556714836d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18768797130215606d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7494021044879499d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7494021044879499d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7513833463812579d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7513833463812579d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7517888405554217d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7517888405554217d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(19.230837262752246d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.04808320788339374d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.017928048941754288d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.088129738840159d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3668680576203295d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.06306264232749212d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0221762315560726d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0602600133268445d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3661667467897506d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0821387460738169d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.028299102046346936d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.036001201661861d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3668579282824315d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.08737542682068598d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08163456087370356d), null);
    }
  }

  /**
   * make instance LIN318
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __LIN318(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.LIN318);
      i.setDescription(
          "The problem is posed by Lin and Kernighan as an open tour with fixed ends, but it can easily be converted to a TSP. Padberg and Gr&ouml,tschel used a combination of cutting-plane and branch-and-bound methods to find the optimal tour for this problem.");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(42029L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(318L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48776582968096366d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9840211871478062d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4758965758320215d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7414636150478935d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16387382937169154d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7346445116525353d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7346445116525353d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7346666903726102d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7346666903726102d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.73600423779867d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.73600423779867d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(28.595293349410884d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.039005335157102965d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.027084587371435355d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0129916225276694d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2929884119607956d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07708209333597274d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.047497139129894485d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.983967639541355d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.29216948455618486d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.09604649239142751d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04413598915788483d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9505592766101962d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.29016145692331013d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10899895075447509d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08585085322566198d), null);
    }
  }

  /**
   * make instance RBG323
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RBG323(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RBG323);
      i.setDescription("Stacker crane application (Ascheuer).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1326L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(323L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.38896185310685705d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.094286042094515d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3027214786839465d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7816328872103679d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.07605078168549394d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.9232330479368839d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.9232330479368839d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.924689506732928d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.924689506732928d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.9253368217533922d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.9253368217533922d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2467.510807204837d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.035330774468332424d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0757534016913467d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.4992186627133022d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.1656770006416607d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.06598304455501351d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.10869724369587205d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.4992186627133022d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.1656770006416607d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.12535165188079586d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.15994921414875538d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.4992186627133022d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.1656770006416607d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.7645321470567459d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.23731310156583102d), null);
    }
  }

  /**
   * make instance RBG358
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RBG358(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RBG358);
      i.setDescription("Stacker crane application (Ascheuer).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1163L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(358L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4180462847045838d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.086409588517095d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3968123280934077d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7242730590113966d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1330161837251978d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8464343612816262d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8464343612816262d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8467241864352922d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8467241864352922d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8468690990121253d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8468690990121253d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2950.3780396728534d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03092466772315221d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.07715637714060156d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.5001354001590799d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.16885082635227572d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07702265372168283d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.1315245535195798d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.5001354001590799d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.16885082635227572d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.12760038130627757d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.15520373100864404d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5001354001590799d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.16885082635227572d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.7049090147361519d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.22051889226883306d), null);
    }
  }

  /**
   * make instance RD400
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RD400(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RD400);
      i.setDescription("400-city random TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(15281L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(400L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4737445758459035d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.007802822138869d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5183220753799473d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7393075111750428d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15732576072552976d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7418617660483088d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7418617660483088d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7434255955625533d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7434255955625533d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7453543186301215d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7453543186301215d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(153.73058283523193d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.049964708395909226d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.026682045048472977d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9746412012352643d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.26499609364617827d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07446963255148098d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.02718752657499248d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9286708332929543d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2635114249346795d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0945311483918063d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.02772943118587131d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9058770940417646d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2604403113522012d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10247728602790124d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08693953801490657d), null);
    }
  }

  /**
   * make instance RBG403
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RBG403(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RBG403);
      i.setDescription("Stacker crane application (Ascheuer).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2465L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(403L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4291748316682641d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.2053978862646018d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6246667162696806d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5764946412569835d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.19885760099803873d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6779222375704012d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6779222375704012d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6781829768925437d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6781829768925437d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6783133465536149d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6783133465536149d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(3784.5300940408447d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03459227939868207d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.08092245268655453d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.48707787565387d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.16790992765704693d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.08283940592842284d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.13717712756507672d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.48707787565387d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.16790992765704693d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.14253059481562236d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.17341265829043417d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.48707787565387d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.16790992765704693d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.7605099621183937d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3152318994827268d), null);
    }
  }

  /**
   * make instance FL417
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FL417(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FL417);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(11861L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(417L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5759318333336015d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.006294675453053d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.282037911500339d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6607749497962404d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.09824206138924386d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7908015628191831d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7908015628191831d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7941602434763608d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7941602434763608d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7972642949140414d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7972642949140414d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(73.67220349929228d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0129105960376588d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.023052917445990563d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7598993106256267d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.22165101790827982d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.020192462509867354d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.03880024890805225d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7539782580705665d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2214914199124426d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.025718375023799703d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04587702483290169d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7511578452259875d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.22178796049558336d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.03423606213484099d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.030157652424631164d), null);
    }
  }

  /**
   * make instance GR431
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR431(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR431);
      i.setDescription(
          "Europe/Asia/Australia-Subproblem of 666-city TSP (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(171414L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(431L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.754417437718463d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.55984488755755d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.8885355092354787d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.36176196644780234d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.3824261711185586d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7220094188435457d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7220094188435457d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7242278505793526d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7242278505793526d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7248796411785978d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7248796411785978d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(18.050914335444954d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.050071430953620245d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05866209196698165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(3.0856532931426033d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2825429844520935d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.06783253563146256d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.07749146480308643d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.9738630716192485d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3570753896017781d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.08235152110149646d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0884523183333865d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.892569814994312d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.41076607741439175d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.09168359961708329d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08110458318682952d), null);
    }
  }

  /**
   * make instance PR439
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR439(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR439);
      i.setDescription("439-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(107217L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(439L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5987836369299391d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7553675751467355d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.343783187089413d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.4665437815370743d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.28140439704861103d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7183545502815515d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7183545502815515d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.719450242334406d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.719450242334406d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.719782844340844d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.719782844340844d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(23.309670634463984d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03525216356437566d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.02181043140013835d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.4013177550325517d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32354272274077917d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.0518874378657631d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.037236602961174876d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.3596817878902367d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.31293574733692875d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0699414518667386d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.046642512794332666d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.3304621559352494d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.31423526341114816d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.07960641916885458d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0655292869164653d), null);
    }
  }

  /**
   * make instance PCB442
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PCB442(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PCB442);
      i.setDescription(
          "Drilling problem (Gr&ouml,tschel/J&uuml,nger/Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(50778L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(442L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.47522337897948574d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0114485583362132d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.593832400183648d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7460005204018223d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15926054356628955d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7334613229730075d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7334613229730075d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7336312624374369d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7336312624374369d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7339841138444965d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7339841138444965d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(57.2669816666129d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.05725781275424003d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.013697079385973173d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0000783807698705d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2823705526109796d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.06519453927462973d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.020500899323437575d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9093004533018256d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2771940324469045d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.08293956872579458d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.029018482043621573d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8792025018169687d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2751186039839951d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.09977857916778253d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08256939524490231d), null);
    }
  }

  /**
   * make instance RBG443
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RBG443(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RBG443);
      i.setDescription("Stacker crane application (Ascheuer).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2720L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.FALSE,
          null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(443L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.42365362617872504d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.1262415722089414d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.608916531727059d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.589936061633255d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20637897174081105d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6780260617866368d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6780260617866368d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.678268733963368d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.678268733963368d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6783900700517335d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6783900700517335d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(4772.42184695986d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.04261389158524641d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.10684493091367198d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.5083138501721165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.17614773417154692d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.08631734289852469d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.14401978101259705d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.5083138501721165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.17614773417154692d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.13558965504396583d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.1760410442123068d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5083138501721165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.17614773417154692d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.7478011600058724d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.36124958093856613d), null);
    }
  }

  /**
   * make instance D493
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D493(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D493);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(35002L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(493L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6473853571464053d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7084107422275008d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(3.7547962560169705d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.46386647672172265d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.32757140336727575d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7048779606748863d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7048779606748863d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7049871760036422d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7049871760036422d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7058608986336895d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7058608986336895d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(148.8079270879062d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.05432562235828614d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.09959081036667598d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(3.7037115308365913d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4067364036529363d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07133977645149871d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.10721562709855896d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.350691455958739d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.31723882142642607d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.08726176821502539d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.11566226740670366d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.295907614781888d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.32140547459786367d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10662040111778771d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.08888332898557735d), null);
    }
  }

  /**
   * make instance ATT532
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ATT532(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ATT532);
      i.setDescription(
          "The so-called AT&amp,T532 is 532 city problem solved to optimality by Padberg and Rinaldi using branch-and-cut methods.");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(27686L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.ATT, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(532L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6607604148846142d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7640592838574832d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.129600995116031d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5027936007981263d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.32353544070851115d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7032564414269943d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7032564414269943d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7035714161734127d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7035714161734127d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7037905290404863d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7037905290404863d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(175.209026261595d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03812427088634273d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.02793429921547621d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.297043948839852d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.36264416867726434d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.05558324988628078d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0362172914638003d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.2761814355152423d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3622249973432483d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0679457144929923d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04385702245152385d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.2564202379445706d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.36326574335756545d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.0768304426497665d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.06316748816340408d), null);
    }
  }

  /**
   * make instance ALI535
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __ALI535(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.ALI535);
      i.setDescription("535 Airports around the globe (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(202310L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(535L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.648145084272302d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.6887807856553316d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.4994244476166645d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.4757995266644814d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.33987016754469246d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6831615484651332d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6831615484651332d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.6832310478397406d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.6832310478397406d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.6836029404116196d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.6836029404116196d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(21.891019674113615d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.035337373909351204d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.05143997657128645d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.7582413710485825d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.16773539401737925d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.056949785975171384d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.06526266324161194d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.6023064490084504d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.17890480673471065d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.06950056337415228d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.07045256374158619d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.5501264511096937d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.1945027408349024d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.08327026144682219d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.07012800314485956d), null);
    }
  }

  /**
   * make instance SI535
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __SI535(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.SI535);
      i.setDescription("TSP (M. Hofmeister).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(48450L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(535L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.35146102792917383d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.928579460120673d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4164201185174035d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7065278500918164d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.08257943303248956d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8902036697255487d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8902036697255487d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8902855750407902d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8902855750407902d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8903359783117081d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8903359783117081d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(487.5109454000209d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.2771148753195014d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.044539015746568246d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.5776292100804092d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.11534659443199029d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.3030932157621297d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.06192889044962105d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.5776292100804092d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.11534659443199029d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.3240595161822999d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.06857247718589707d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.5776292100804092d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.11534659443199029d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.3317253590773771d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.3107087495980104d), null);
    }
  }

  /**
   * make instance PA561
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PA561(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PA561);
      i.setDescription("561-city problem (Kleinschmidt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(2763L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(561L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48057179223288427d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9505557684936758d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.8091222690686088d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6899195093905711d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20669799455370236d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.6972841263757323d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.6972841263757323d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7060723867929694d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7060723867929694d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7074138969501177d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7074138969501177d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2540.9275584515144d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0574181378185617d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.024589852254593494d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.058007076833312d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2808603478068796d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.07868006605408812d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.03074674332734033d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0305141464927727d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.28180614288293376d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.09685382219370903d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.037963114408454304d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.001108189190168d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2811438304049348d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.10773074692088062d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0926998207903671d), null);
    }
  }

  /**
   * make instance U574
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U574(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U574);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(36905L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(574L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5304920861482108d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9326151637358383d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.566726016474853d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.691450663890947d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18511858724835725d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7341641434794091d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7341641434794091d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7344658235247367d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7344658235247367d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7347645603501098d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7347645603501098d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(151.0499272245282d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.035828567692777064d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.01959322522019006d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1506748177793047d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.33318632592961744d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.054137290442995624d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.02516966890650734d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.131785821781366d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3324699803254101d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.06864999782078662d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.027427459009907614d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0750615409858764d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.33361339612406404d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.07634192370702142d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.07050099602870408d), null);
    }
  }

  /**
   * make instance RAT575
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RAT575(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RAT575);
      i.setDescription("Rattled grid (Pulleyblank).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(6773L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(575L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5450564828045427d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9155666741304775d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4264629950541141d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7132314975270571d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18143391460433736d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7385586446332518d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7385586446332518d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7395720830178073d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7395720830178073d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7410437718023356d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7410437718023356d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(925.592382131177d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.04676581734016448d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.015380003835154052d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0568866222608237d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3390558163574806d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.063726783448486d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.013861303698281544d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.029808897322331d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.33995462571809987d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.07665864038792199d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013234540537383559d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0126719875821806d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.33957715539872224d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.08828851445182295d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.06843327559991338d), null);
    }
  }

  /**
   * make instance P654
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __P654(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.P654);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(34643L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(654L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5890756759644964d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0252222033692446d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.2938015411533001d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.666795536338432d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.0745052058923235d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8023743895898936d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8023743895898936d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8029871637069944d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8029871637069944d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.8060873977926309d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.8060873977926309d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(68.15433336676497d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.009919415986138519d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.020421599855878176d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.7825524657886118d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.1873272364722761d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.014180687581311543d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.030469518360798205d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.745903076833742d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.1853875807042909d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.016589765708231466d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.03443253152805131d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7415029520593595d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.18596805047605025d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.023863120932968933d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.024932731995430328d), null);
    }
  }

  /**
   * make instance D657
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D657(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D657);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(48912L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(657L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49537166974656993d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9789983209264682d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.3560509042076543d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.700821403991318d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18353266973617802d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7327915074855998d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7327915074855998d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7347372183863767d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7347372183863767d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7359168275838976d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7359168275838976d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(172.70329563445696d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.038987155630395d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.043163849117324085d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.5176511613257624d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.41855653031591156d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.05770234709956821d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.04606094123731409d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.048378349595508d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3000311268227878d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.07185716537092214d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.04851201232520406d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.018711946469197d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2955503632226568d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.07976150034677817d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.06650495512387904d), null);
    }
  }

  /**
   * make instance GR666
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __GR666(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.GR666);
      i.setDescription("666 cities around the world (Gr&ouml,tschel).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(294358L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.GEO, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(666L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5803273401869988d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7842363723579189d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.9874635487002168d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5531253288260116d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2724669743547534d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7121885220280032d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7121885220280032d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7123063639281357d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7123063639281357d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7125302635383874d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7125302635383874d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(29.252036280166273d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.042085618582234204d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.046011316113460404d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.4505759672972895d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.10792422537045865d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.05774054933926852d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.056386521220339804d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.399696461795592d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.12088792712236625d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.07006811112608434d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.06232517415297908d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.3692947776671d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.13105071049158495d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.08094015735346352d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.06897814137677838d), null);
    }
  }

  /**
   * make instance U724
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U724(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U724);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(41910L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(724L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5234778785663167d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9508810419991086d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.462000345551858d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7160648684513357d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17290202848241548d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7382428887427731d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7382428887427731d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7383186325636908d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7383186325636908d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7398277708137938d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7398277708137938d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(235.62102050304264d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03561804198158656d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.017976801497271433d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.008709039873171d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.33102347123133874d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.05012128601379409d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.021637930992369703d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9708481809361496d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.32729926362566136d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.06084715412105844d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.023452224964228566d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9649368626652586d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3273662044981862d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.06997428454164326d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0572726287843176d), null);
    }
  }

  /**
   * make instance RAT783
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RAT783(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RAT783);
      i.setDescription(
          "Rattled grid (Pulleyblank): The city positions of this problem are obtained by small, random displacements from a regular 27&times,29 lattice. This problem has been solved to optimality by Cook et al.");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(8806L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(783L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5433953226317241d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9248831335418306d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4309512632156625d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7154756316078312d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1814109982645941d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379081647833591d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379081647833591d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.738756149126229d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.738756149126229d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7391076163209712d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7391076163209712d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1482.5178605669098d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0358707294979536d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.01495185521058991d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0662219940588087d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.33763791597633364d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.05309848588311553d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.013911063961804412d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0430659282319223d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3378674113992287d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.06573514548258101d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013024039587137892d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0255317272797537d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.34083357775158296d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.07080541013668754d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0572382733974025d), null);
    }
  }

  /**
   * make instance DSJ1000
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __DSJ1000(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.DSJ1000);
      i.setDescription("Clustered random problem (Johnson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(18660188L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.CEIL_2D,
          "rounded-up Euclidean distance in 2D plane");//$NON-NLS-1$
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1000L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.51391100804169d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.010832249595504d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7886689194789693d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.654545146185616d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17776988015910455d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7334269852634631d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7334269852634631d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.733526014220241d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.733526014220241d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.733626368003197d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.733626368003197d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(0.8783517423265187d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.02230514451113796d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.01663332551913226d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9813671558134458d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.21313364043128036d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03282573228351222d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.019944449419608684d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9443799072994128d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2105869236596717d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.04070697963363294d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.021388793338061204d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9314305614768197d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.21111861540934132d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04640856771244064d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0398546052018091d), null);
    }
  }

  /**
   * make instance PR1002
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR1002(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR1002);
      i.setDescription("1002-city problem (Padberg/Rinaldi)");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(259045L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1002L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49115442862334896d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9903496465728413d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6613820383081226d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7352843065164643d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16032414539961848d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7365831183961786d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7365831183961786d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.736598641425696d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.736598641425696d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7366149406066892d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7366149406066892d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(80.97916659785616d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.02829288181597558d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.013525792030879582d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.054142215235709d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.31591386137205835d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.039939352608119d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.018633002220970318d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0165013898316744d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3114689042449548d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.050932180695822d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.021925520891786676d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.009890526607405d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3125338736579119d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.05664174584222471d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.04799133128965162d), null);
    }
  }

  /**
   * make instance SI1032
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __SI1032(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.SI1032);
      i.setDescription("TSP (M. Hofmeister).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(92650L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.MATRIX, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1032L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.3546877203747438d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.1790810852923583d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.3415198096319232d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(1.0306456992579285d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.07463598196684026d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.8788283236619286d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.8788283236619286d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8790809542074917d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8790809542074917d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.879148865644471d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.879148865644471d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1069.0708595672186d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.24467305715394422d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.01597982740544668d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.3872287683670759d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.07140877357277847d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.2518321281711818d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0165299356796478d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.3872287683670759d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.07140877357277847d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.2951122133049592d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013872915868743977d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.3872287683670759d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.07140877357277847d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.2697867960445873d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.25232333054196265d), null);
    }
  }

  /**
   * make instance U1060
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U1060(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U1060);
      i.setDescription("Drilling problem problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(224094L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1060L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5688779294653312d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9021186630381305d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6207396147141935d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6998006735876457d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18502928190091325d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7382636172701851d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7382636172701851d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7386186078790765d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7386186078790765d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390991854868584d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390991854868584d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(97.3598079217939d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.023133554400001602d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.016638069270246816d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.2463371991097443d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3760034846370654d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03223549659469626d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.020726293709421327d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0392192989559077d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3406861888684343d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.04039021064220501d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.021909795489617925d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9910753408827004d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.35141674605852385d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04683082939368929d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.03926247353851071d), null);
    }
  }

  /**
   * make instance VM1084
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __VM1084(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.VM1084);
      i.setDescription("1084-city problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(239297L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1084L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5247160296350792d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9734706698674584d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5241935546781602d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6840092041196523d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17274418747142947d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7436284201186774d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7436284201186774d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7445110545252264d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7445110545252264d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7450967529222695d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7450967529222695d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(78.34251175806813d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.01874874035280988d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.015093581163111212d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9963992137123026d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32209421849138975d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.028009893638990493d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.01930105302216597d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9892737241142586d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.321555473936058d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.036906606363542274d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.022706671605299617d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.977923687122404d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3208985088797078d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04124466229335336d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.035274673259628776d), null);
    }
  }

  /**
   * make instance PCB1173
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PCB1173(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PCB1173);
      i.setDescription("Drilling problem (J&uuml,nger/Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(56892L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1173L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49964662944009286d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9674830423953631d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5639450728317135d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7278999674083773d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1733043054519d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7339850833718995d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7339850833718995d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7343953478098927d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7343953478098927d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7344329672133765d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7344329672133765d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(604.9073338296415d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.032546374672322735d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.010694632304135367d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.072502389142848d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.30375198963321176d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.041222665697799865d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.014281035539620237d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0383823108751735d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.30470378756776517d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.05376761517872719d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.01691381278847562d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.013212982078853d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3047128551833466d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.05996881582665689d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.050046469272999015d), null);
    }
  }

  /**
   * make instance D1291
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D1291(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D1291);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(50801L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1291L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.505474523098553d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9616796783412661d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.1592994019898617d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6839274731060557d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2092091229239106d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7234499195140767d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7234499195140767d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7234690197532464d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7234690197532464d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7238591276684097d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7238591276684097d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(661.675937567293d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.02185456870856994d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.02688572943124326d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.412946112788394d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.35751058106573014d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.026929561839132584d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.02944662527813496d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0815366269065496d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.32527862959500137d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.043216964294164516d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.029060092186252644d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.071190581081748d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3276591021154823d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04509832641994461d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.035401764046495796d), null);
    }
  }

  /**
   * make instance RL1304
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL1304(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL1304);
      i.setDescription("1304-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(252948L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1304L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5105514701756415d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9499141083013197d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6435043949921955d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6955374019933968d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.193151321606278d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7303970848004157d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7303970848004157d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7304447967037282d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7304447967037282d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7305639163549513d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7305639163549513d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(126.21257022806952d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.016811361098486313d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.010609388627449048d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1514434876400994d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3246881713316296d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02691714122716554d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.014233200850164574d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.135586806048234d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.32325950417611243d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03678987759291705d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.015456477136415722d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.1262139672187947d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3230800044711379d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04099031810338948d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.031232237135874724d), null);
    }
  }

  /**
   * make instance RL1323
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL1323(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL1323);
      i.setDescription("1323-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(270199L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1323L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5031320580716868d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9688561716578294d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6443405874477088d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7239744988269853d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17476757651039088d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7351201129643222d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7351201129643222d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7352684650445456d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7352684650445456d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7353812616675254d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7353812616675254d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(123.71967793685748d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.017626362694461394d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.011683324130355322d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0842307877005317d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3007110736132506d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.027101365981968873d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.015472202429846897d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0674138232849004d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3005411243530705d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03857607715134312d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.02101016819387555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0515207041045276d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.29953753127892974d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04251781001553845d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.0325756262885853d), null);
    }
  }

  /**
   * make instance NRW1379
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __NRW1379(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.NRW1379);
      i.setDescription(
          "1379 Orte in Nordrhein-Westfalen (Bachem/Wottawa).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(56638L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1379L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5266369568100818d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9444602417872686d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7155272699541053d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6848547599421527d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17960069719380042d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.737653992393972d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.737653992393972d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7377784160944642d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7377784160944642d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7382648635562756d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7382648635562756d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(989.8950758188481d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03155260418408028d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.011106819603767075d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.091856594789917d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.34536750305040054d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.042287429802788025d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.011702060039681564d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.081378865505226d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.34559111953139765d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.049997510027296355d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.012949209196373582d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0504500360331868d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.341319716195805d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.05679019185774419d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.04602516093203445d), null);
    }
  }

  /**
   * make instance FL1400
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FL1400(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FL1400);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(20127L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1400L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.587953326338355d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0190689786673863d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5298472462500632d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6185723824946056d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.0850725512377626d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7979135298334245d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7979135298334245d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7987219708777961d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7987219708777961d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7987397518391826d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7987397518391826d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(826.6813591145735d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.005817328553104189d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0162082473384057d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8894235380312774d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.23075919098499362d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.010402724364357767d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.030124639416103816d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.7735632250370106d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.20260903649982734d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.01191006514705479d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.031938495701910034d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.7593983676071552d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.20462107696038118d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.01753549648460148d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.01446691786175147d), null);
    }
  }

  /**
   * make instance U1432
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U1432(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U1432);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(152970L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1432L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4846666247075633d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.003797061773449d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4919051774948116d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7348839660488916d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.14927084510366181d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.740686906205091d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.740686906205091d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7409496387460325d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7409496387460325d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7409765206469396d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7409765206469396d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(379.13480346202374d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.036496601352713647d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.002990034314579212d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9592876292009282d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2613791672294695d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03840793829731008d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007870576652846836d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.936599182815502d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2638090171698722d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.04492756893065507d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013556218023165768d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9224942663045759d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.26308554667584044d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.058683011343649664d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.04686374159794713d), null);
    }
  }

  /**
   * make instance FL1577
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FL1577(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FL1577);
      i.setDescription("Drilling problem (Reinelt)");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(22249L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1577L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5441158047595706d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8797184632898714d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.667508674463473d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6341885747261639d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.24689146550314897d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7032780048410484d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7032780048410484d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7032824349759251d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7032824349759251d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7033097541409986d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7033097541409986d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1576.0738094218527d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.011876301082903443d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.002429533293352158d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0640376698741587d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.36578227824786463d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.013168342288008377d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007968689396482867d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0585987003008697d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3664971863396521d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.020181226133420885d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.010504843058999076d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.055284109156763d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3659875096318934d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.024614454894283506d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.019307567203184307d), null);
    }
  }

  /**
   * make instance D1655
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D1655(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D1655);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(62128L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1655L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48613764017442757d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9989221936502456d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.463043671081208d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7309186782806675d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16810951788153283d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.737723167336457d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.737723167336457d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7377388183057371d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7377388183057371d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7380490757555817d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7380490757555817d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1058.0931061515585d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.02269721477236486d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.02755600260471563d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.5569654331157143d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4396809708474882d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02752308142339973d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.02982818870540011d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9647409662701274d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.27964236850885504d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.038617514231441386d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.02952618553701008d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9509718348244196d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2795832401480545d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04134971842977276d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.032887200122262136d), null);
    }
  }

  /**
   * make instance VM1748
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __VM1748(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.VM1748);
      i.setDescription("1784-city problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(336556L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1748L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.49544480313213085d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0126664540774082d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.426261122637528d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7147683283559133d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16134390030296192d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7409344993293968d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7409344993293968d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7415265141456302d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7415265141456302d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.741716419588322d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.741716419588322d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(182.3496838163721d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.01570120137788874d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.013742393659252748d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.9563422394113736d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.290932031475151d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02285866896762764d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.017154018151561326d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9458273895499498d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2903116712559564d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.02870201455910463d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0194300600999606d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9419403028773563d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.28999792488136245d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.03268453502521635d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02662114293087293d), null);
    }
  }

  /**
   * make instance U1817
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U1817(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U1817);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(57201L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1817L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5210949364066619d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9553111089733519d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.47412908108186d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7083366032423597d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1707511820704339d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379716739413926d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7379716739413926d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7379872571804428d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7379872571804428d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7383437828011348d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7383437828011348d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1523.4690990045626d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.021705416281737933d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.004702740045149751d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0443882762307686d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3142921726074156d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02674783131879138d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.010295497244587887d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0160886421562836d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3195326992941381d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0400485932594728d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.011398396343179532d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0081144148569523d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3200321010822451d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.04215636427739499d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.031477337223537974d), null);
    }
  }

  /**
   * make instance RL1889
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL1889(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL1889);
      i.setDescription("1889-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(316536L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(1889L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5121384326484995d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9640324604560594d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4725330989383765d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7113137696440645d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16809148099714855d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7424711454084628d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7424711454084628d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7425397631192503d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7425397631192503d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7427817166038891d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7427817166038891d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(240.80798233260415d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.013819926289507187d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.009133197977099758d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0500280981300683d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3054412995320964d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02095367421268807d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.012406712553227481d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.041657858224635d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3054893296650522d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.028609856890337505d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.013841537178197753d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.03388633719651d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3052478930853442d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.03214703477399353d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.025983291162135772d), null);
    }
  }

  /**
   * make instance D2103
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D2103(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D2103);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(80450L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(2103L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4860066306421775d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.001370662293879d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.1398799424212864d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7432654334468132d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.15446205303938407d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7400051814915136d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7400051814915136d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7402012151422146d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7402012151422146d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7403535489838111d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7403535489838111d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1466.73244926432d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.022077903747121177d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0162493830928929d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.319993803561228d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.33240811448017626d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.023387044232634904d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.01692187583887333d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0384858429428148d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.27321890850216957d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03411547511932376d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.016520232941777856d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0250668453675007d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.27524736004364725d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.034382163277439454d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02979217400938592d), null);
    }
  }

  /**
   * make instance U2152
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U2152(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U2152);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(64253L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(2152L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5206552284030517d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9576955030737812d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.4267708515180821d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.721458280487702d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1691509943592709d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7386702570380606d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7386702570380606d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7386900100408956d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7386900100408956d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390036877259163d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390036877259163d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2119.703912265606d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.021381771563663398d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0018415397417567458d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0194089732659903d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.317595708592521d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.025340013385229513d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.008711700088510091d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0093889826213136d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3173273245117209d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03634995936177644d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.010936855723844806d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0026125073719228d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.31772690162649203d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.039608085186770656d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02941237450911333d), null);
    }
  }

  /**
   * make instance U2319
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __U2319(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.U2319);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(234256L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(2319L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.47788445965822607d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0028543509264107d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.52013234689385d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7596795680687729d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1625727216485771d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7339793167592028d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7339793167592028d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7340418607699047d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7340418607699047d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7344259643876287d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7344259643876287d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(1065.6390643383031d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.03872722265835299d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0016045981771769379d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0248704248193663d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.27868669937671325d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.03876673342791833d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0021697136516059317d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.9975706503071153d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2790877480413259d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03978401071014506d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.005634574946890062d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.986962092034874d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.27894952148017865d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.06104447240267387d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.04443277783811826d), null);
    }
  }

  /**
   * make instance PR2392
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PR2392(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PR2392);
      i.setDescription("2392-city problem (Padberg/Rinaldi).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(378032L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(2392L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4902785448981942d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.0022088264241131d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5059015078527915d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7360093619630519d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16044065680525746d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7388717006326336d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7388717006326336d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7389329769469518d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7389329769469518d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390097363556802d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7390097363556802d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(463.6629957846241d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.01916268062842498d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.007134428569489762d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.027843814817668d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.28316602930333107d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.02513245949473522d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.00882498371636993d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.015976197143668d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2829691050615691d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.032730562568472336d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.011251286112770395d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.00225781161699d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2830120611313133d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.035404935887162446d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02945430606106842d), null);
    }
  }

  /**
   * make instance PCB3038
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PCB3038(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PCB3038);
      i.setDescription("Drilling problem (Junger/Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(137694L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(3038L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4847026804264028d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9975162864778107d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.5272008083842918d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7399838968459971d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16117499420933334d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7375295604756712d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7375295604756712d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7375693675305611d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7375693675305611d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7377548869212571d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7377548869212571d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2681.0606226847835d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0215103893088136d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.006013645656230315d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0568742250018865d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.28053855645350845d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.026053007727859113d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007980221748939767d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.033125934894284d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2828550755428949d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.03274283375312012d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0097346148053481d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0204857099954583d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2823648100661116d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.03676239306560887d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.030724262409840556d), null);
    }
  }

  /**
   * make instance FL3795
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FL3795(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FL3795);
      i.setDescription("Drilling problem (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(28772L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(3795L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5389529646634453d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8775056218040466d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.0019421734152703d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6782652149210021d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.21903543253103766d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7116402009779244d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7116402009779244d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7116435529739127d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7116435529739127d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7116594749548573d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7116594749548573d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(8165.798130001285d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.006056298466729155d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.009832267599870588d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.2192121252611483d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3337290229625536d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.0064978075678449754d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.011801749872515034d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0009862685113364d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.33149070049035245d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.009528051246660767d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.012253844671440512d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.9573408179019807d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.34604622576312183d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.011988326503352973d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.009450137875875623d), null);
    }
  }

  /**
   * make instance FNL4461
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __FNL4461(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.FNL4461);
      i.setDescription(
          "The new five provinces of Germany, i.e., the former GDR (Eastern Germany) (Bachem/Wottawa).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(182566L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(4461L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5107580408439958d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9618258322327542d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.743175436479741d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7063743166564603d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17814868119379038d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7349182395662451d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7349182395662451d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7350396361321548d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7350396361321548d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7351430213381372d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7351430213381372d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(5669.698725354445d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.017672506262991956d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.005411435823021165d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.0816069746535244d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3189473086319607d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.023166540330594423d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.005598677893794098d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0738505005982013d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3189177395551843d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.027640879754468814d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.005794502369862338d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0617548620859028d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3181132976288357d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.031344505779151516d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.02553318269066686d), null);
    }
  }

  /**
   * make instance RL5915
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL5915(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL5915);
      i.setDescription("5915-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(565530L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(5915L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5019374479694666d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9651647669597689d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7382417303569802d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7244295457077067d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1842273779614693d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7295585238731416d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7295585238731416d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7296817464495685d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7296817464495685d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7297720893036187d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7297720893036187d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2567.798345691706d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.009356447549097185d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0059192869360024875d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.138970360079699d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.3080023731674423d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.013708996218963885d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007572321653669352d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.1349889675322355d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3080678579643344d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.018920573338764273d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.00820431691849315d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.126941893927587d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.30815562811125224d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.02113030929562244d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.015271259190908828d), null);
    }
  }

  /**
   * make instance RL5934
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL5934(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL5934);
      i.setDescription("5934-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(556045L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(5934L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.519431239562321d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9309571603219668d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6567536081736143d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6943684703073593d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.20186595593358647d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7265860546773659d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7265860546773659d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7266586030855701d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7266586030855701d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.726705341230287d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.726705341230287d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2673.9356396115854d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.009072093924351857d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.005472946249975766d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1891366031630777d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.33645102277120315d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.013605830439688188d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007316339972309726d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.186364683074719d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3364758870109263d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0187606624592894d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.008030022543131414d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.1747897472554816d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.33620972297664514d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.02084736325725036d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.015029871387162193d), null);
    }
  }

  /**
   * make instance PLA7397
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PLA7397(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PLA7397);
      i.setDescription("Programmed logic array (Johnson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(23260728L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.CEIL_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(7397L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5694127277407203d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(1.1302296830432144d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.420738101704343d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6940035172377274d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.09227613757101726d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.805966916746786d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.805966916746786d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.8066435874065343d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.8066435874065343d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7690951671863323d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7690951671863323d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(68.55711551836833d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.006725770657941861d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0058375917814538466d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(1.8651713155555414d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.1610249892167643d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.008527332541464972d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.008306182734017809d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(1.862250446328539d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.1608900594095383d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.010450106622485841d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.01019487813424555d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(1.8605879699601906d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.16107408781798987d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.013096403975991237d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.009615293094406344d), null);
    }
  }

  /**
   * make instance RL11849
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __RL11849(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.RL11849);
      i.setDescription("11849-city TSP (Reinelt).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(923288L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(11849L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5022536955872554d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9616800365446863d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6741869909473932d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7100547316150878d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.19171613624718864d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7262899104621375d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7262899104621375d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7262914972594624d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7262914972594624d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7263158741459024d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7263158741459024d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(10065.088488559553d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.007973213569501073d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.004044200399423748d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1845544300487076d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32539731879669404d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.011046833534470838d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.004723784542156539d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.1809802637691917d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3252247462597058d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.01452159190229339d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.004784790102829211d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.173700445103507d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3254442221121084d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.0163968440879476d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.012427943547010108d), null);
    }
  }

  /**
   * make instance USA13509
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __USA13509(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.USA13509);
      i.setDescription(
          "Cities with population at least 500 in the continental US. Contributed by David Applegate and Andre Rohe, based on the data set &quot,US.lat-long&quot, from the ftp site <a href=\"ftp://ftp.cs.toronto.edu\">ftp.cs.toronto.edu</a>. The file US.lat-long.Z can be found in the directory /doc/geography.");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(19982859L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(13509L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.6858468840671569d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.7800994967079644d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(2.3677561355510233d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.5583430960113576d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.3488015886525892d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7003780906733347d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7003780906733347d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7004020032855021d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7004020032855021d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7004214316153076d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7004214316153076d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(695.0365266862315d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.006673810377873234d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.006030952186446156d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.6174908636992846d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.4666229869207471d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.009535323949060667d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.007760153663504487d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.5958224264051153d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.4669659668315992d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.011789171222764471d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.009025352240277205d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.591245339753088d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.466656702427718d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.013399641267467004d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.010858649814980718d), null);
    }
  }

  /**
   * make instance BRD14051
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __BRD14051(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.BRD14051);
      i.setDescription(
          "West-Germany in the borders of 1989 (Bachem/Wottawa).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(469385L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(14051L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5547000402635259d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.8955516729235286d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.8366341972555056d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6608669167667459d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.2125958726908856d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.727254605782227d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.727254605782227d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7273007847596341d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7273007847596341d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7273214914321661d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7273214914321661d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(35798.78793001588d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.008803569399683417d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.004161952273745773d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.1878973001224447d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32988457423872763d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.01173727449091591d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0046935654018683765d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.1741142137914236d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.3286062815991661d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.014017371970048069d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.005187970919958676d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.1683420856460245d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.32826432261179145d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.016058585302238047d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.013047110626363696d), null);
    }
  }

  /**
   * make instance D15112
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D15112(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D15112);
      i.setDescription("Germany-Problem (A.Rohe).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(1573084L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(15112L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.508528895658592d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9723385018045514d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.600039279007536d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7059660196354085d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.18317920133909713d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7265212102786746d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7265212102786746d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7265545477159275d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7265545477159275d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7265792578692172d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7265792578692172d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(13431.457326640804d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.009330215554906558d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.004975110505249923d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.083719626174816d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.32030988252239734d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.012265693480007275d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.005898769522274772d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0758880930649517d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.32028191748533696d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.014621532404606294d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.0066403809077561615d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.0709875901976686d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.3201531420307489d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.01680985190439538d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.013652027166117006d), null);
    }
  }

  /**
   * make instance D18512
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __D18512(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.D18512);
      i.setDescription(
          "Germany (united with GDR, the former East Germany) (Bachem/Wottawa).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(645238L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.EUC_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(18512L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.5005029021466361d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9704406940620631d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.6789620608756683d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.6923265927150085d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.17344379393484521d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7352313909166697d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7352313909166697d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7352484845891266d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7352484845891266d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.7353013471076891d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.7353013471076891d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(55651.948439255044d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.00864620561256336d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0037953526512281804d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.076558919669805d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2965255012087136d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.011469150510490514d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.0042345558589368905d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.0690646744975103d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2959181221910235d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.013680777275459516d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.004649423858796799d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.064754576312325d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.29567409789462024d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.015619576174556752d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.012576629426595369d), null);
    }
  }

  /**
   * make instance PLA33810
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PLA33810(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PLA33810);
      i.setDescription("Programmed logic array (Johnson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(66048945L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.CEIL_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(33810L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.48053743386505954d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9984509910233574d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.7181903708178903d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7513186224484298d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.16842704081342474d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7322791001059274d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7322791001059274d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.732285021131048d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.732285021131048d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.22824916743898277d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.22824916743898277d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(2110.796667519827d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.0064621291731706216d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0035693464315278895d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.2172730961301026d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.2837139290403452d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.007142456498093733d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.00543546367908824d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.19007981026188d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.28310223483327374d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.00860856154160108d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.005667504980788691d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.1839515398451805d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.2834412772483648d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.01162635686269778d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.008030870325371193d), null);
    }
  }

  /**
   * make instance PLA85900
   *
   * @param esb
   *          the context used for creating instances
   */
  private static final void __PLA85900(final IExperimentSetContext esb) {
    try (final IInstanceContext i = esb.createInstance()) {
      i.setName(TSPSuiteInput.PLA85900);
      i.setDescription("Programmed logic array (Johnson).");//$NON-NLS-1$
      i.setLowerBound(TSPSuiteInput.LENGTH, Long.valueOf(142382641L));
      i.setFeatureValue(TSPSuiteInput.SYMMETRIC, null, Boolean.TRUE, null);
      i.setFeatureValue(TSPSuiteInput.DISTANCE_TYPE, null,
          TSPSuiteInput.CEIL_2D, null);
      i.setFeatureValue(TSPSuiteInput.SCALE, null, Long.valueOf(85900L),
          null);
      i.setFeatureValue(TSPSuiteInput.DIST_CV, null,
          Double.valueOf(0.4786894982639533d), null);
      i.setFeatureValue(TSPSuiteInput.MED_MED_DIST, null,
          Double.valueOf(0.9926745476481007d), null);
      i.setFeatureValue(TSPSuiteInput.MAX_MED_DIST, null,
          Double.valueOf(1.8185031534671903d), null);
      i.setFeatureValue(TSPSuiteInput.MIN_MED_DIST, null,
          Double.valueOf(0.7471488415670915d), null);
      i.setFeatureValue(TSPSuiteInput.MEAN_DIST_CV, null,
          Double.valueOf(0.1716585607453908d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_1, null,
          Double.valueOf(0.7317815041568476d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_1, null,
          Double.valueOf(0.7317815041568476d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_2, null,
          Double.valueOf(0.7317927968254081d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_2, null,
          Double.valueOf(0.7317927968254081d), null);
      i.setFeatureValue(TSPSuiteInput.SMALLEST_MEAN_DIST_3, null,
          Double.valueOf(0.07443857127202039d), null);
      i.setFeatureValue(TSPSuiteInput.LARGEST_MEAN_DIST_3, null,
          Double.valueOf(0.07443857127202039d), null);
      i.setFeatureValue(TSPSuiteInput.FRAC_DIST_BELOW_MEAN, null,
          Double.valueOf(-1433.6386979044082d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_MEAN, null,
          Double.valueOf(0.004588612428267869d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_1_STDDEV, null,
          Double.valueOf(0.0010785672087782804d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_MEAN, null,
          Double.valueOf(2.3410674164348975d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_1_STDDEV, null,
          Double.valueOf(0.28085623858063336d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_MEAN, null,
          Double.valueOf(0.005105856922914861d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_2_STDDEV, null,
          Double.valueOf(0.003726914626830628d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_MEAN, null,
          Double.valueOf(2.338763489607107d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_2_STDDEV, null,
          Double.valueOf(0.2808552382950924d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_MEAN, null,
          Double.valueOf(0.0058969493677349305d), null);
      i.setFeatureValue(TSPSuiteInput.NEAR_3_STDDEV, null,
          Double.valueOf(0.003948474787610199d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_MEAN, null,
          Double.valueOf(2.297095697144837d), null);
      i.setFeatureValue(TSPSuiteInput.FAR_3_STDDEV, null,
          Double.valueOf(0.279538536059904d), null);
      i.setFeatureValue(TSPSuiteInput.DMST, null,
          Double.valueOf(0.00806126486885101d), null);
      i.setFeatureValue(TSPSuiteInput.EDGE_GREEDY, null,
          Double.valueOf(0.005596467594650125d), null);
    }
  }

  /**
   * fill in the TSPLib instance set used in TSPSuite
   *
   * @param esb
   *          the experiment set builder
   */
  public static final void makeTSPLibInstanceSet(
      final IExperimentSetContext esb) {
    TSPSuiteInput.__BURMA14(esb);
    TSPSuiteInput.__ULYSSES16(esb);
    TSPSuiteInput.__GR17(esb);
    TSPSuiteInput.__BR17(esb);
    TSPSuiteInput.__GR21(esb);
    TSPSuiteInput.__ULYSSES22(esb);
    TSPSuiteInput.__GR24(esb);
    TSPSuiteInput.__FRI26(esb);
    TSPSuiteInput.__BAYG29(esb);
    TSPSuiteInput.__BAYS29(esb);
    TSPSuiteInput.__FTV33(esb);
    TSPSuiteInput.__FTV35(esb);
    TSPSuiteInput.__FTV38(esb);
    TSPSuiteInput.__DANTZIG42(esb);
    TSPSuiteInput.__SWISS42(esb);
    TSPSuiteInput.__P43(esb);
    TSPSuiteInput.__FTV44(esb);
    TSPSuiteInput.__ATT48(esb);
    TSPSuiteInput.__GR48(esb);
    TSPSuiteInput.__HK48(esb);
    TSPSuiteInput.__FTV47(esb);
    TSPSuiteInput.__RY48P(esb);
    TSPSuiteInput.__EIL51(esb);
    TSPSuiteInput.__BERLIN52(esb);
    TSPSuiteInput.__FT53(esb);
    TSPSuiteInput.__FTV55(esb);
    TSPSuiteInput.__BRAZIL58(esb);
    TSPSuiteInput.__FTV64(esb);
    TSPSuiteInput.__ST70(esb);
    TSPSuiteInput.__FT70(esb);
    TSPSuiteInput.__FTV70(esb);
    TSPSuiteInput.__EIL76(esb);
    TSPSuiteInput.__PR76(esb);
    TSPSuiteInput.__GR96(esb);
    TSPSuiteInput.__RAT99(esb);
    TSPSuiteInput.__KROA100(esb);
    TSPSuiteInput.__KROB100(esb);
    TSPSuiteInput.__KROC100(esb);
    TSPSuiteInput.__KROD100(esb);
    TSPSuiteInput.__KROE100(esb);
    TSPSuiteInput.__RD100(esb);
    TSPSuiteInput.__KRO124P(esb);
    TSPSuiteInput.__EIL101(esb);
    TSPSuiteInput.__LIN105(esb);
    TSPSuiteInput.__PR107(esb);
    TSPSuiteInput.__GR120(esb);
    TSPSuiteInput.__PR124(esb);
    TSPSuiteInput.__BIER127(esb);
    TSPSuiteInput.__CH130(esb);
    TSPSuiteInput.__PR136(esb);
    TSPSuiteInput.__GR137(esb);
    TSPSuiteInput.__PR144(esb);
    TSPSuiteInput.__CH150(esb);
    TSPSuiteInput.__KROA150(esb);
    TSPSuiteInput.__KROB150(esb);
    TSPSuiteInput.__PR152(esb);
    TSPSuiteInput.__U159(esb);
    TSPSuiteInput.__FTV170(esb);
    TSPSuiteInput.__SI175(esb);
    TSPSuiteInput.__BRG180(esb);
    TSPSuiteInput.__RAT195(esb);
    TSPSuiteInput.__D198(esb);
    TSPSuiteInput.__KROA200(esb);
    TSPSuiteInput.__KROB200(esb);
    TSPSuiteInput.__GR202(esb);
    TSPSuiteInput.__TS225(esb);
    TSPSuiteInput.__TSP225(esb);
    TSPSuiteInput.__PR226(esb);
    TSPSuiteInput.__GR229(esb);
    TSPSuiteInput.__GIL262(esb);
    TSPSuiteInput.__PR264(esb);
    TSPSuiteInput.__A280(esb);
    TSPSuiteInput.__PR299(esb);
    TSPSuiteInput.__LIN318(esb);
    TSPSuiteInput.__RBG323(esb);
    TSPSuiteInput.__RBG358(esb);
    TSPSuiteInput.__RD400(esb);
    TSPSuiteInput.__RBG403(esb);
    TSPSuiteInput.__FL417(esb);
    TSPSuiteInput.__GR431(esb);
    TSPSuiteInput.__PR439(esb);
    TSPSuiteInput.__PCB442(esb);
    TSPSuiteInput.__RBG443(esb);
    TSPSuiteInput.__D493(esb);
    TSPSuiteInput.__ATT532(esb);
    TSPSuiteInput.__ALI535(esb);
    TSPSuiteInput.__SI535(esb);
    TSPSuiteInput.__PA561(esb);
    TSPSuiteInput.__U574(esb);
    TSPSuiteInput.__RAT575(esb);
    TSPSuiteInput.__P654(esb);
    TSPSuiteInput.__D657(esb);
    TSPSuiteInput.__GR666(esb);
    TSPSuiteInput.__U724(esb);
    TSPSuiteInput.__RAT783(esb);
    TSPSuiteInput.__DSJ1000(esb);
    TSPSuiteInput.__PR1002(esb);
    TSPSuiteInput.__SI1032(esb);
    TSPSuiteInput.__U1060(esb);
    TSPSuiteInput.__VM1084(esb);
    TSPSuiteInput.__PCB1173(esb);
    TSPSuiteInput.__D1291(esb);
    TSPSuiteInput.__RL1304(esb);
    TSPSuiteInput.__RL1323(esb);
    TSPSuiteInput.__NRW1379(esb);
    TSPSuiteInput.__FL1400(esb);
    TSPSuiteInput.__U1432(esb);
    TSPSuiteInput.__FL1577(esb);
    TSPSuiteInput.__D1655(esb);
    TSPSuiteInput.__VM1748(esb);
    TSPSuiteInput.__U1817(esb);
    TSPSuiteInput.__RL1889(esb);
    TSPSuiteInput.__D2103(esb);
    TSPSuiteInput.__U2152(esb);
    TSPSuiteInput.__U2319(esb);
    TSPSuiteInput.__PR2392(esb);
    TSPSuiteInput.__PCB3038(esb);
    TSPSuiteInput.__FL3795(esb);
    TSPSuiteInput.__FNL4461(esb);
    TSPSuiteInput.__RL5915(esb);
    TSPSuiteInput.__RL5934(esb);
    TSPSuiteInput.__PLA7397(esb);
    TSPSuiteInput.__RL11849(esb);
    TSPSuiteInput.__USA13509(esb);
    TSPSuiteInput.__BRD14051(esb);
    TSPSuiteInput.__D15112(esb);
    TSPSuiteInput.__D18512(esb);
    TSPSuiteInput.__PLA33810(esb);
    TSPSuiteInput.__PLA85900(esb);
  }

  /** {@inheritDoc} */
  @Override
  protected void before(final IOJob job, final IExperimentSetContext data)
      throws Throwable {
    super.before(job, data);
    TSPSuiteInput.makeTSPSuiteDimensionSet(data);
    TSPSuiteInput.makeTSPLibInstanceSet(data);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean isFileInDirectoryLoadable(final IOJob job,
      final IExperimentSetContext data, final Path path,
      final BasicFileAttributes attributes) throws Throwable {
    final String name;
    int len;
    char ch;

    if (super.isFileInDirectoryLoadable(job, data, path, attributes)) {

      name = TextUtils.normalize(path.getFileName().toString());
      if (name != null) {
        len = name.length();
        if (len > 4) {
          ch = name.charAt(--len);
          if ((ch == 't') || (ch == 'T')) {
            ch = name.charAt(--len);
            if ((ch == 'x') || (ch == 'X')) {
              ch = name.charAt(--len);
              if ((ch == 't') || (ch == 'T')) {
                ch = name.charAt(--len);
                if (ch == '.') {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  protected void leaveDirectory(final IOJob job,
      final IExperimentSetContext data, final Path path) throws Throwable {
    _TSPSuiteInputToken token;

    token = ((_TSPSuiteInputToken) (job.getToken()));

    if (token.m_instanceRunsRoot != null) {
      if (Files.isSameFile(token.m_instanceRunsRoot, path)) {
        token._popIRSC();
      }
    }

    if (token.m_experimentRoot != null) {
      if (Files.isSameFile(token.m_experimentRoot, path)) {
        token._popEC();
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void file(final IOJob job, final IExperimentSetContext data,
      final Path path, final BasicFileAttributes attributes,
      final StreamEncoding<?, ?> encoding) throws Throwable {
    final Logger logger;

    logger = job.getLogger();
    if ((logger != null) && (logger.isLoggable(IOTool.FINER_LOG_LEVEL))) {
      logger.log(IOTool.FINER_LOG_LEVEL, //
          (("Beginning to load run from file '" //$NON-NLS-1$
              + path) + '\''));
    }

    try (final InputStream stream = PathUtils.openInputStream(path)) {
      try (final InputStream input = StreamEncoding.openInputStream(stream,
          encoding)) {
        try (final Reader reader = StreamEncoding.openReader(stream,
            encoding)) {
          if (reader instanceof BufferedReader) {
            this.__reader(job, data, path, ((BufferedReader) reader));
          } else {
            try (final BufferedReader buffered = new BufferedReader(
                reader)) {
              this.__reader(job, data, path, buffered);
            }
          }
        }
      }
    }
    if ((logger != null) && (logger.isLoggable(IOTool.FINER_LOG_LEVEL))) {
      logger.log(IOTool.FINER_LOG_LEVEL, //
          (("Finished loading run from file '" //$NON-NLS-1$
              + path) + '\''));
    }
  }

  /**
   * prepare a string for processing
   *
   * @param s
   *          the string
   * @return the result
   */
  private static final String __prepare(final String s) {
    int i;
    String t;

    t = TextUtils.normalize(s);
    if (t == null) {
      return null;
    }
    i = t.indexOf(TSPSuiteInput.COMMENT_START);
    if (i == 0) {
      return null;
    }
    if (i < 0) {
      return t;
    }
    return TextUtils.prepare(t.substring(0, i));
  }

  /**
   * load the file data
   *
   * @param job
   *          the job
   * @param data
   *          the data
   * @param file
   *          the file
   * @param reader
   *          the reader
   * @throws Throwable
   *           if it fails
   */
  @SuppressWarnings({ "resource", "null" })
  private final void __reader(final IOJob job,
      final IExperimentSetContext data, final Path file,
      final BufferedReader reader) throws Throwable {
    String s;
    IRunContext run;
    int state, idx;
    _TSPSuiteInputToken token;

    token = ((_TSPSuiteInputToken) (job.getToken()));

    run = null;
    state = 0;

    while ((s = reader.readLine()) != null) {
      s = TSPSuiteInput.__prepare(s);
      if (s == null) {
        continue;
      }

      if (state == 0) {
        if (TSPSuiteInput.LOG_DATA_SECTION.equalsIgnoreCase(s)) {
          state = 1;
          if (run == null) {
            run = token._beginRun(file);
          }
        } else {
          if (TSPSuiteInput.ALGORITHM_DATA_SECTION.equalsIgnoreCase(s) || //
              TSPSuiteInput.DETERMINISTIC_INITIALIZATION_SECTION
                  .equalsIgnoreCase(s)) {
            state = 2;
            if (run == null) {
              run = token._beginRun(file);
            }
          }
        }

      } else {
        if (TSPSuiteInput.SECTION_END.equalsIgnoreCase(s)) {
          state = 0;
        } else {
          if (state == 1) {
            run.addDataPoint(s);
          } else {
            if (state == 2) {
              idx = s.indexOf(':');
              if (idx <= 0) {
                continue;
              }
              run.setParameterValue(TextUtils.prepare(s.substring(0, idx)),
                  TextUtils.prepare(s.substring(idx + 1)));
            }
          }

        }
      }
    }

    if (run != null) {
      run.close();
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "TSPSuite Experimet Data Input"; //$NON-NLS-1$
  }

  /** the loader */
  private static final class __TSPSuiteInputLoader {

    /** the globally shared instance */
    static final TSPSuiteInput INSTANCE = new TSPSuiteInput();

    /** the list of all instance names */
    static final String[] ALL = { TSPSuiteInput.BURMA14,
        TSPSuiteInput.ULYSSES16, TSPSuiteInput.BR17, TSPSuiteInput.GR17,
        TSPSuiteInput.GR21, TSPSuiteInput.ULYSSES22, TSPSuiteInput.GR24,
        TSPSuiteInput.FRI26, TSPSuiteInput.BAYG29, TSPSuiteInput.BAYS29,
        TSPSuiteInput.FTV33, TSPSuiteInput.FTV35, TSPSuiteInput.FTV38,
        TSPSuiteInput.DANTZIG42, TSPSuiteInput.SWISS42, TSPSuiteInput.P43,
        TSPSuiteInput.FTV44, TSPSuiteInput.ATT48, TSPSuiteInput.FTV47,
        TSPSuiteInput.GR48, TSPSuiteInput.HK48, TSPSuiteInput.RY48P,
        TSPSuiteInput.EIL51, TSPSuiteInput.BERLIN52, TSPSuiteInput.FT53,
        TSPSuiteInput.FTV55, TSPSuiteInput.BRAZIL58, TSPSuiteInput.FTV64,
        TSPSuiteInput.FT70, TSPSuiteInput.ST70, TSPSuiteInput.FTV70,
        TSPSuiteInput.EIL76, TSPSuiteInput.PR76, TSPSuiteInput.GR96,
        TSPSuiteInput.RAT99, TSPSuiteInput.KRO124P, TSPSuiteInput.KROA100,
        TSPSuiteInput.KROB100, TSPSuiteInput.KROC100,
        TSPSuiteInput.KROD100, TSPSuiteInput.KROE100, TSPSuiteInput.RD100,
        TSPSuiteInput.EIL101, TSPSuiteInput.LIN105, TSPSuiteInput.PR107,
        TSPSuiteInput.GR120, TSPSuiteInput.PR124, TSPSuiteInput.BIER127,
        TSPSuiteInput.CH130, TSPSuiteInput.PR136, TSPSuiteInput.GR137,
        TSPSuiteInput.PR144, TSPSuiteInput.CH150, TSPSuiteInput.KROA150,
        TSPSuiteInput.KROB150, TSPSuiteInput.PR152, TSPSuiteInput.U159,
        TSPSuiteInput.FTV170, TSPSuiteInput.SI175, TSPSuiteInput.BRG180,
        TSPSuiteInput.RAT195, TSPSuiteInput.D198, TSPSuiteInput.KROA200,
        TSPSuiteInput.KROB200, TSPSuiteInput.GR202, TSPSuiteInput.TS225,
        TSPSuiteInput.TSP225, TSPSuiteInput.PR226, TSPSuiteInput.GR229,
        TSPSuiteInput.GIL262, TSPSuiteInput.PR264, TSPSuiteInput.A280,
        TSPSuiteInput.PR299, TSPSuiteInput.LIN318, TSPSuiteInput.RBG323,
        TSPSuiteInput.RBG358, TSPSuiteInput.RD400, TSPSuiteInput.RBG403,
        TSPSuiteInput.FL417, TSPSuiteInput.GR431, TSPSuiteInput.PR439,
        TSPSuiteInput.PCB442, TSPSuiteInput.RBG443, TSPSuiteInput.D493,
        TSPSuiteInput.ATT532, TSPSuiteInput.ALI535, TSPSuiteInput.SI535,
        TSPSuiteInput.PA561, TSPSuiteInput.U574, TSPSuiteInput.RAT575,
        TSPSuiteInput.P654, TSPSuiteInput.D657, TSPSuiteInput.GR666,
        TSPSuiteInput.U724, TSPSuiteInput.RAT783, TSPSuiteInput.DSJ1000,
        TSPSuiteInput.PR1002, TSPSuiteInput.SI1032, TSPSuiteInput.U1060,
        TSPSuiteInput.VM1084, TSPSuiteInput.PCB1173, TSPSuiteInput.D1291,
        TSPSuiteInput.RL1304, TSPSuiteInput.RL1323, TSPSuiteInput.NRW1379,
        TSPSuiteInput.FL1400, TSPSuiteInput.U1432, TSPSuiteInput.FL1577,
        TSPSuiteInput.D1655, TSPSuiteInput.VM1748, TSPSuiteInput.U1817,
        TSPSuiteInput.RL1889, TSPSuiteInput.D2103, TSPSuiteInput.U2152,
        TSPSuiteInput.U2319, TSPSuiteInput.PR2392, TSPSuiteInput.PCB3038,
        TSPSuiteInput.FL3795, TSPSuiteInput.FNL4461, TSPSuiteInput.RL5915,
        TSPSuiteInput.RL5934, TSPSuiteInput.PLA7397, TSPSuiteInput.RL11849,
        TSPSuiteInput.USA13509, TSPSuiteInput.BRD14051,
        TSPSuiteInput.D15112, TSPSuiteInput.D18512, TSPSuiteInput.PLA33810,
        TSPSuiteInput.PLA85900 };

    static {
      Arrays.sort(__TSPSuiteInputLoader.ALL);
    }
  }

}
