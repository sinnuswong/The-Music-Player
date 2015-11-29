package com.Project100Pi.themusicplayer;

/**
 * Created by BalachandranAR on 11/27/2015.
 */
import android.content.Context;
import android.util.AttributeSet;
import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;


public class MySectionTitleIndicator extends SectionTitleIndicator<String> {

    public MySectionTitleIndicator(Context context) {
        super(context);
    }

    public MySectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(String s) {
        // Example of using a single character
        setTitleText(s);

        // Example of using a longer string
        // setTitleText(colorGroup.getName());

        //setIndicatorTextColor(ColorUtils.accentColor);
    }


}