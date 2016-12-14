package com.nummist.secondsight.filters;

import org.opencv.core.Mat;

public interface Filter {
    public abstract void apply(Mat src, Mat dst);
}
