package com.github.androidutils.eventbus;

import java.lang.reflect.Method;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import com.github.androidutils.logger.Logger;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;

public class IntentEventReceiver {
    public static final String ACTION_EVENT = "com.github.androidutils.eventbus.IntentEventReceiver.ACTION_EVENT";
    public static final String EXTRA_EVENT = "EXTRA_EVENT";

    private final EventBus eventBus;
    private final Context context;
    private final BroadcastReceiver broadcastReceiver;
    private final Object listener;

    private final Logger logger;

    public IntentEventReceiver(Context context, Object listener, Logger pLogger) {
        this.eventBus = new EventBus();
        this.listener = Preconditions.checkNotNull(listener);
        this.context = Preconditions.checkNotNull(context);
        this.logger = Preconditions.checkNotNull(pLogger);

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleIntent(intent);
            }
        };

        eventBus.register(listener);
        eventBus.register(new Object() {
            @Subscribe
            public void handle(DeadEvent deadEvent) {
                logger.e("Event was not handled" + deadEvent.toString());
            }
        });
    }

    public void registerForBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        for (Class<?> clazz : getParamsClassesOfAnnotatedMethods(listener.getClass())) {
            intentFilter.addAction(ACTION_EVENT + clazz.getSimpleName());
        }
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    public void unregisterFromBroadcasts() {
        context.unregisterReceiver(broadcastReceiver);
    }

    public boolean handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_EVENT)) {
            Parcelable event = intent.getParcelableExtra(EXTRA_EVENT);
            eventBus.post(event);
            return true;
        } else return false;
    }

    @SuppressWarnings("unchecked")
    public <T extends Parcelable> Optional<T> getLastEvent(Class<T> eventClass) {
        Intent intent = context.registerReceiver(null, new IntentFilter(ACTION_EVENT + eventClass.getSimpleName()));
        Optional<T> optional;
        if (intent != null && intent.hasExtra(EXTRA_EVENT)) {
            optional = (Optional<T>) Optional.of(intent.getParcelableExtra(EXTRA_EVENT));
        } else {
            optional = Optional.absent();
        }

        return optional;
    }

    private static ImmutableList<Class<?>> getParamsClassesOfAnnotatedMethods(Class<?> clazz) {
        Set<? extends Class<?>> supers = TypeToken.of(clazz).getTypes().rawTypes();
        Set<Class<?>> paramsClasses = Sets.newHashSet();
        for (Class<?> superClazz : supers) {
            for (Method superClazzMethod : superClazz.getMethods()) {
                if (superClazzMethod.isAnnotationPresent(Subscribe.class)) {
                    Class<?>[] parameterTypes = superClazzMethod.getParameterTypes();
                    if (parameterTypes.length != 1)
                        throw new IllegalArgumentException("Method " + superClazzMethod
                                + " has @Subscribe annotation, but requires " + parameterTypes.length
                                + " arguments.  Event subscriber methods must require a single argument.");

                    paramsClasses.add(parameterTypes[0]);
                }
            }
        }
        return ImmutableList.copyOf(paramsClasses);
    }
}
