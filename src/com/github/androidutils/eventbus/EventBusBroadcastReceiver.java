package com.github.androidutils.eventbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.common.eventbus.EventBus;

public class EventBusBroadcastReceiver extends BroadcastReceiver {

    private final EventBus eventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (IntentEventBus.ACTION_EVENT.equals(intent.getAction())) {
            eventBus.post(intent.getParcelableExtra(IntentEventBus.EXTRA_EVENT));
        }
    }

    public EventBusBroadcastReceiver() {
        this.eventBus = new EventBus();
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void register(Object object, Context context) {
        context.registerReceiver(this, new IntentFilter(IntentEventBus.ACTION_EVENT));
        eventBus.register(object);
    }
}
