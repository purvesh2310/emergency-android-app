package com.pk.eager.ReportFragments;

import android.widget.RadioGroup;

/**
 * Created by kimpham on 7/25/17.
 */

public class ViewUtils {

    static public void setRadioGroupClickable(RadioGroup group, boolean clickable){
        for(int i = 0; i < group.getChildCount(); i++){
            group.getChildAt(i).setEnabled(clickable);
        }
    }



}
