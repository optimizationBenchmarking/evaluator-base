package test.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomBBOBParallelExample;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/**
 * A class for creating in parallel data sets according to the BBOB data
 * format
 */
public class RandomBBOBParallelTest extends ExperimentSetTest {

  /** create */
  public RandomBBOBParallelTest() {
    super(new RandomBBOBParallelExample(true, TestBase.getNullLogger()));
  }
}