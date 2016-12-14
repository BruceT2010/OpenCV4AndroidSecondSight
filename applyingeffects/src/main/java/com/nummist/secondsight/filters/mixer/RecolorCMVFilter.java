package com.nummist.secondsight.filters.mixer;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.nummist.secondsight.filters.Filter;

public class RecolorCMVFilter implements Filter {
    
    private ArrayList<Mat> mChannels = new ArrayList<Mat>(4);
    
    @Override
    public void apply(Mat src, Mat dst) {
        Core.split(src, mChannels);
        
        Mat r = mChannels.get(0);
        Mat g = mChannels.get(1);
        Mat b = mChannels.get(2);
        
        // dst.b = max(dst.r, dst.g, dst.b)
        Core.max(b, r, b);
        Core.max(b, g, b);
        
        Core.merge(mChannels, dst);
    }
}
