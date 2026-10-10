package fr.memomimi.app;

import android.media.AudioManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=23)
public class AudioFocusTest {
    @Test public void lossStopsAudioAndLateGainAfterPauseCannotRestartIt() throws Exception {
        int[] calls={0,0}; AudioFocus focus=new AudioFocus(RuntimeEnvironment.getApplication(),()->calls[0]++,()->calls[1]++);
        assertTrue(focus.request());
        java.lang.reflect.Field field=AudioFocus.class.getDeclaredField("listener"); field.setAccessible(true);
        AudioManager.OnAudioFocusChangeListener listener=(AudioManager.OnAudioFocusChangeListener)field.get(focus);
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS_TRANSIENT);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
        assertEquals(1,calls[0]); assertFalse(focus.request());
        listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle(); assertEquals(1,calls[1]);
        focus.release(); listener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper()).idle(); assertEquals(1,calls[1]);
    }
}
