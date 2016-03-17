package test.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.BBOBExample;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** Test the BBOB example data. */
public class BBOBExampleTest extends ExperimentSetTest {

  /** create */
  public BBOBExampleTest() {
    super(new BBOBExample(TestBase.getNullLogger()));
  }
}
