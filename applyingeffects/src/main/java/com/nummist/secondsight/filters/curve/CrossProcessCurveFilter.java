package com.nummist.secondsight.filters.curve;


public class CrossProcessCurveFilter extends CurveFilter {
    
    public CrossProcessCurveFilter() {
        super(
                new double[] { 0, 255 }, // vValIn
                new double[] { 0, 255 }, // vValOut
                
                new double[] { 0, 56, 211, 255 }, // rValIn
                new double[] { 0, 22, 255, 255 }, // rValOut
                
                new double[] { 0, 56, 208, 255 }, // gValIn
                new double[] { 0, 39, 226, 255 }, // gValOut
                
                new double[] {  0, 255 },  // bValIn
                new double[] { 20, 235 }); // bValOut
    }
}
