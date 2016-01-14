package org.optimizationBenchmarking.evaluator.data.impl.shadow;

import java.util.Collection;

import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;

/**
 * A shadow instance set is basically a shadow of another instance set with
 * a different owner and potentially different attributes. If all
 * associated data of this element is the same, it will delegate
 * attribute-based computations to that instance set.
 */
public class ShadowInstanceSet extends //
    _ShadowNamedElementSet<IExperimentSet, IInstanceSet, IInstance>
    implements IInstanceSet {

  /**
   * create the shadow instance
   *
   * @param owner
   *          the owning instance set
   * @param shadow
   *          the instance to shadow
   * @param selection
   *          the selection of instances
   */
  public ShadowInstanceSet(final IExperimentSet owner,
      final IInstanceSet shadow,
      final Collection<? extends IInstance> selection) {
    super(owner, shadow, selection);
  }

  /** {@inheritDoc} */
  @Override
  final IInstance _shadow(final IInstance element) {
    return new ShadowInstance(this, element);
  }

  /** {@inheritDoc} */
  @SuppressWarnings("rawtypes")
  @Override
  final boolean _canDelegateAttributesTo(final IInstanceSet shadow) {
    final IExperimentSet ours, others;

    if (this == shadow) {
      return true;
    }

    if (!(_ShadowNamedElementSet._compare(this.getData(),
        shadow.getData()))) {
      return false;
    }

    ours = this.getOwner();
    others = shadow.getOwner();
    if (ours == others) {
      return true;
    }

    if (ours instanceof ShadowExperimentSet) {
      if (((ShadowExperimentSet) ours)._canDelegateAttributesTo(others)) {
        return true;
      }
    }

    return false;
  }
}
