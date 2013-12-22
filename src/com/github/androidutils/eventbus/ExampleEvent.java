package com.github.androidutils.eventbus;

public class ExampleEvent extends ReflectionParcelableEvent {
    public boolean booleanValue;
    public String string;

    public static ExampleEvent create(boolean booleanValue, String string) {
        ExampleEvent event = new ExampleEvent();
        event.booleanValue = booleanValue;
        event.string = string;
        return event;
    }

    @Override
    public String toString() {
        return "ExampleEvent [booleanValue=" + booleanValue + ", string=" + string + "]";
    }
}
