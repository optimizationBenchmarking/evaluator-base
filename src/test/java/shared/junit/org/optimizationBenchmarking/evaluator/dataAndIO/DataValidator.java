package shared.junit.org.optimizationBenchmarking.evaluator.dataAndIO;

import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.optimizationBenchmarking.evaluator.data.spec.IDataElement;
import org.optimizationBenchmarking.evaluator.data.spec.IDataPoint;
import org.optimizationBenchmarking.evaluator.data.spec.IDimension;
import org.optimizationBenchmarking.evaluator.data.spec.IDimensionSet;
import org.optimizationBenchmarking.evaluator.data.spec.IExperiment;
import org.optimizationBenchmarking.evaluator.data.spec.IExperimentSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeature;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSet;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureSetting;
import org.optimizationBenchmarking.evaluator.data.spec.IFeatureValue;
import org.optimizationBenchmarking.evaluator.data.spec.IInstance;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceRuns;
import org.optimizationBenchmarking.evaluator.data.spec.IInstanceSet;
import org.optimizationBenchmarking.evaluator.data.spec.INamedElement;
import org.optimizationBenchmarking.evaluator.data.spec.IParameter;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterSet;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterSetting;
import org.optimizationBenchmarking.evaluator.data.spec.IParameterValue;
import org.optimizationBenchmarking.evaluator.data.spec.IProperty;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySet;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertySetting;
import org.optimizationBenchmarking.evaluator.data.spec.IPropertyValue;
import org.optimizationBenchmarking.evaluator.data.spec.IRun;
import org.optimizationBenchmarking.utils.collections.lists.ArrayListView;
import org.optimizationBenchmarking.utils.error.ErrorUtils;
import org.optimizationBenchmarking.utils.math.MathUtils;

/** This static class provides tools to validate data. */
public final class DataValidator {

  /** the forbidden constructor */
  private DataValidator() {
    ErrorUtils.doNotCall();
  }

  /**
   * Assert that an experiment set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkExperimentSet(final IExperimentSet data) {
    Assert.assertNotNull(data);
    DataValidator.checkDimensionSet(data.getDimensions());
    DataValidator.checkInstanceSet(data.getInstances());
    DataValidator.checkExperimentList(data.getData());
    DataValidator.checkParameterSet(data.getParameters());
    DataValidator.checkFeatureSet(data.getFeatures());
  }

  /**
   * Check a list of experiments
   *
   * @param list
   *          the list of experiments
   */
  public static final void checkExperimentList(
      final ArrayListView<? extends IExperiment> list) {
    Assert.assertNotNull(list);
    for (final IExperiment exp : list) {
      DataValidator.checkExperiment(exp);
    }
  }

  /**
   * Check a property value
   *
   * @param data
   *          the property value
   */
  public static final void checkPropertyValue(final IPropertyValue data) {
    DataValidator.checkNamedElement(data);
  }

  /**
   * Check a feature value
   *
   * @param data
   *          the feature value
   */
  public static final void checkFeatureValue(final IFeatureValue data) {
    DataValidator.checkPropertyValue(data);
  }

  /**
   * Check a parameter value
   *
   * @param data
   *          the parameter value
   */
  public static final void checkParameterValue(
      final IParameterValue data) {
    DataValidator.checkPropertyValue(data);
  }

  /**
   * Check a property setting
   *
   * @param data
   *          the property setting
   */
  public static final void checkPropertySetting(
      final IPropertySetting data) {
    final Iterator<? extends IPropertyValue> iterator;
    IPropertyValue value;
    boolean need;

    Assert.assertNotNull(data);
    iterator = data.iterator();
    Assert.assertNotNull(iterator);
    while (iterator.hasNext()) {
      value = iterator.next();
      need = true;
      if (value instanceof IParameterValue) {
        DataValidator.checkParameterValue((IParameterValue) value);
        need = false;
      }
      if (value instanceof IFeatureValue) {
        DataValidator.checkFeatureValue((IFeatureValue) value);
        need = false;
      }
      if (need) {
        DataValidator.checkPropertyValue(value);
      }
    }
  }

  /**
   * Check a parameter setting
   *
   * @param data
   *          the parameter setting
   */
  public static final void checkParameterSetting(
      final IParameterSetting data) {
    DataValidator.checkPropertySetting(data);
  }

