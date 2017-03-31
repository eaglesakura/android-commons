package com.eaglesakura.android.util;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

public class DrawableUtil {
    @NonNull
    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int drawableId) {
        if (drawableId == 0) {
            return null;
        }
        try {
            return ResourcesCompat.getDrawable(context.getResources(), drawableId, context.getTheme());
        } catch (Exception e) {
            return getVectorDrawable(context, drawableId);
        }
    }

    @NonNull
    public static VectorDrawableCompat getVectorDrawable(@NonNull Context context, @DrawableRes int drawableId) {
        return getVectorDrawable(context, drawableId, 0);
    }

    @NonNull
    public static VectorDrawableCompat getVectorDrawable(@NonNull Context context, @DrawableRes int drawableId, @ColorRes int colorId) {
        VectorDrawableCompat drawableCompat = VectorDrawableCompat.create(context.getResources(), drawableId, context.getTheme());
        if (colorId != 0) {
            DrawableCompat.setTint(drawableCompat, ContextCompat.getColor(context, colorId));
        }
        return drawableCompat;
    }
}
