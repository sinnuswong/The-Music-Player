package com.Project100Pi.themusicplayer;

import android.graphics.Color;
import android.widget.Switch;

import java.util.ArrayList;

/**
 * Created by BalachandranAR on 11/21/2015.
 */
public class ColorUtils {
    public static final int DARK_THEME = 0;
    public static final int LIGHT_THEME = 1;

    public static int CURRENT_THEME = LIGHT_THEME;
    public static ColorTheme darkTheme,lightTheme,currentTheme;
    static int primaryBgColor,secondaryBgColor,primaryTextColor,secondaryTextColor,accentColor;

    public ColorUtils() {
        darkTheme = new ColorTheme(Color.parseColor("#3D3D3D"), Color.parseColor("#484848"), Color.parseColor("#C1C1C1"), Color.parseColor("#A1A1A1"), Color.parseColor("#BE4D56"));
        lightTheme = new ColorTheme(Color.parseColor("#D4D4D4"), Color.parseColor("#E0E0E0"), Color.parseColor("#212121"), Color.parseColor("#626262"), Color.parseColor("#BE4D56"));
        setCurrentTheme(CURRENT_THEME);
    }
    public void setCurrentTheme(int theme){
        CURRENT_THEME = theme;
        switch(CURRENT_THEME){
            case DARK_THEME :
                currentTheme = darkTheme;
                break;
            case LIGHT_THEME:
                currentTheme = lightTheme;
                break;
            default:
                break;
        }
        primaryBgColor = currentTheme.primaryBgColor;
        secondaryBgColor = currentTheme.secondaryBgColor;
        primaryTextColor = currentTheme.primaryTextColor;
        secondaryTextColor = currentTheme.secondaryTextColor;
        accentColor = currentTheme.accentColor;
    }
    public int getPrimaryBgColor(){
        return currentTheme.getPrimaryBgColor();
    }

    public int getSecondaryBgColor(){
        return currentTheme.getSecondaryBgColor();
    }

    public int getPrimaryTextColor(){
        return currentTheme.getPrimaryTextColor();
    }
    public int getSecondaryTextColor(){
        return currentTheme.getSecondaryTextColor();
    }
    public int getAccentColor(){
        return currentTheme.getAccentColor();
    }
}

class ColorTheme{

    int primaryBgColor,secondaryBgColor,primaryTextColor,secondaryTextColor,accentColor;

    public ColorTheme(int primaryBgColor,int secondaryBgColor,int primaryTextColor,int secondaryTextColor,int accentColor){
        this.primaryBgColor = primaryBgColor;
        this.secondaryBgColor = secondaryBgColor;
        this.primaryTextColor = primaryTextColor;
        this.secondaryTextColor = secondaryTextColor;
        this.accentColor = accentColor;

    }

    public int getAccentColor() {
        return accentColor;
    }

    public int getPrimaryBgColor() {
        return primaryBgColor;
    }

    public int getPrimaryTextColor() {
        return primaryTextColor;
    }

    public int getSecondaryBgColor() {
        return secondaryBgColor;
    }

    public int getSecondaryTextColor() {
        return secondaryTextColor;
    }
}