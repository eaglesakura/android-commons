package com.eaglesakura.android.util;

import com.eaglesakura.lambda.Matcher1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * View系の便利メソッド
 */
public class ViewUtil {

    /**
     * CardView配下のitemを横幅に合わせてマッチングする
     */
    public static void matchCardWidth(View itemView) {
        if (itemView.getLayoutParams() == null) {
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    /**
     * ImageViewの横幅いっぱいになるようにセットする
     * <br>
     * Viewの高さは画像に合わせて変更される。
     */
    public static void setWidthMatchImage(ImageView view, Bitmap image) {
        Matrix matrix = new Matrix();
        float scale = (float) view.getWidth() / (float) image.getWidth();
        int newHeight = (int) (scale * image.getHeight());
        matrix.postScale(
                scale, scale
        );
        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.setImageMatrix(matrix);
        setViewHeight(view, newHeight);

        // 画像を再設定する
        view.setImageBitmap(image);

    }

    /**
     * Viewの高さを再設定する
     */
    public static void setViewHeight(View itemView, int height) {
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = height;
        itemView.setLayoutParams(params);
    }

    /**
     * カーソル位置にテキストを挿入する
     */
    public static void insertTextInCursor(EditText editText, String text) {
        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        Editable editable = editText.getText();
        editable.replace(Math.min(start, end), Math.max(start, end), text);
    }

    /**
     * 入力を整数に限るようにする
     */
    public static void setInputIntegerOnly(EditText editText) {
        editText.setMaxLines(1);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    /**
     * 入力を数値に限るようにする
     */
    public static void setInputDecimal(EditText editText) {
        editText.setMaxLines(1);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    /**
     * 実数を取得する
     */
    public static double getDoubleValue(EditText text, double defValue) {
        try {
            return Double.valueOf(text.getText().toString());
        } catch (Exception e) {
            return defValue;
        }
    }

    /**
     * 整数を取得する
     */
    public static long getLongValue(EditText text, long defValue) {
        try {
            return Long.valueOf(text.getText().toString());
        } catch (Exception e) {
            return defValue;
        }
    }

    public static int getTouchPointerId(MotionEvent event) {
        int pointerId = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        return event.getPointerId(pointerId);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static WebView setupDefault(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                return false;
            }
        });
        return webView;
    }

    public static <T extends View> T findViewByChildAt(View root, int... indices) {
        for (int index : indices) {
            root = ((ViewGroup) root).getChildAt(index);
        }

        return (T) root;
    }

    public static <T extends View> T findViewById(View view, @IdRes int... id) {
        for (int i : id) {
            if (view == null) {
                return null;
            }
            view = view.findViewById(i);
        }
        return (T) view;
    }

    public static <T extends View> T findViewById(View view, Class<T> clazz, @IdRes int... id) {
        return findViewById(view, id);
    }

    public static <T extends View> T findViewById(Activity activity, Class<T> clazz, @IdRes int... id) {
        return findViewById(activity, id);
    }

    public static <T extends View> T findViewById(Activity activity, @IdRes int... id) {
        View view = null;

        for (int i : id) {
            if (view == null) {
                view = activity.findViewById(i);
            } else {
                view = view.findViewById(i);
            }

            if (view == null) {
                return null;
            }
        }
        return (T) view;
    }

    public static <T extends View> T findViewByMatcher(View view, Matcher1<View> matcher) {
        if (view == null) {
            return null;
        }

        try {
            // ヒットした
            if (matcher.match(view)) {
                return (T) view;
            }

            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                    View child = findViewByMatcher(viewGroup.getChildAt(i), matcher);
                    if (child != null) {
                        return (T) child;
                    }
                }
            }

            // ヒットしなかった
            return null;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static List<View> listViews(View root, Matcher1<View> matcher) {
        List<View> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        try {
            // ヒットした
            if (matcher.match(root)) {
                result.add(root);
            }

            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                    result.addAll(listViews(viewGroup.getChildAt(i), matcher));
                }
            }

            // ヒットしなかった
            return result;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * スクリーン上の座標を取得する
     */
    public static Rect getScreenArea(@NonNull View view) {
        Rect area = new Rect();

        int[] viewInWindow = new int[2];
        int[] viewOnScreen = new int[2];
        int[] windowOnScreen = new int[2];

        view.getLocationInWindow(viewInWindow);
        view.getLocationOnScreen(viewOnScreen);
        windowOnScreen[0] = viewOnScreen[0] - viewInWindow[0];
        windowOnScreen[1] = viewOnScreen[1] - viewInWindow[1];

        view.getGlobalVisibleRect(area);
        area.offset(windowOnScreen[0], windowOnScreen[1]);
        return area;
    }

    public static Rect getWindowArea(@NonNull View view) {
        int[] xy = new int[2];
        view.getLocationInWindow(xy);
        return new Rect(xy[0], xy[1], xy[0] + view.getWidth(), xy[1] + view.getHeight());
    }
}