  /**
   * Check a feature setting
   *
   * @param data
   *          the feature setting
   */
  public static final void checkFeatureSetting(
      final IFeatureSetting data) {
    DataValidator.checkPropertySetting(data);
  }

  /**
   * Assert that an experiment set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkExperiment(final IExperiment data) {
    DataValidator.checkNamedElement(data);
    DataValidator.checkInstanceRunsList(data.getData());
    DataValidator.checkParameterSetting(data.getParameterSetting());
  }

  /**
   * Check a list of instance runs
   *
   * @param list
   *          the list of instance runs
   */
  public static final void checkInstanceRunsList(
      final ArrayListView<? extends IInstanceRuns> list) {
    Assert.assertNotNull(list);
    for (final IInstanceRuns runs : list) {
      DataValidator.checkInstanceRuns(runs);
    }
  }

  /**
   * Assert that a dimension is OK
   *
   * @param data
   *          the data
   */
  public static final void checkDimension(final IDimension data) {
    DataValidator.checkNamedElement(data);
    Assert.assertNotNull(data.getDataType());
    Assert.assertNotNull(data.getDimensionType());
    Assert.assertNotNull(data.getDirection());
    Assert.assertTrue(data.getIndex() >= 0);
    Assert.assertNotNull(data.getParser());
  }

  /**
   * Assert that a dimension set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkDimensionSet(final IDimensionSet data) {
    Assert.assertNotNull(data);
    DataValidator.checkDimensionList(data.getData());
  }

  /**
   * Check a list of dimensions
   *
   * @param list
   *          the list of dimensions
   */
  public static final void checkDimensionList(
      final ArrayListView<? extends IDimension> list) {
    int index;

    Assert.assertNotNull(list);
    index = 0;
    for (final IDimension dim : list) {
      DataValidator.checkDimension(dim);
      Assert.assertEquals(index++, dim.getIndex());
    }
  }

  /**
   * Assert that a property set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkPropertySet(final IPropertySet data) {
    DataValidator.checkDataElement(data);
    DataValidator.checkPropertyList(data.getData());
  }

  /**
   * Assert that a feature set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkFeatureSet(final IFeatureSet data) {
    DataValidator.checkPropertySet(data);
  }

  /**
   * Assert that a parameter set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkParameterSet(final IParameterSet data) {
    DataValidator.checkPropertySet(data);
  }

  /**
   * Assert that a property is OK
   *
   * @param data
   *          the data
   */
  public static final void checkProperty(final IProperty data) {
    DataValidator.checkNamedElement(data);
    DataValidator.checkPropertyValueList(data.getData());
    DataValidator.__checkPropertyValue(data.getGeneralized(), true);
  }

  /**
   * Assert that a feature is OK
   *
   * @param data
   *          the data
   */
  public static final void checkFeature(final IFeature data) {
    DataValidator.checkProperty(data);
  }

  /**
   * Assert that a parameter is OK
   *
   * @param data
   *          the data
   */
  public static final void checkParameter(final IParameter data) {
    final IParameterValue unspec;

    DataValidator.checkProperty(data);
    unspec = data.getUnspecified();
    if (unspec != null) {
      Assert.assertTrue(unspec.isUnspecified());
      DataValidator.checkParameterValue(unspec);
    }
  }

  /**
   * Check a list of properties
   *
   * @param list
   *          the list of properties
   */
  public static final void checkPropertyList(
      final ArrayListView<? extends IProperty> list) {
    boolean need;
    Assert.assertNotNull(list);
    for (final IProperty prop : list) {
      need = true;
      if (prop instanceof IFeature) {
        DataValidator.checkFeature((IFeature) prop);
        need = false;
      }
      if (prop instanceof IParameter) {
        DataValidator.checkParameter((IParameter) prop);
        need = false;
      }
      if (need) {
        DataValidator.checkProperty(prop);
      }
    }
  }

  /**
   * Check a list of features
   *
   * @param list
   *          the list of features
   */
  public static final void checkFeatureList(
      final ArrayListView<? extends IFeature> list) {
    DataValidator.checkPropertyList(list);
  }

