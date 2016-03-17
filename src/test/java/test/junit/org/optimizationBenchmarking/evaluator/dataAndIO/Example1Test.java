package test.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.Example1;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** Test the first example. */
public class Example1Test extends ExperimentSetTest {

  /** create */
  public Example1Test() {
    super(new Example1(TestBase.getNullLogger()));
  }
}
