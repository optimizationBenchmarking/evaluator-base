package org.optimizationBenchmarking.evaluator.evaluation.definition.data;

import org.optimizationBenchmarking.evaluator.evaluation.spec.IEvaluationModule;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * An entry for an evaluation module.
 */
public final class EvaluationModuleEntry extends _ConfigEntry {

  /** The module */
  private final IEvaluationModule m_module;

  /**
   * Create the module entry
   *
   * @param config
   *          the configuration
   * @param module
   *          the module
   */
  EvaluationModuleEntry(final Configuration config,
      final IEvaluationModule module) {
    super(config);
    EvaluationModuleEntry._validateModule(module);
    this.m_module = module;
  }

  /**
   * Validate the module .
   *
   * @param module
   *          the entry class
   */
  static final void _validateModule(final IEvaluationModule module) {
    if (module == null) {
      throw new IllegalArgumentException("Module cannot be null."); //$NON-NLS-1$
    }
    module.checkCanUse();
  }

  /**
   * Get the module.
   *
   * @return the module.
   */
  public final IEvaluationModule getModule() {
    return this.m_module;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(HashUtils.hashCode(this.m_module),
        HashUtils.hashCode(this.m_config));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final EvaluationModuleEntry me;
    if (o == this) {
      return true;
    }
    if (o instanceof EvaluationModuleEntry) {
      me = ((EvaluationModuleEntry) o);
      return (Compare.equals(me.m_module, this.m_module) && //
          Compare.equals(me.m_config, this.m_config));
    }

    return false;
  }
}
