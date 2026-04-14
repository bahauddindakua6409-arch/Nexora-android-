package com.nexora.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.getcapacitor.BridgeActivity;

/**
 * NEXORA — Main Activity
 * ─────────────────────────────────────────────────────────────
 * Capacitor BridgeActivity extend করে।
 * Notification থেকে call accept করলে JS side কে জানায়।
 * ─────────────────────────────────────────────────────────────
 * android/app/src/main/java/com/nexora/app/ এ রাখুন
 *
 * ⚠️ যদি আগে থেকে MainActivity.java থাকে, তাহলে
 *    শুধু onCreate ও onNewIntent method দুটো copy করুন।
 */
public class MainActivity extends BridgeActivity {

    private static final String TAG = "NexoraMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Notification থেকে এলে call data handle করো
        handleCallIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // অ্যাপ already open থাকলেও call intent handle করো
        handleCallIntent(intent);
    }

    /**
     * Notification এর Accept বোতাম থেকে এলে
     * WebView/JS এ call data পাঠাও যাতে call screen দেখায়।
     */
    private void handleCallIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (CallNotificationManager.ACTION_ACCEPT.equals(action)) {
            String callId    = intent.getStringExtra(CallNotificationManager.EXTRA_CALL_ID);
            String callerUid = intent.getStringExtra(CallNotificationManager.EXTRA_CALLER_UID);
            String mediaType = intent.getStringExtra(CallNotificationManager.EXTRA_MEDIA_TYPE);

            Log.d(TAG, "Call accept intent: callId=" + callId);

            // Ringtone বন্ধ করো
            CallNotificationManager.stopRingtone();
            CallNotificationManager.stopVibration();

            // Notification dismiss করো
            new CallNotificationManager(this).dismissCallNotification();

            // WebView লোড হওয়ার পর JS এ event পাঠাও
            if (callId != null && !callId.isEmpty()) {
                final String finalCallId    = callId;
                final String finalCallerUid = callerUid != null ? callerUid : "";
                final String finalMediaType = mediaType  != null ? mediaType  : "audio";

                // WebView ready হওয়ার জন্য ছোট delay
                getBridge().getWebView().postDelayed(() -> {
                    String js = String.format(
                        "window.dispatchEvent(new CustomEvent('nexora:accept_call', {" +
                        "  detail: {" +
                        "    callId: '%s'," +
                        "    callerUid: '%s'," +
                        "    mediaType: '%s'" +
                        "  }" +
                        "}));",
                        finalCallId, finalCallerUid, finalMediaType
                    );
                    getBridge().getWebView().evaluateJavascript(js, null);
                    Log.d(TAG, "JS call accept event fired");
                }, 1500); // 1.5 সেকেন্ড delay — WebView load হওয়ার সময়
            }
        }
    }
}
