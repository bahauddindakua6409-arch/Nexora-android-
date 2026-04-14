package com.nexora.app;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "NexoraFCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        if (data == null || data.isEmpty()) return;
        String type = data.get("type");
        Log.d(TAG, "FCM received, type: " + type);

        if ("incoming_call".equals(type)) {
            handleIncomingCall(data);
        } else if ("call_cancelled".equals(type)) {
            new CallNotificationManager(this).dismissCallNotification();
        } else if ("call_accepted".equals(type)) {
            new CallNotificationManager(this).dismissCallNotification();
        } else {
            handleRegularNotification(data);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM token refreshed");
    }

    private void handleIncomingCall(Map<String, String> data) {
        String callId = data.getOrDefault("callId", "");
        String callerUid = data.getOrDefault("callerUid", "");
        String callerName = data.getOrDefault("callerName", "কেউ");
        String callerPhoto = data.getOrDefault("callerPhoto", "");
        String mediaType = data.getOrDefault("mediaType", "audio");
        String emoji = data.getOrDefault("callerEmoji", "😊");
        new CallNotificationManager(this).showIncomingCallNotification(callId, callerUid, callerName, callerPhoto, mediaType, emoji);
    }

    private void handleRegularNotification(Map<String, String> data) {
        String title = data.getOrDefault("title", "NEXORA");
        String body = data.getOrDefault("body", "নতুন বার্তা");
        String type = data.getOrDefault("type", "message");
        new CallNotificationManager(this).showRegularNotification(title, body, type);
    }
}