  /**
   * Check a list of parameters
   *
   * @param list
   *          the list of parameters
   */
  public static final void checkParameterList(
      final ArrayListView<? extends IParameter> list) {
    DataValidator.checkPropertyList(list);
  }

  /**
   * Check a list of property values
   *
   * @param list
   *          the list of property values
   */
  public static final void checkPropertyValueList(
      final ArrayListView<? extends IPropertyValue> list) {

    Assert.assertNotNull(list);
    for (final IPropertyValue prop : list) {
      DataValidator.__checkPropertyValue(prop, false);
    }
  }

  /**
   * Check a property value
   *
   * @param prop
   *          the property value
   * @param gen
   *          is it generalized?
   */
  private static final void __checkPropertyValue(final IPropertyValue prop,
      final boolean gen) {
    boolean need;

    need = true;
    if (prop instanceof IFeatureValue) {
      DataValidator.checkFeatureValue((IFeatureValue) prop);
      need = false;
    }
    if (prop instanceof IParameterValue) {
      DataValidator.checkParameterValue((IParameterValue) prop);
      need = false;
    }
    if (need) {
      DataValidator.checkPropertyValue(prop);
    }

    Assert.assertTrue(gen == prop.isGeneralized());
  }

  /**
   * Assert that an instance is OK
   *
   * @param data
   *          the data
   */
  public static final void checkInstance(final IInstance data) {
    DataValidator.checkNamedElement(data);
    DataValidator.checkFeatureSetting(data.getFeatureSetting());
  }

  /**
   * Assert that an instance set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkInstanceSet(final IInstanceSet data) {
    Assert.assertNotNull(data);
    DataValidator.checkInstanceList(data.getData());
  }

  /**
   * Check a list of instances
   *
   * @param list
   *          the list of instances
   */
  public static final void checkInstanceList(
      final ArrayListView<? extends IInstance> list) {
    Assert.assertNotNull(list);
    for (final IInstance inst : list) {
      DataValidator.checkInstance(inst);
    }
  }

  /**
   * Assert that an instance run set is OK
   *
   * @param data
   *          the data
   */
  public static final void checkInstanceRuns(final IInstanceRuns data) {
    Assert.assertNotNull(data);
    DataValidator.checkRunList(data.getData());
  }

  /**
   * Check a list of runs
   *
   * @param list
   *          the list of runs
   */
  public static final void checkRunList(
      final ArrayListView<? extends IRun> list) {
    Assert.assertNotNull(list);
    Assert.assertTrue(list.size() > 0);
    for (final IRun run : list) {
      DataValidator.checkRun(run);
    }
  }

  /**
   * Assert that a run is OK
   *
   * @param data
   *          the data
   */
  public static final void checkRun(final IRun data) {
    int i, j;
    Assert.assertNotNull(data);
    DataValidator.checkDataPointList(data.getData());

    for (i = data.m(); (--i) >= 0;) {
      for (j = data.n(); (--j) >= 0;) {
        Assert.assertTrue(MathUtils.isFinite(data.getDouble(i, j)));
      }
    }
  }

  /**
   * Check a list of data points
   *
   * @param list
   *          the list of data points
   */
  public static final void checkDataPointList(
      final ArrayListView<? extends IDataPoint> list) {
    Assert.assertNotNull(list);
    Assert.assertTrue(list.size() > 0);
    for (final IDataPoint point : list) {
      DataValidator.checkDataPoint(point);
    }
  }

  /**
   * Assert that a data point is OK
   *
   * @param data
   *          the data
   */
  public static final void checkDataPoint(final IDataPoint data) {
    Assert.assertNotNull(data);
    Assert.assertTrue(data.size() > 0);
    Assert.assertTrue(data.m() == 1);
    Assert.assertTrue(data.n() > 0);
  }

  /**
   * Assert that a data element is OK
   *
   * @param element
   *          the element
   */
  public static final void checkDataElement(final IDataElement element) {
    Assert.assertNotNull(element);
    if (!(element instanceof IExperimentSet)) {
      Assert.assertNotNull(element.getOwner());
    }
  }

