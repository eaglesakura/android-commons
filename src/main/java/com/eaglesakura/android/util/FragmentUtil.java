package com.eaglesakura.android.util;

import com.eaglesakura.lambda.Matcher1;
import com.eaglesakura.util.CollectionUtil;
import com.eaglesakura.util.ReflectionUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FragmentUtil {

    /**
     * 所属する全てのFragmentを列挙する
     */
    public static List<Fragment> listFragments(FragmentManager fragmentManager) {
        return listFragments(fragmentManager, fragment -> true);
    }

    public static <T> T findInterface(Fragment fragment, Context context, Class<? extends T> clazz) {
        T interfaceOrNull = findInterfaceOrNull(fragment, context, clazz);
        if (interfaceOrNull == null) {
            throw new IllegalStateException("Interface not found.");
        }
        return interfaceOrNull;
    }

    /**
     * Fragment Treeからインターフェースを検索し、見つかれば最初のオブジェクトを返却する。
     * 見つからない場合はnullを返却する
     */
    public static <T> T findInterfaceOrNull(Fragment fragment, Context context, Class<? extends T> clazz) {
        if (context instanceof AppCompatActivity) {
            List<? extends T> ts = listInterfaces(((AppCompatActivity) context).getSupportFragmentManager(), clazz);
            if (ts.isEmpty()) {
                return null;
            } else {
                return ts.get(0);
            }
        } else {
            List<? extends T> ts = listInterfaces(fragment.getFragmentManager(), clazz);
            if (ts.isEmpty()) {
                return null;
            } else {
                return ts.get(0);
            }
        }
    }

    /**
     * 指定した条件に合致するFragmentを全て列挙する。
     *
     * このメソッドはChildFragmentを再帰的に検索する
     */
    @SuppressLint("RestrictedApi")
    @NonNull
    public static List<Fragment> listFragments(FragmentManager fragmentManager, Matcher1<Fragment> matcher) {
        List<Fragment> result = new ArrayList<>();

        try {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (!CollectionUtil.isEmpty(fragments)) {
                for (Fragment frag : fragments) {
                    if (frag != null && matcher.match(frag)) {
                        result.add(frag);
                    }

                    // 子Fragmentを足し込む
                    if (frag != null) {
                        FragmentManager childFragmentManager = null;
                        try {
                            childFragmentManager = frag.getChildFragmentManager();
                        } catch (IllegalStateException e) {
                            // not attached
                        }

                        if (childFragmentManager != null) {
                            result.addAll(listFragments(childFragmentManager, matcher));
                        }
                    }
                }
            }
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Activity及びFragment全てをチェックし、指定したインターフェースを列挙する。
     *
     * @param activity       検索対象Activity
     * @param checkInterface チェック対象Fragment
     */
    public static <T> List<T> listInterfaces(AppCompatActivity activity, Class<? extends T> checkInterface) {
        List<T> result = new ArrayList<>();
        if (ReflectionUtil.instanceOf(activity, checkInterface)) {
            result.add((T) activity);
        }
        result.addAll(listInterfaces(activity.getSupportFragmentManager(), checkInterface));
        return result;
    }

    /**
     * 指定したインターフェースに合致するFragmentを全て列挙する。
     *
     * このメソッドはChildFragmentを再帰的に検索する
     */
    @SuppressLint("RestrictedApi")
    @NonNull
    public static <T> List<T> listInterfaces(FragmentManager fragmentManager, Class<? extends T> checkInterface) {
        List<T> result = new ArrayList<>();

        try {
            List<Fragment> fragments = fragmentManager.getFragments();
            if (!CollectionUtil.isEmpty(fragments)) {
                for (Fragment frag : fragments) {
                    if (ReflectionUtil.instanceOf(frag, checkInterface)) {
                        result.add((T) frag);
                    }

                    // 子Fragmentを足し込む
                    if (frag != null) {
                        result.addAll(listInterfaces(frag.getChildFragmentManager(), checkInterface));
                    }
                }
            }
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
