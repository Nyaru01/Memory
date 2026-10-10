package fr.memomimi.app;

import android.content.Context;
import android.media.AudioManager;

/** Android focus shared by music, effects and narration, released on pause. */
final class AudioFocus {
    private final AudioManager manager;
    private final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final AudioManager.OnAudioFocusChangeListener listener;
    private boolean requested;
    volatile boolean allowed = true;

    AudioFocus(Context context, Runnable stop, Runnable resume) {
        manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        listener = change -> handler.post(() -> {
            if (!requested) return;
            allowed = change == AudioManager.AUDIOFOCUS_GAIN;
            if (allowed) resume.run(); else stop.run();
        });
    }
    boolean request() {
        if (requested) return allowed;
        requested = true;
        allowed = manager == null || manager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return allowed;
    }
    void release() {
        if (requested && manager != null) manager.abandonAudioFocus(listener);
        requested = false; allowed = true; handler.removeCallbacksAndMessages(null);
    }
}
