package com.github.androidutils.eventbus;

import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.ExtractEditText;
import android.os.Parcelable;

import com.google.common.base.Preconditions;

/**
 * This one is to post events. Events will be packed into Intents and broadcast.
 * All registered {@link IntentEventReceiver} will get the intent,
 * {@link ExtractEditText} the event and pass it to the internal bus.
 * 
 */
public class IntentEventBroadcastSender<T extends Parcelable> {

    private final Context context;

    public IntentEventBroadcastSender(Context context) {
        this.context = Preconditions.checkNotNull(context);
    }

    public void sendBroadcast(T event) {
        Intent intent = new Intent(IntentEventReceiver.ACTION_EVENT + event.getClass().getSimpleName());
        intent.putExtra(IntentEventReceiver.EXTRA_EVENT, event);
        context.sendBroadcast(intent);
    }

    public void sendStickyBroadcast(T event) {
        Intent intent = new Intent(IntentEventReceiver.ACTION_EVENT + event.getClass().getSimpleName());
        intent.putExtra(IntentEventReceiver.EXTRA_EVENT, event);
        context.sendStickyBroadcast(intent);
    }
}
