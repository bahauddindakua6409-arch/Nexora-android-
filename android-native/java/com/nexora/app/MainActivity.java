package com.nexora.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    private static final String TAG = "NexoraMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleCallIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleCallIntent(intent);
    }

    private void handleCallIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (CallNotificationManager.ACTION_ACCEPT.equals(action)) {
            String callId = intent.getStringExtra(CallNotificationManager.EXTRA_CALL_ID);
            String callerUid = intent.getStringExtra(CallNotificationManager.EXTRA_CALLER_UID);
            String mediaType = intent.getStringExtra(CallNotificationManager.EXTRA_MEDIA_TYPE);

            CallNotificationManager.stopRingtone();
            CallNotificationManager.stopVibration();
            new CallNotificationManager(this).dismissCallNotification();

            if (callId != null && !callId.isEmpty()) {
                final String finalCallId = callId;
                final String finalCallerUid = callerUid != null ? callerUid : "";
                final String finalMediaType = mediaType != null ? mediaType : "audio";

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
                }, 1500);
            }
        }
    }
}
