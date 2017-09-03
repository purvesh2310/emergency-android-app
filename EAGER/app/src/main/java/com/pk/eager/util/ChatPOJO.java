package com.pk.eager.util;

/**
 * Created by NB on 08/07/17.
 * Chat Object that used to store msg.
 */

public class ChatPOJO {

    private String uid;
    private String message;
    private String messenger;
    private String timestamp;

    public ChatPOJO() {
    }

    public ChatPOJO(String messenger, String message, String timestamp) {
        this.messenger = messenger;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
