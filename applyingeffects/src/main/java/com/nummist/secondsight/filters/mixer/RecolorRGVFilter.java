package com.nummist.secondsight.filters.mixer;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.nummist.secondsight.filters.Filter;

public class RecolorRGVFilter implements Filter {
    
    private ArrayList<Mat> mChannels = new ArrayList<Mat>(4);
    
    @Override
    public void apply(Mat src, Mat dst) {
        Core.split(src, mChannels);
        
        Mat r = mChannels.get(0);
        Mat g = mChannels.get(1);
        Mat b = mChannels.get(2);
        
        // dst.b = min(dst.r, dst.g, dst.b)
        Core.min(b, r, b);
        Core.min(b, g, b);
        
        Core.merge(mChannels, dst);
    }
}
