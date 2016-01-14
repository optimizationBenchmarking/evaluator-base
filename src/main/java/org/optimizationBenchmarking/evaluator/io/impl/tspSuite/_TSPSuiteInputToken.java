package org.optimizationBenchmarking.evaluator.io.impl.tspSuite;

import java.nio.file.Path;

import org.optimizationBenchmarking.evaluator.data.spec.builders.IExperimentContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IExperimentSetContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IInstanceRunsContext;
import org.optimizationBenchmarking.evaluator.data.spec.builders.IRunContext;
import org.optimizationBenchmarking.utils.text.TextUtils;

/** the internal content handler */
final class _TSPSuiteInputToken {

  /** the hierarchical fsm stack */
  final IExperimentSetContext m_esc;

  /** the experiment context */
  IExperimentContext m_ec;

  /** the hierarchical fsm stack */
  IInstanceRunsContext m_irsc;

  /** the experiment root */
  Path m_experimentRoot;

  /** the experiment root */
  Path m_instanceRunsRoot;

  /**
   * create
   *
   * @param esb
   *          the experiment set builder to use
   */
  _TSPSuiteInputToken(final IExperimentSetContext esb) {
    super();
    this.m_esc = esb;
  }

  /** pop the instance runs context */
  final void _popIRSC() {
    this.m_instanceRunsRoot = null;
    if (this.m_irsc != null) {
      try {
        this.m_irsc.close();
      } finally {
        this.m_irsc = null;
      }
    }
  }

  /** pop the experiment context */
  final void _popEC() {
    this.m_experimentRoot = null;
    try {
      this._popIRSC();
    } finally {
      if (this.m_ec != null) {
        try {
          this.m_ec.close();
        } finally {
          this.m_ec = null;
        }
      }
    }
  }

  /**
   * begin a run
   *
   * @param f
   *          the file
   * @return the run context to use
   */
  final IRunContext _beginRun(final Path f) {
    Path experiment, instance;
    String en, in, n;

    experiment = instance = null;
    en = in = null;
    for (Path p = f.getParent(); p != null; p = p.getParent()) {
      n = TextUtils.normalize(p.getFileName().toString());
      if (n == null) {
        continue;
      }
      if (in == null) {
        in = TSPSuiteInput._instanceName(n);
        if (in != null) {
          instance = p;
        }
        continue;
      }

      if ("results".equalsIgnoreCase(n) || //$NON-NLS-1$
          "symmetric".equalsIgnoreCase(n) || //$NON-NLS-1$
          "asymmetric".equalsIgnoreCase(n)) { //$NON-NLS-1$
        continue;
      }

      en = n;
      experiment = p;
      break;
    }

    if ((experiment == null) || (instance == null)) {
      // TODO: get instance from file, then try again
      throw new IllegalStateException();
    }

    if (!(instance.equals(this.m_instanceRunsRoot))) {
      this._popIRSC();
      if (!(experiment.equals(this.m_experimentRoot))) {
        this._popEC();
        this.m_ec = this.m_esc.createExperiment();
        this.m_ec.setName(en);
        this.m_experimentRoot = experiment;
      }
      this.m_irsc = this.m_ec.createInstanceRuns();
      this.m_irsc.setInstance(in);
      this.m_instanceRunsRoot = instance;
    }

    return this.m_irsc.createRun();
  }

}