  /**
   * Assert that a named element is OK
   *
   * @param element
   *          the element
   */
  public static final void checkNamedElement(final INamedElement element) {
    String string;

    DataValidator.checkDataElement(element);

    string = element.getName();
    Assert.assertNotNull(string);
    Assert.assertTrue(string.length() > 0);

    string = element.getDescription();
    if (string != null) {
      Assert.assertTrue(string.length() > 0);
    }

    string = element.getPathComponentSuggestion();
    Assert.assertNotNull(string);
    Assert.assertTrue(string.length() > 0);
  }

  /**
   * assert that two experiment sets are equal
   *
   * @param a
   *          set a
   * @param b
   *          set b
   */
  public static final void assertEquals(final IExperiment a,
      final IExperiment b) {
    Iterator<Map.Entry<IProperty, Object>> x, y;
    Map.Entry<IProperty, Object> xe, ye;
    boolean z;
    ArrayListView<? extends IInstanceRuns> ia, ib;
    IInstanceRuns iae, ibe;
    int si, sr, sp;
    ArrayListView<? extends IRun> ra, rb;
    IRun rae, rbe;
    ArrayListView<? extends IDataPoint> da, db;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getParameterSetting().entrySet().iterator();
    y = b.getParameterSetting().entrySet().iterator();

    outer: for (;;) {
      z = x.hasNext();
      Assert.assertTrue(z == y.hasNext());
      if (!z) {
        break outer;
      }
      xe = x.next();
      ye = y.next();
      DataValidator.assertPropertyValueEquals(xe.getValue(),
          ye.getValue());
      DataValidator.assertEquals(((IParameter) (xe.getKey())),
          ((IParameter) (ye.getKey())));
    }

    ia = a.getData();
    ib = b.getData();
    si = ia.size();
    Assert.assertEquals(si, ib.size());
    for (; (--si) >= 0;) {
      iae = ia.get(si);
      ibe = ib.get(si);
      DataValidator.assertEquals(iae.getInstance(), ibe.getInstance());
      ra = iae.getData();
      rb = ibe.getData();
      sr = ra.size();
      Assert.assertEquals(sr, rb.size());
      for (; (--sr) >= 0;) {
        rae = ra.get(sr);
        rbe = rb.get(sr);

        da = rae.getData();
        db = rbe.getData();
        sp = da.size();
        Assert.assertEquals(sp, db.size());
        for (; (--sp) >= 0;) {
          Assert.assertEquals(da.get(sp), db.get(sp));
        }
      }
    }

  }

  /**
   * assert that two experiment sets are equal
   *
   * @param a
   *          set a
   * @param b
   *          set b
   */
  public static final void assertEquals(final IExperimentSet a,
      final IExperimentSet b) {
    int s;
    ArrayListView<? extends IExperiment> ae, be;
    ArrayListView<? extends IFeature> af, bf;
    ArrayListView<? extends IParameter> ap, bp;
    ArrayListView<? extends IDimension> ad, bd;
    IDimension d1, d2;

    ae = a.getData();
    be = b.getData();

    Assert.assertEquals(s = ae.size(), be.size());
    for (; (--s) >= 0;) {
      DataValidator.assertEquals(ae.get(s), be.get(s));
    }

    af = a.getFeatures().getData();
    bf = b.getFeatures().getData();
    Assert.assertEquals(s = af.size(), bf.size());
    for (; (--s) >= 0;) {
      DataValidator.assertEquals(af.get(s), bf.get(s));
    }

    ap = a.getParameters().getData();
    bp = b.getParameters().getData();
    Assert.assertEquals(s = ap.size(), bp.size());
    for (; (--s) >= 0;) {
      DataValidator.assertEquals(ap.get(s), bp.get(s));
    }

    ad = a.getDimensions().getData();
    bd = b.getDimensions().getData();
    Assert.assertEquals(s = ad.size(), bd.size());
    for (; (--s) >= 0;) {
      d1 = ad.get(s);
      d2 = bd.get(s);
      Assert.assertEquals(d1.getName(), d2.getName());
      Assert.assertEquals(d1.getDescription(), d2.getDescription());
      Assert.assertSame(d1.getDataType(), d2.getDataType());
      Assert.assertSame(d1.getDimensionType(), d2.getDimensionType());
      Assert.assertSame(d1.getDirection(), d2.getDirection());
      Assert.assertSame(d1.getParser().getOutputClass(),
          d2.getParser().getOutputClass());
    }
  }

