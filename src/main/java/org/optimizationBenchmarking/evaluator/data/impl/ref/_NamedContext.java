package org.optimizationBenchmarking.evaluator.data.impl.ref;

import org.optimizationBenchmarking.evaluator.data.impl.abstr.AbstractNamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.builders.INamedContext;
import org.optimizationBenchmarking.utils.hierarchy.FSM;
import org.optimizationBenchmarking.utils.text.textOutput.MemoryTextOutput;

/**
 * A builder for named objects.
 *
 * @param <RT>
 *          the return type
 */
class _NamedContext<RT> extends _Context<RT> implements INamedContext {

  /** we have a name */
  static final int FLAG_HAS_NAME = (FSM.FLAG_NOTHING + 1);

  /** the name of this context has been set */
  static final String FLAG_HAS_NAME_NAME = "nameSet"; //$NON-NLS-1$

  /** the name */
  private volatile String m_name;

  /** the description */
  private volatile String m_description;

  /**
   * create
   *
   * @param element
   *          the owning element
   */
  _NamedContext(final _FSM element) {
    super(element);
  }

  /** {@inheritDoc} */
  @Override
  protected void fsmFlagsAppendName(final int flagValue,
      final int flagIndex, final MemoryTextOutput append) {
    if (flagValue == _NamedContext.FLAG_HAS_NAME) {
      append.append(_NamedContext.FLAG_HAS_NAME_NAME);
      return;
    }
    super.fsmFlagsAppendName(flagValue, flagIndex, append);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setName(final String name) {
    final String n;

    this.fsmStateAssert(_FSM.STATE_OPEN);
    n = this.normalizeLocal(name);
    if (n == null) {
      throw new IllegalArgumentException(((//
      "Specified name must not be empty or null, but is '" + name) + '\'') //$NON-NLS-1$
          + '.');
    }
    if (!(n.equals(this.m_name))) {
      this.fsmFlagsAssertAndUpdate(FSM.FLAG_NOTHING,
          _NamedContext.FLAG_HAS_NAME, _NamedContext.FLAG_HAS_NAME,
          FSM.FLAG_NOTHING);
      this.m_name = n;
    }
  }

  /**
   * Get the name of this object
   *
   * @return the name of this object
   */
  public synchronized final String getName() {
    this.fsmFlagsAssertTrue(_NamedContext.FLAG_HAS_NAME);
    return this.m_name;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void setDescription(final String description) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_description = this.normalizeLocal(description);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized final void addDescription(final String description) {
    this.fsmStateAssert(_FSM.STATE_OPEN);
    this.m_description = this.normalizeLocal(
        AbstractNamedElement.mergeDescriptions(this.m_description,
            this.normalizeLocal(description)));
  }

  /**
   * Get the description of this object
   *
   * @return the description of this object
   */
  public synchronized final String getDescription() {
    return this.m_description;
  }

  /**
   * compile and return the compilation result
   *
   * @param name
   *          the name
   * @param description
   *          the description
   * @return the compilation result
   */
  RT _doCompile(final String name, final String description) {
    throw new UnsupportedOperationException();
  }

  /** {@inheritDoc} */
  @Override
  final RT _doCompile() {
    final String a, b, c, d;

    a = this.m_name;
    this.m_name = null;
    c = this.m_description;
    this.m_description = null;

    this.fsmFlagsAssertTrue(_NamedContext.FLAG_HAS_NAME);

    b = this.normalize(a);
    if (b == null) {
      throw new IllegalArgumentException(//
          "A name must not normalize to null, buth the name '" //$NON-NLS-1$
              + a + "' of " + this + //$NON-NLS-1$
              " does."); //$NON-NLS-1$
    }

    if (c != null) {
      d = this.normalize(c);

      if (d == null) {
        throw new IllegalArgumentException(//
            "A non-empty descriptionmust not normalize to null, buth the description '" //$NON-NLS-1$
                + c + "' of " + this + //$NON-NLS-1$
                " does."); //$NON-NLS-1$
      }
    } else {
      d = null;
    }

    return this._doCompile(b, d);
  }

}
