package com.eaglesakura.android.device.external;

import com.eaglesakura.util.StringUtil;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Storageの容量やパス等をラップする
 */
public class Storage {
    final File mRoot;

    /**
     * 最大容量
     */
    long mMaxSize = -1;

    /**
     * 空き容量
     */
    long mFreeSize;

    public Storage(File root) {
        mRoot = root;
        reloadInfo();
    }

    /**
     * 実際のパスを取得する
     */
    public File getPath() {
        return mRoot;
    }

    /**
     * 容量をbyte単位で取得する
     */
    public long getMaxSize() {
        return mMaxSize;
    }

    /**
     * 空き容量をbyte単位で取得する
     */
    public long getFreeSize() {
        return mFreeSize;
    }

    /**
     * 容量をGB単位で取得する
     */
    public double getMaxSizeGB() {
        return (double) mMaxSize / (double) (1024 * 1024 * 1024);
    }

    /**
     * 空き容量をGB単位で取得する
     */
    public double getFreeSizeGB() {
        return (double) mFreeSize / (double) (1024 * 1024 * 1024);
    }

    /**
     * 使用中のサイズをbyte単位で取得する
     */
    public long getUsingSize() {
        return mMaxSize - mFreeSize;
    }

    /**
     * 使用中のサイズをGB単位で取得する
     */
    public double getUsingSizeGB() {
        return (double) getUsingSize() / (double) (1024 * 1024 * 1024);
    }

    /**
     * 空き容量を再読み込みする
     */
    public Storage reloadInfo() {
        StatFs stat = new StatFs(mRoot.getAbsolutePath());
        if (Build.VERSION.SDK_INT >= 18) {
            if (this.mMaxSize < 0) {
                this.mMaxSize = stat.getBlockSizeLong() * stat.getBlockCountLong();
            }
            this.mFreeSize = stat.getBlockSizeLong() * stat.getFreeBlocksLong();
        } else {
            if (this.mMaxSize < 0) {
                this.mMaxSize = ((long) stat.getBlockSize() & 0xFFFFFFFFL) * ((long) stat.getBlockCount() & 0xFFFFFFFFL);
            }
            this.mFreeSize = ((long) stat.getBlockSize() & 0xFFFFFFFFL) * ((long) stat.getFreeBlocks() & 0xFFFFFFFFL);
        }
        return this;
    }


    /**
     * /storage/配下でデフォルトで使用されているディレクトリ名テーブル
     */
    private static final Set<String> STORAGE_NG_PATH = new HashSet<>(Arrays.asList(
            "emulated", "self", "sdcard0"  // for Android Default
            , "Private", "UsbDriveA", "UsbDriveB", "UsbDriveC", "UsbDriveD", "UsbDriveE", "UsbDriveF", "knox-emulated"  // for Galaxy
    ));

    /**
     * /storage/以下のディレクトリ一覧
     */
    private static final File[] STORAGE_DIRECTORIES = new File("/storage/").listFiles();

    private static final File[] DEVICE_CUSTOM_PATH = {
            new File("/mnt/sdcard/external_sd"),  // Xperia GX
            new File("/storage/extSdCard")       // Galaxy : Android 4.4-
    };

    private static final String[] STORAGE_ENV_TABLE = {
            "EXTERNAL_ALT_STORAGE",
            "EXTERNAL_STORAGE2",
            "EXTERNAL_STORAGE"
    };

    /**
     * 外部ストレージ領域を取得する。
     *
     * SDカードが挿入されている場合はそちらを優先し、挿入されていない場合は外部ストレージ領域を取得する。
     */
    public static Storage getExternalStorage(Context context) {
        if (STORAGE_DIRECTORIES != null) {
            for (File file : STORAGE_DIRECTORIES) {
                if (STORAGE_NG_PATH.contains(file.getName())) {
                    // 標準パスは適用外
                    continue;
                } else if (file.isDirectory()) {
                    // その他のパスが見つかった
                    return new Storage(file);
                }
            }
        }

        // 事前設定されたテーブルから検索する
        for (File path : DEVICE_CUSTOM_PATH) {
            if (path.isDirectory()) {
                return new Storage(path);
            }
        }


        // 環境変数テーブルから探る
        for (String env : STORAGE_ENV_TABLE) {
            String envPath = System.getenv(env);
            if (!StringUtil.isEmpty(envPath)) {
                File file = new File(envPath);
                if (file.isDirectory()) {
                    return new Storage(file);
                }
            }
        }

        // 標準Storageパスしか残っていない
        try {
            return new Storage(context.getExternalFilesDir(null));
        } catch (Exception e) {
            return new Storage(Environment.getExternalStorageDirectory());
        }
    }
}