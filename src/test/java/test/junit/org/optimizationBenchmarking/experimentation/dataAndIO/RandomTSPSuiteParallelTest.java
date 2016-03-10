package test.junit.org.optimizationBenchmarking.experimentation.dataAndIO;

import org.junit.experimental.categories.Category;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomTSPSuiteParallelExample;
import shared.junit.CategorySlowTests;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/**
 * A class for creating in parallel sets according to the TSP Suite format
 */
@Category(CategorySlowTests.class)
public class RandomTSPSuiteParallelTest extends ExperimentSetTest {

  /** create */
  public RandomTSPSuiteParallelTest() {
    super(
        new RandomTSPSuiteParallelExample(true, TestBase.getNullLogger()));
  }
}