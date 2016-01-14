package test.junit.org.optimizationBenchmarking.experimentation.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.Example2Random;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** Test the second example data. */
public class Example2RandomTest extends ExperimentSetTest {

  /** create */
  public Example2RandomTest() {
    super(new Example2Random(TestBase.getNullLogger()));
  }

}
