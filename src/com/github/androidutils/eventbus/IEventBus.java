package com.github.androidutils.eventbus;

import android.os.Parcelable;

public interface IEventBus {
    public void post(Parcelable event);
}
