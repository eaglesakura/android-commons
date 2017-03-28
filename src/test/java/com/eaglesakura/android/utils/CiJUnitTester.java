package com.eaglesakura.android.utils;

import org.junit.Before;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import android.content.Context;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, packageName = BuildConfig.APPLICATION_ID, sdk = 21)
public abstract class CiJUnitTester {

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private void initializeLogger() {
        ShadowLog.stream = System.out;
    }

    @Before
    public void onSetup() {
        mContext = RuntimeEnvironment.application;
    }
}
