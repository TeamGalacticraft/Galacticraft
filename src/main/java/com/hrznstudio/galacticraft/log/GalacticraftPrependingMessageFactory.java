package com.hrznstudio.galacticraft.log;

import org.apache.logging.log4j.message.AbstractMessageFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.StringFormattedMessage;

public final class GalacticraftPrependingMessageFactory extends AbstractMessageFactory {
    public static final GalacticraftPrependingMessageFactory INSTANCE = new GalacticraftPrependingMessageFactory();
    private static final String APPENDED_STRING = "[Galacticraft] ";

    @Override
    public Message newMessage(CharSequence message) {
        return new SimpleMessage(APPENDED_STRING + message);
    }

    @Override
    public Message newMessage(Object object) {
        return new SimpleMessage(APPENDED_STRING + object);
    }

    @Override
    public Message newMessage(String message) {
        return new SimpleMessage(APPENDED_STRING + message);
    }

    @Override
    public Message newMessage(final String message, final Object... params) {
        return new StringFormattedMessage(APPENDED_STRING + message, params);
    }

    @Override
    public Message newMessage(final String message, final Object object) {
        return new StringFormattedMessage(APPENDED_STRING + message, object);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4, final Object object5) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4, object5);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4, final Object object5, final Object object6) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4, object5, object6);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4, final Object object5, final Object object6, final Object object7) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4, object5, object6, object7);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4, final Object object5, final Object object6, final Object object7, final Object object8) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4, object5, object6, object7, object8);
    }

    @Override
    public Message newMessage(final String message, final Object object, final Object object1, final Object object2, final Object object3, final Object object4, final Object object5, final Object object6, final Object object7, final Object object8, final Object object9) {
        return new StringFormattedMessage(APPENDED_STRING + message, object, object1, object2, object3, object4, object5, object6, object7, object8, object9);
    }
}
