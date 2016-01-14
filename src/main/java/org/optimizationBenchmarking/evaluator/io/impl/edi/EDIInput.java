package org.optimizationBenchmarking.evaluator.io.impl.edi;

import org.optimizationBenchmarking.evaluator.data.spec.builders.IExperimentSetContext;
import org.optimizationBenchmarking.evaluator.io.spec.IExperimentSetInput;

/**
 * A driver for Experiment Data Interchange (EDI) input. EDI is our
 * default, canonical format for storing and exchanging
 * {@link org.optimizationBenchmarking.evaluator.data experiment data
 * structures}.
 */
public final class EDIInput extends EDIInputToolBase<IExperimentSetContext>
    implements IExperimentSetInput {

  /** create */
  EDIInput() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "EDI Experiment Data Input"; //$NON-NLS-1$
  }

  /**
   * get the instance of the {@link EDIInput}
   *
   * @return the instance of the {@link EDIInput}
   */
  public static final EDIInput getInstance() {
    return __EDIInputLoader.INSTANCE;
  }

  /** the loader */
  private static final class __EDIInputLoader {
    /** create */
    static final EDIInput INSTANCE = new EDIInput();
  }
}
