package com.eaglesakura.android.util;

import com.eaglesakura.util.CollectionUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PermissionUtil {

    @SuppressLint("NewApi")
    public enum PermissionType {
        SelfLocation {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                };
            }
        },

        UsageStatus {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.PACKAGE_USAGE_STATS,
                };
            }
        },

        BluetoothLE {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                };
            }
        },

        GoogleMap {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                };
            }
        },

        ExternalStorage {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                };
            }
        },

        Camera {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.CAMERA
                };
            }
        },

        /**
         * マイクからの集音を許可する
         */
        RecordAudio {
            @Override
            public String[] getPermissions() {
                return new String[]{
                        Manifest.permission.RECORD_AUDIO
                };
            }
        };

        public abstract String[] getPermissions();
    }

    /**
     * パーミッション一覧を取得する
     */
    public static Set<String> listPermissions(PermissionType[] types) {
        Set<String> result = new HashSet<>();
        for (PermissionType pm : types) {
            for (String permission : pm.getPermissions()) {
                result.add(permission);
            }
        }

        return result;
    }

    /**
     * パーミッション一覧を取得する
     */
    public static Set<String> listPermissions(Iterable<PermissionType> types) {
        Set<String> result = new HashSet<>();
        for (PermissionType pm : types) {
            for (String permission : pm.getPermissions()) {
                result.add(permission);
            }
        }

        return result;
    }

    public static boolean isSupportRuntimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * Runtime Permissionチェックを行う
     */
    public static boolean isRuntimePermissionGranted(Context context, PermissionType... types) {
        Set<String> permissions = new HashSet<>();
        for (PermissionType type : types) {
            for (String pm : type.getPermissions()) {
                permissions.add(pm);
            }
        }
        return isRuntimePermissionGranted(context, permissions);
    }

    /**
     * Runtime Permissionチェックを行う
     */
    public static boolean isRuntimePermissionGranted(Context context, List<PermissionType> types) {
        Set<String> permissions = new HashSet<>();
        for (PermissionType type : types) {
            for (String pm : type.getPermissions()) {
                permissions.add(pm);
            }
        }
        return isRuntimePermissionGranted(context, permissions);
    }

    public static boolean isRuntimePermissionGranted(Context context, Collection<String> permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Runtime Permission非対応なので常にtrue
            return true;
        }

        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * call
     * boolean isRuntimePermissionGranted(Context context, Collection<String> permissions)
     */
    public static boolean isRuntimePermissionGranted(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Runtime Permission非対応なので常にtrue
            return true;
        }

        return isRuntimePermissionGranted(context, CollectionUtil.asList(permissions));
    }

    /**
     * 指定したPermissionに対応していたらtrue
     */
    public static boolean supportedPermission(PackageManager packageManager, PackageInfo info, String permissionName) {
        return packageManager.checkPermission(permissionName, info.packageName) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * システムレイヤーへのオーバーレイが許可されている場合はtrue
     */
    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        return Settings.canDrawOverlays(context);
    }

    @SuppressLint("NewApi")
    public static boolean isUsageStatsAllowed(Context context) {
        if (Build.VERSION.SDK_INT <= 19) {
            return true;
        }

        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int uid = android.os.Process.myUid();
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, uid, context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

}
