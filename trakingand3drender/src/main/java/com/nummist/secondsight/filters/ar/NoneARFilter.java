package com.nummist.secondsight.filters.ar;

import com.nummist.secondsight.filters.NoneFilter;

public class NoneARFilter extends NoneFilter implements ARFilter {
    @Override
    public float[] getGLPose() {
        return null;
    }
}
