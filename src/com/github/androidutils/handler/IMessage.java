package com.github.androidutils.handler;

public interface IMessage {

    void setArg1(int i);

    void setObj(Object obj);

    Object obj();

    void sendToTarget();

    int what();

    void setArg2(int minute);

    void setWhat(int change);

    int arg1();

    int arg2();

    void copyFrom(IMessage msg);

}
