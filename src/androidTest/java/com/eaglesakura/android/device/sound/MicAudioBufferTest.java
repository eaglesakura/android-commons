package com.eaglesakura.android.device.sound;


import com.eaglesakura.android.device.sound.mic.MicBuffer;
import com.eaglesakura.log.Logger;
import com.eaglesakura.util.Timer;

import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class MicAudioBufferTest {

    @Test
    public void バッファのリスティングが行える() throws Throwable {
        MicAudioBuffer audioBuffer = MicAudioBuffer.Builder.from(InstrumentationRegistry.getContext()).build();

        assertNotNull(audioBuffer.nextWriteBuffer());
        assertEquals(audioBuffer.listBuffers().size(), 1);
        assertEquals(audioBuffer.listBuffers().get(0), audioBuffer.mBuffers.get(0));    // 正しく先頭バッファである

        // 適当に読み進める
        for (int i = 0; i < 100; ++i) {
            audioBuffer.nextWriteBuffer();
        }
        assertEquals(audioBuffer.listBuffers().size(), audioBuffer.mBuffers.size());
    }


    @Test
    public void RMSを監視できる() throws Throwable {
        Timer timer = new Timer();

        MicAudioBuffer audioBuffer = MicAudioBuffer.Builder.from(InstrumentationRegistry.getContext()).build();
        audioBuffer.record((buffer, audio, audioContext) -> {
            MicBuffer currentBuffer = audioContext.getCurrentBuffer();
            Logger.out(Logger.LEVEL_DEBUG, "Mic", "onRecord context size[%d] RMS[%.3f] AVG[%.3f] spike[%s]", audioContext.getMicBufferList().size(), currentBuffer.getRMS(), audioContext.getAverageRMS(), "" + audioContext.isSpikeRMS(0.05));
        }, () -> timer.end() > (1000 * 10));
    }
}