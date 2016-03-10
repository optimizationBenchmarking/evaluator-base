package test.junit.org.optimizationBenchmarking.experimentation.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.RandomParallelExample;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** A class for creating in parallel sets */
public class RandomParallelTest extends ExperimentSetTest {

  /** create */
  public RandomParallelTest() {
    super(new RandomParallelExample(true, TestBase.getNullLogger()));
  }
}