package org.optimizationBenchmarking.evaluator.data.impl.abstr;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterSetting;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.collections.lists.ArraySetView;
import org.optimizationBenchmarking.utils.document.impl.SemanticComponentUtils;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/**
 * An abstract implementation of the
 * {@link org.optimizationBenchmarking.evaluator.data.spec.IExperiment}
 * interface.
 */
public abstract class AbstractExperiment extends AbstractElementSet
    implements IExperiment {

  /** the owner */
  IExperimentSet m_owner;

  /**
   * Create the abstract experiment. If {@code owner==null}, you must later
   * set it via
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractExperimentSet#own(AbstractExperiment)}
   * .
   *
   * @param owner
   *          the owner
   */
  protected AbstractExperiment(final IExperimentSet owner) {
    super();
    this.m_owner = owner;
  }

  /**
   * Own an
   * {@link org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractInstanceRuns}
   * .
   *
   * @param runs
   *          the runs to own
   */
  protected final void own(final AbstractInstanceRuns runs) {
    if (runs == null) {
      throw new IllegalArgumentException(//
          "AbstractInstanceRuns to be owned by AbstractExperiment cannot be null."); //$NON-NLS-1$
    }
    synchronized (runs) {
      if (runs.m_owner != null) {
        throw new IllegalArgumentException(//
            "AbstractInstanceRuns to be owned by AbstractExperiment already owned.");//$NON-NLS-1$
      }
      runs.m_owner = this;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final IExperimentSet getOwner() {
    return this.m_owner;
  }

  /** {@inheritDoc} */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public ArrayListView<? extends IInstanceRuns> getData() {
    return ((ArraySetView) (ArraySetView.EMPTY_SET_VIEW));
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

  /** {@inheritDoc} */
  @Override
  public String getDescription() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IParameterSetting getParameterSetting() {
    return new AbstractParameterSetting();
  }

  /** {@inheritDoc} */
  @Override
  public String getPathComponentSuggestion() {
    return this.getName();
  }

  /** {@inheritDoc} */
  @Override
  public void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    SemanticComponentUtils.mathRender(this.getName(), out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    SemanticComponentUtils.mathRender(this.getName(), out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return SemanticComponentUtils.printShortName(this.getName(), textOut,
        textCase, true);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return this.printShortName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return SemanticComponentUtils.printDescription(this.getDescription(),
        textOut, textCase);
  }
}
