package com.nummist.secondsight.filters.convolution;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import com.nummist.secondsight.filters.Filter;

public final class StrokeEdgesFilter implements Filter {
    
    private final Mat mKernel = new MatOfInt(
            0, 0,   1, 0, 0,
            0, 1,   2, 1, 0,
            1, 2, -16, 2, 1,
            0, 1,   2, 1, 0,
            0, 0,   1, 0, 0
    );
    private final Mat mEdges = new Mat();
    
    @Override
    public void apply(final Mat src, final Mat dst) {
        Imgproc.filter2D(src, mEdges, -1, mKernel);
        Core.bitwise_not(mEdges, mEdges);
        Core.multiply(src, mEdges, dst, 1.0/255.0);
    }
}
