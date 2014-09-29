package com.github.androidutils.handler;

public interface IHandler {

    IMessage obtainMessage(int what, Object object);

    void sendMessageAtFrontOfQueue(IMessage message);

    void sendMessage(IMessage message);

    IMessage obtainMessage();

    IMessage obtainMessage(int what);

    void removeMessages(int what);
}
