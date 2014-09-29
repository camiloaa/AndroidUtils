package com.github.androidutils.handler;

public interface IMessage {

    IMessage setWhat(int what);

    IMessage setArg1(int arg1);

    IMessage setArg2(int arg2);

    IMessage setObj(Object obj);

    IMessage copyFrom(IMessage msg);

    void sendToTarget();

    Object obj();

    int what();

    int arg1();

    int arg2();
}
