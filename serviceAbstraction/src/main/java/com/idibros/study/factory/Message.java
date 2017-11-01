package com.idibros.study.factory;

import lombok.Getter;

/**
 * Created by dongba on 2017-10-31.
 */
public class Message {

    @Getter
    String text;

    private Message (String text) {
        this.text = text;
    }

    public static Message newMessage (String text) {
        return new Message(text);
    }

}