  /**
   * assert that two experiment sets are equal
   *
   * @param a
   *          set a
   * @param b
   *          set b
   */
  public static final void assertEquals(final IFeature a,
      final IFeature b) {
    ArrayListView<? extends IFeatureValue> x, y;
    IFeatureValue xe, ye;
    int s;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getData();
    y = b.getData();
    s = x.size();
    Assert.assertEquals(s, y.size());
    for (; (--s) >= 0;) {
      xe = x.get(s);
      ye = y.get(s);
      Assert.assertEquals(xe.getName(), ye.getName());
      Assert.assertEquals(xe.getDescription(), ye.getDescription());
      DataValidator.assertPropertyValueEquals(xe.getValue(),
          ye.getValue());
    }
  }

  /**
   * assert that two experiment sets are equal
   *
   * @param a
   *          set a
   * @param b
   *          set b
   */
  public static final void assertEquals(final IInstance a,
      final IInstance b) {
    Iterator<Map.Entry<IProperty, Object>> x, y;
    Map.Entry<IProperty, Object> xe, ye;
    boolean z;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getFeatureSetting().entrySet().iterator();
    y = b.getFeatureSetting().entrySet().iterator();

    outer: for (;;) {
      z = x.hasNext();
      Assert.assertTrue(z == y.hasNext());
      if (!z) {
        break outer;
      }
      xe = x.next();
      ye = y.next();
      DataValidator.assertPropertyValueEquals(xe.getValue(),
          ye.getValue());
      DataValidator.assertEquals(((IFeature) (xe.getKey())),
          ((IFeature) (ye.getKey())));
    }
  }

  /**
   * assert that two experiment sets are equal
   *
   * @param a
   *          set a
   * @param b
   *          set b
   */
  public static final void assertEquals(final IParameter a,
      final IParameter b) {
    ArrayListView<? extends IParameterValue> x, y;
    IParameterValue xe, ye;
    int s;

    Assert.assertEquals(a.getName(), b.getName());
    Assert.assertEquals(a.getDescription(), b.getDescription());

    x = a.getData();
    y = b.getData();
    s = x.size();
    Assert.assertEquals(s, y.size());
    for (; (--s) >= 0;) {
      xe = x.get(s);
      ye = y.get(s);
      Assert.assertEquals(xe.getName(), ye.getName());
      Assert.assertEquals(xe.getDescription(), ye.getDescription());
      DataValidator.assertPropertyValueEquals(xe.getValue(),
          ye.getValue());
    }
  }

  /**
   * assert that parameter values are equal
   *
   * @param a
   *          the parameter value
   * @param b
   *          the parameter value
   */
  public static final void assertPropertyValueEquals(final Object a,
      final Object b) {
    boolean id1, id2;
    long l1, l2;
    double d1, d2;
    String s1, s2;

    if ((a instanceof Number) && (b instanceof Number)) {

      if ((a instanceof Float) || (a instanceof Double)) {
        d1 = ((Number) a).doubleValue();
        l1 = 0L;
        id1 = true;
      } else {
        d1 = Double.NaN;
        l1 = ((Number) a).longValue();
        id1 = false;
      }

      if ((b instanceof Float) || (b instanceof Double)) {
        d2 = ((Number) b).doubleValue();
        l2 = 0L;
        id2 = true;
      } else {
        d2 = Double.NaN;
        l2 = ((Number) b).longValue();
        id2 = false;
      }

      if (id1 == id2) {
        if (id1) {
          Assert.assertEquals(d1, d2, 1e-14);
        } else {
          Assert.assertEquals(l1, l2);
        }
        return;
      }

    } else {
      checkString: {
        if (a instanceof String) {
          s1 = ((String) a);
        } else {
          if (a instanceof Character) {
            s1 = a.toString();
          } else {
            break checkString;
          }
        }

        if (b instanceof String) {
          s2 = ((String) b);
        } else {
          if (b instanceof Character) {
            s2 = b.toString();
          } else {
            break checkString;
          }
        }

        Assert.assertEquals(s1, s2);
        return;
      }
    }

    Assert.assertEquals(a, b);
  }

}
