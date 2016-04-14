package org.optimizationBenchmarking.evaluator.evaluation.spec;

import org.optimizationBenchmarking.utils.tools.spec.IConfigurableJobTool;

/**
 * The evaluator is a tool which can be used to create a document
 * summarizing the properties of an
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet}
 * .
 */
public interface IEvaluator extends IConfigurableJobTool {

  /**
   * Create a builder for a new evaluation process
   *
   * @return the evaluation process builder
   */
  @Override
  public abstract IEvaluationBuilder use();

}
