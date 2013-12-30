package com.github.androidutils.eventbus;

import java.lang.reflect.Method;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;

public class EventBusBroadcastReceiver extends BroadcastReceiver {

    private final EventBus eventBus;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.hasExtra(IntentEventBus.EXTRA_EVENT)) {
            eventBus.post(intent.getParcelableExtra(IntentEventBus.EXTRA_EVENT));
        }
    }

    public EventBusBroadcastReceiver() {
        this.eventBus = new EventBus();
    }

    public void register(Object object, Context context) {
        this.context = context;
        IntentFilter intentFilter = new IntentFilter();
        for (Class<?> clazz : getParamsClassesOfAnnotatedMethods(object.getClass())) {
            intentFilter.addAction(IntentEventBus.ACTION_EVENT + clazz.getSimpleName());
        }
        context.registerReceiver(this, intentFilter);

        eventBus.register(object);
    }

    public void unregister() {
        context.unregisterReceiver(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Parcelable> Optional<T> getLastEvent(Class<T> eventClass) {
        Intent intent = context.registerReceiver(null,
                new IntentFilter(IntentEventBus.ACTION_EVENT + eventClass.getSimpleName()));
        Optional<T> optional;
        if (intent != null && intent.hasExtra(IntentEventBus.EXTRA_EVENT)) {
            optional = (Optional<T>) Optional.of(intent.getParcelableExtra(IntentEventBus.EXTRA_EVENT));
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
