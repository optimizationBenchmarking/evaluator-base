package test.junit.org.optimizationBenchmarking.experimentation.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomExample;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** Test the random data. */
public class RandomTest extends ExperimentSetTest {

  /** create */
  public RandomTest() {
    super(new RandomExample(TestBase.getNullLogger()));
  }
}