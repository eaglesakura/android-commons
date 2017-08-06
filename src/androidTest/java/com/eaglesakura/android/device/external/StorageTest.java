package com.eaglesakura.android.device.external;

import com.eaglesakura.log.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class StorageTest {

    @Test
    public void 容量が取得できる() throws Throwable {
        Storage storage = Storage.getExternalStorage(InstrumentationRegistry.getTargetContext());
        assertNotNull(storage);

        Logger.out(Logger.LEVEL_DEBUG, getClass().getName(), "Storage path[%s] size[%.1f GB Free / %.1f GB Max]", storage.getPath().getAbsolutePath(), storage.getFreeSizeGB(), storage.getMaxSizeGB());
        assertNotNull(storage.getPath());
        assertTrue(storage.getMaxSize() > 0);
        assertTrue(storage.getFreeSize() >= 0);
        assertTrue(storage.getMaxSize() >= storage.getFreeSize());
    }

    @Test
    public void データディレクトリが正しく取得できる() throws Throwable {
        Storage rootPath = Storage.getExternalStorage(InstrumentationRegistry.getTargetContext());
        Storage dataPath = Storage.getExternalDataStorage(InstrumentationRegistry.getTargetContext());


        Logger.out(Logger.LEVEL_DEBUG, getClass().getName(), "Root [%s] size[%.1f GB Free / %.1f GB Max] SDCard[%s]", rootPath.getPath().getAbsolutePath(), rootPath.getFreeSizeGB(), rootPath.getMaxSizeGB(), String.valueOf(rootPath.isSdcard()));
        Logger.out(Logger.LEVEL_DEBUG, getClass().getName(), "Data [%s] size[%.1f GB Free / %.1f GB Max] SDCard[%s]", dataPath.getPath().getAbsolutePath(), dataPath.getFreeSizeGB(), dataPath.getMaxSizeGB(), String.valueOf(rootPath.isSdcard()));

        // rootPathの配下に無ければならない
        assertTrue(dataPath.getPath().getAbsolutePath().startsWith(rootPath.getPath().getAbsolutePath()));
    }
}