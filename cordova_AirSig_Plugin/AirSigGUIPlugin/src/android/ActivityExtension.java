package com.airsig.webclientgui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Created by ssoko_000 on 13.4.2015..
 */
public class ActivityExtension extends Activity{
    @Nullable
    public Drawable getDrawable(String name) throws Resources.NotFoundException {
        return this.getResources().getDrawable(getIdentifier(name, "drawable"));
    }

    @Nullable
    public int getIdentifier(String name, String defType) throws Resources.NotFoundException {
        return getResources().getIdentifier(name, defType, getPackageName());
    }
}
