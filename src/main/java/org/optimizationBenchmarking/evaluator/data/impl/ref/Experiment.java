package org.optimizationBenchmarking.evaluator.data.impl.ref;

import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.utils.document.spec.IMath;
import org.optimizationBenchmarking.utils.document.spec.IParameterRenderer;
import org.optimizationBenchmarking.utils.text.ETextCase;
import org.optimizationBenchmarking.utils.text.TextUtils;
import org.optimizationBenchmarking.utils.text.textOutput.ITextOutput;

/** The experiment. */
public final class Experiment extends _IDObjectSet<InstanceRuns>
    implements IExperiment {
  /** the serial version uid */
  private static final long serialVersionUID = 1L;

  /** the parameter value map */
  final ParameterSetting m_parameters;

  /** this object's name */
  final String m_name;
  /** the description of this property */
  final String m_description;

  /**
   * instantiate
   *
   * @param name
   *          the name
   * @param desc
   *          the description
   * @param params
   *          the parameter values
   * @param data
   *          the data of the set
   * @param clone
   *          should we clone the data?
   * @param sort
   *          should we sort the data?
   * @param own
   *          should mark the elements as owned by this object?
   */
  Experiment(final String name, final String desc,
      final ParameterSetting params, final InstanceRuns[] data,
      final boolean clone, final boolean sort, final boolean own) {
    super(data, clone, sort, own);
    String n;

    if (params == null) {
      throw new IllegalArgumentException(//
          "Parameter setting of experiment must not be null."); //$NON-NLS-1$
    }
    this.m_parameters = params;

    n = TextUtils.prepare(name);
    if (n == null) {
      n = TextUtils.prepare(params.toName());
    }

    if (n == null) {
      throw new IllegalArgumentException(//
          "Experiments must have a non-empty name, but name '" + //$NON-NLS-1$
              name + "' was provided."); //$NON-NLS-1$

    }
    this.m_name = n;
    this.m_description = TextUtils.prepare(desc);

  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return this.m_name;
  }

  /** {@inheritDoc} */
  @Override
  public final String getDescription() {
    return this.m_description;
  }

  /** {@inheritDoc} */
  @Override
  final void _validateElementPair(final InstanceRuns before,
      final InstanceRuns after) {
    super._validateElementPair(before, after);
    if (before.m_inst.equals(after.m_inst)) {
      throw new IllegalArgumentException(
          (((((("An experiment cannot contain more than one run set for a given instance, but run set '" //$NON-NLS-1$
              + before) + "' and '") + //$NON-NLS-1$
              after) + "' are both for instance '") + //$NON-NLS-1$
              before.m_inst) + '\'') + '.');
    }
  }

  /** {@inheritDoc} */
  @Override
  public final ParameterSetting getParameterSetting() {
    return this.m_parameters;
  }

  /** {@inheritDoc} */
  @Override
  final int _compareTo(final _IDObject o) {

    if (o instanceof Experiment) {
      return this.m_parameters.compareTo(((Experiment) o).m_parameters);
    }

    return super._compareTo(o);
  }

  /** {@inheritDoc} */
  @Override
  public final ExperimentSet getOwner() {
    return ((ExperimentSet) (this.m_owner));
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final ITextOutput out,
      final IParameterRenderer renderer) {
    super.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final void mathRender(final IMath out,
      final IParameterRenderer renderer) {
    super.mathRender(out, renderer);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printShortName(final ITextOutput textOut,
      final ETextCase textCase) {
    return super.printShortName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printLongName(final ITextOutput textOut,
      final ETextCase textCase) {
    return super.printLongName(textOut, textCase);
  }

  /** {@inheritDoc} */
  @Override
  public final ETextCase printDescription(final ITextOutput textOut,
      final ETextCase textCase) {
    return super.printDescription(textOut, textCase);
  }

  /**
   * Obtain a suggestion for the path component of figures drawn based on
   * this object.
   *
   * @return a suggestion for the path component of figures drawn based on
   *         this component.
   */
  @Override
  public final String getPathComponentSuggestion() {
    return super.getPathComponentSuggestion();
  }
}
