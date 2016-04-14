package org.optimizationBenchmarking.evaluator.evaluation.impl.abstr;

import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleRelationship;
import org.optimizationBenchmarking.evaluator.evaluation.spec.EModuleType;
import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationModule;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.tools.impl.abstr.Tool;

/**
 * The abstract base-class for evaluation modules.
 */
public abstract class EvaluationModule extends Tool
    implements IEvaluationModule {

  /** the module type */
  private final EModuleType m_type;

  /**
   * create the module
   *
   * @param type
   *          the module type
   */
  protected EvaluationModule(final EModuleType type) {
    super();
    if (type == null) {
      throw new IllegalArgumentException(
          "Evaluation module type cannot be null."); //$NON-NLS-1$
    }
    this.m_type = type;
  }

  /** {@inheritDoc} */
  @Override
  public final EModuleType getType() {
    return this.m_type;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Iterable<Class<? extends IEvaluationModule>> getRequiredModules() {
    return ((Iterable) (ArraySetView.EMPTY_SET_VIEW));
  }

  /** {@inheritDoc} */
  @Override
  public EModuleRelationship getRelationship(
      final IEvaluationModule other) {
    return EModuleRelationship.NONE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean canUse() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
