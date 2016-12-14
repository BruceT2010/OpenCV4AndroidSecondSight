package com.nummist.secondsight.filters.curve;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

import com.nummist.secondsight.filters.Filter;

public class CurveFilter implements Filter {
    
    // The lookup table.
    private Mat mLUT = new MatOfInt();
    
    public CurveFilter(
            double[] vValIn, double[] vValOut,
            double[] rValIn, double[] rValOut,
            double[] gValIn, double[] gValOut,
            double[] bValIn, double[] bValOut) {
        
        // Create the interpolation functions.
        UnivariateFunction vFunc = newFunc(vValIn, vValOut);
        UnivariateFunction rFunc = newFunc(rValIn, rValOut);
        UnivariateFunction gFunc = newFunc(gValIn, gValOut);
        UnivariateFunction bFunc = newFunc(bValIn, bValOut);
        
        // Create and populate the lookup table.
        mLUT.create(256, 1, CvType.CV_8UC4);
        for (int i = 0; i < 256; i++) {
            double v = vFunc.value(i);
            double r = rFunc.value(v);
            double g = gFunc.value(v);
            double b = bFunc.value(v);
            mLUT.put(i, 0, r, g, b, i); // alpha is unchanged
        }
    }
    
    @Override
    public void apply(Mat src, Mat dst) {
        // Apply the lookup table.
        Core.LUT(src, mLUT, dst);
    }
    
    private UnivariateFunction newFunc(double[] valIn, double[] valOut) {
        UnivariateInterpolator interpolator;
        if (valIn.length > 2) {
            interpolator = new SplineInterpolator();
        } else {
            interpolator = new LinearInterpolator();
        }
        return interpolator.interpolate(valIn, valOut);
    }
}
