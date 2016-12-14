package com.nummist.secondsight.filters.mixer;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import com.nummist.secondsight.filters.Filter;

public class RecolorRCFilter implements Filter {
    
    private ArrayList<Mat> mChannels = new ArrayList<Mat>(4);
    
    @Override
    public void apply(Mat src, Mat dst) {
        Core.split(src, mChannels);
        
        Mat g = mChannels.get(1);
        Mat b = mChannels.get(2);
        
        // dst.g = 0.5 * src.g + 0.5 * src.b
        Core.addWeighted(g, 0.5, b,  0.5, 0.0, g);
        
        // dst.b = dst.g
        mChannels.set(2, g);
        
        Core.merge(mChannels, dst);
    }
}
