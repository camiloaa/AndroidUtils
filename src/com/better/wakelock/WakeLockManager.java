/*
 * Copyright (C) 2012 Yuriy Kulikov yuriy.kulikov.87@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.better.wakelock;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.better.wakelock.Logger.LogLevel;

/**
 * Utility class to pass {@link WakeLock} objects with intents. It contains a
 * factory, which has to be initialized in {@link Application#onCreate()} or
 * otherwise there will be an exception. Instance maintains a {@link Map} of
 * String tag to {@link WakeLock} wakelock
 */
public class WakeLockManager {
    public static final String EXTRA_WAKELOCK_TAG = "WakeLockManager.EXTRA_WAKELOCK_TAG";
    private static final String TAG = "WakeLockManager";
    private final Logger log;

    private static WakeLockManager sInstance;

    private final Map<String, WakeLock> map;
    private final PowerManager pm;

    public static WakeLockManager getWakeLockManager() {
        if (sInstance == null) throw new RuntimeException(TAG + " was not initialized");
        return sInstance;
    }

    public static void init(Context context, Logger logger, boolean debug) {
        if (sInstance != null) throw new RuntimeException("Attempt to reinitalize a " + TAG);
        sInstance = new WakeLockManager(context, logger, debug);
    }

    private WakeLockManager(Context context, Logger logger, boolean debug) {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        map = new HashMap<String, PowerManager.WakeLock>();
        log = logger;
        if (debug && logger.getLevel(this.getClass()) == null) {
            logger.setLogLevel(getClass(), LogLevel.DEBUG);
        }
    }

    public void acquirePartialWakeLock(String tag) {
        if (!map.containsKey(tag)) {
            final WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
            wl.acquire();
            map.put(tag, wl);
            log.d("Wakelock " + tag + " was acquired");
        } else {
            final WakeLock wl = map.get(tag);
            if (wl.isHeld()) {
                log.d("Wakelock " + tag + " is already held");
            } else
                throw new RuntimeException(
                        "Wakelock is present in the map but is not held. this will be only possible when timeouts are supported");
        }
    }

    public void acquirePartialWakeLock(String tag, int timeInMillis) {
        acquirePartialWakeLock(tag);
        // TODO check time
    }

    /**
     * Acquires a partial {@link WakeLock}, stores it internally and puts the
     * tag into the {@link Intent}. To be used with
     * {@link WakeLockManager#releasePartialWakeLock(Intent)}
     * 
     * @param intent
     * @param wlTag
     */
    public void acquirePartialWakeLock(Intent intent, String wlTag) {
        acquirePartialWakeLock(wlTag);
        intent.putExtra(WakeLockManager.EXTRA_WAKELOCK_TAG, wlTag);
    }

    /**
     * Releases a partial {@link WakeLock} with a given tag
     * 
     * @param tag
     */
    public void releasePartialWakeLock(String tag) {
        if (map.containsKey(tag)) {
            final WakeLock wl = map.get(tag);
            if (wl.isHeld()) {
                wl.release();
                map.remove(tag);
                log.d("Wakelock " + tag + " was released");
            } else {
                map.remove(tag);
                log.d("Wakelock " + tag + " was already released!");
            }

        } else {
            log.w("There is no wakelock " + tag + " known to me");
        }
    }

    /**
     * Releases a partial {@link WakeLock} with a tag contained in the given
     * {@link Intent}
     * 
     * @param intent
     */
    public void releasePartialWakeLock(Intent intent) {
        if (intent.hasExtra(WakeLockManager.EXTRA_WAKELOCK_TAG)) {
            final String wlTag = intent.getStringExtra(WakeLockManager.EXTRA_WAKELOCK_TAG);
            releasePartialWakeLock(wlTag);
        }
    }

}
