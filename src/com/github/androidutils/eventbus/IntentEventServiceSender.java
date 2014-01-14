package com.github.androidutils.eventbus;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.google.common.base.Preconditions;

/**
 * This one is to send events to a service. Events will be packed into Intents
 * and sent. All registered {@link IntentEventReceiver} will get the intent,
 * unparcel the event and pass it to the internal bus.
 * 
 */
public class IntentEventServiceSender<T extends Parcelable> {

    private final Context context;
    private final Class<? extends Service> clazz;

    public static <T extends Parcelable> IntentEventServiceSender<T> create(Context context,
            Class<? extends Service> clazz) {
        return new IntentEventServiceSender<T>(context, clazz);
    }

    private IntentEventServiceSender(Context context, Class<? extends Service> clazz) {
        this.context = Preconditions.checkNotNull(context);
        this.clazz = Preconditions.checkNotNull(clazz);
    }

    public void sendEvent(T event) {
        context.startService(createIntent(event));
    }

    public Intent createIntent(T event) {
        Intent intent = new Intent(IntentEventReceiver.ACTION_EVENT + event.getClass().getSimpleName());
        intent.setClass(context, clazz);
        intent.putExtra(IntentEventReceiver.EXTRA_EVENT, event);
        return intent;
    }

    public PendingIntent createPendingIntent(T event) {
        return PendingIntent.getService(context, 0, createIntent(event), 0);
    }
}
