package com.eaglesakura.android.context;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Activity/Fragment/Serviceを強参照しない形で保持を行う。
 */
public class WeakContext {

    /**
     * MainContext
     */
    final protected WeakReference<Object> mOwner;

    /**
     * ApplicationContext
     */
    final private Context mAppContext;

    /**
     * Activityから生成する
     *
     * @param activity 生成元のActivity
     */
    public WeakContext(Activity activity) {
        this(activity, activity.getApplicationContext());
    }

    /**
     * Serviceから生成する
     *
     * @param service 参照元のService
     */
    public WeakContext(Service service) {
        this(service, service.getApplicationContext());
    }

    protected WeakContext(Object obj, Context appContext) {
        this.mOwner = new WeakReference<Object>(obj);
        this.mAppContext = appContext;
    }

    public Service getService() {
        Object obj = mOwner.get();
        if (obj instanceof Service) {
            return (Service) obj;
        }
        return null;
    }

    @SuppressLint("NewApi")
    public Activity getActivity() {
        Object obj = mOwner.get();
        if (obj instanceof Activity) {
            return (Activity) obj;
        } else if (obj instanceof Fragment) {
            return ((Fragment) obj).getActivity();
        }
        return null;
    }

    public Fragment getFragment() {
        Object obj = mOwner.get();
        if (obj instanceof Fragment) {
            return (Fragment) obj;
        }
        return null;
    }

    public android.support.v4.app.Fragment getSupportFragment() {
        Object obj = mOwner.get();
        if (obj instanceof android.support.v4.app.Fragment) {
            return (android.support.v4.app.Fragment) obj;
        }
        return null;
    }

    /**
     * オーナーオブジェクトが有効であればtrue
     */
    public boolean isExistOwner() {
        return mOwner.get() != null;
    }

    /**
     * app context
     */
    public Context getApplicationContext() {
        return mAppContext;
    }
}
