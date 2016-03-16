package org.optimizationBenchmarking.evaluator.evaluation.definition.data;

import java.util.Collection;

import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.comparison.Compare;
import org.optimizationBenchmarking.utils.config.Configuration;
import org.optimizationBenchmarking.utils.config.ConfigurationBuilder;
import org.optimizationBenchmarking.utils.hash.HashUtils;

/**
 * An evaluation setup.
 */
public final class EvaluationModules extends _ConfigEntry {

  /** the modules */
  private final ArrayListView<EvaluationModuleEntry> m_modules;

  /**
   * Create the evaluation setup
   *
   * @param config
   *          the configuration
   * @param modules
   *          the modules
   */
  EvaluationModules(final Configuration config,
      final ArrayListView<EvaluationModuleEntry> modules) {
    super(config);
    EvaluationModules._validateModules(modules);

    this.m_modules = modules;
  }

  /**
   * Verify whether the module list is OK
   *
   * @param modules
   *          the module list
   */
  static final void _validateModules(
      final Collection<EvaluationModuleEntry> modules) {
    if (modules == null) {
      throw new IllegalArgumentException("Module list cannot be null."); //$NON-NLS-1$
    }
  }

  /**
   * Obtain an empty evaluation modules list
   *
   * @return the empty list
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static final EvaluationModules empty() {
    try (final ConfigurationBuilder cb = new ConfigurationBuilder()) {
      return new EvaluationModules(cb.getResult(),
          ((ArrayListView) (ArraySetView.EMPTY_SET_VIEW)));
    }
  }

  /**
   * Get the module entries.
   *
   * @return the module entries.
   */
  public final ArrayListView<EvaluationModuleEntry> getEntries() {
    return this.m_modules;
  }

  /** {@inheritDoc} */
  @Override
  protected final int calcHashCode() {
    return HashUtils.combineHashes(HashUtils.hashCode(this.m_modules),
        HashUtils.hashCode(this.m_config));
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object o) {
    final EvaluationModules me;
    if (o == this) {
      return true;
    }
    if (o instanceof EvaluationModules) {
      me = ((EvaluationModules) o);
      return (Compare.equals(me.m_config, this.m_modules) && //
          Compare.equals(me.m_config, this.m_config));
    }

    return false;
  }
}
