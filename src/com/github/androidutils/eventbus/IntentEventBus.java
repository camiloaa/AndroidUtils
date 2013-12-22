package com.github.androidutils.eventbus;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.os.Parcelable;

/**
 * This one is to post events. Events will be packed into Intents and broadcast.
 * All registered {@link EventBusBroadcastReceiver} will get the intent,
 * {@link ExtractEditText} the event and pass it to the internal bus.
 * 
 */
public class IntentEventBus implements IEventBus {
    public static final String ACTION_EVENT = "com.github.androidutils.eventbus.IntentEventBus.ACTION_EVENT";
    public static final String EXTRA_EVENT = "EXTRA_EVENT";

    Context context;

    public IntentEventBus(Context context) {
        this.context = context;
    }

    public void post(Parcelable event) {
        Intent intent = new Intent(ACTION_EVENT);
        intent.putExtra(EXTRA_EVENT, event);
        context.sendBroadcast(intent);
    }
}
