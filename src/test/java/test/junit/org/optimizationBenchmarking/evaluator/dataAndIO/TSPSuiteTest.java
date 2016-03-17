package test.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import org.junit.experimental.categories.Category;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.TSPSuiteExample;
import shared.junit.CategorySlowTests;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** A class for loading a TSPSuite experiment set */
@Category(CategorySlowTests.class)
public class TSPSuiteTest extends ExperimentSetTest {

  /** create */
  public TSPSuiteTest() {
    super(new TSPSuiteExample(TestBase.getNullLogger()));
  }
}
