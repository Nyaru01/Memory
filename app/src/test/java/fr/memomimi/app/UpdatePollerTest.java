package fr.memomimi.app;

import android.os.Looper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import java.time.Duration;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
@LooperMode(LooperMode.Mode.PAUSED)
public class UpdatePollerTest {
    @Test public void checksWhileOpenAndStopsInBackground() {
        int[] count = {0};
        UpdatePoller poller = new UpdatePoller(() -> count[0]++);
        try {
            poller.resume(); poller.resume();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertEquals(1, count[0]);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMinutes(3));
            assertEquals(4, count[0]);
            poller.pause();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMinutes(3));
            assertEquals(4, count[0]);
            poller.resume();
            Shadows.shadowOf(Looper.getMainLooper()).idle();
            assertEquals(5, count[0]);
        } finally { poller.pause(); }
    }

    @Test public void rapidResumesAreThrottledWithoutLosingTheNextCheck() {
        int[] count = {0};
        UpdatePoller poller = new UpdatePoller(() -> count[0]++);
        try {
            poller.resume(); Shadows.shadowOf(Looper.getMainLooper()).idle();
            poller.pause();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(10));
            poller.resume();
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(4));
            assertEquals(1, count[0]);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));
            assertEquals(2, count[0]);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMinutes(1));
            assertEquals(3, count[0]);
        } finally { poller.pause(); }
    }
}
