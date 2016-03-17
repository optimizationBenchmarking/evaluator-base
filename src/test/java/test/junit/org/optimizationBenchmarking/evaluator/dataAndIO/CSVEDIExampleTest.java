package test.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import examples.org.optimizationBenchmarking.evaluator.dataAndIO.CSVEDIExample;
import shared.junit.TestBase;
import shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO.ExperimentSetTest;

/** A class for loading experiment sets from a CSV/EDI mixture */
public class CSVEDIExampleTest extends ExperimentSetTest {

  /** create */
  public CSVEDIExampleTest() {
    super(new CSVEDIExample(TestBase.getNullLogger()));
  }
}
