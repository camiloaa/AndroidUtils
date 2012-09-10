package com.better.wakelock;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * Utility class to pass {@link WakeLock} objects with intents. It contains a
 * factory, which has to be initialized in {@link Application#onCreate()} or
 * otherwise there will be an exception. Instance maintains a {@link Map} of
 * String tag to {@link WakeLock} wakelock
 */
public class WakeLockManager {
    public static final String EXTRA_WAKELOCK_TAG = "WakeLockManager.EXTRA_WAKELOCK_TAG";
    private static final String TAG = "WakeLockManager";
    private boolean debug;

    private static WakeLockManager sInstance;

    private Map<String, WakeLock> map;
    private PowerManager pm;

    public static WakeLockManager getWakeLockManager() {
        if (sInstance == null) throw new RuntimeException(TAG + " was not initialized");
        return sInstance;
    }

    public static void init(Context context, boolean debug) {
        if (sInstance != null) throw new RuntimeException("Attempt to reinitalize a " + TAG);
        sInstance = new WakeLockManager(context, debug);
    }

    private WakeLockManager(Context context, boolean debug) {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        map = new HashMap<String, PowerManager.WakeLock>();
        this.debug = debug;
    }

    public void acquirePartialWakeLock(String tag) {
        if (!map.containsKey(tag)) {
            WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, tag);
            wl.acquire();
            map.put(tag, wl);
            if (debug) Log.d(TAG, "Wakelock " + tag + " was acquired");
        } else {
            WakeLock wl = map.get(tag);
            if (wl.isHeld()) {
                Log.w(TAG, "Wakelock " + tag + " is already held");
            } else {
                throw new RuntimeException(
                        "Wakelock is present in the map but is not held. this will be only possible when timeouts are supported");
            }
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
            WakeLock wl = map.get(tag);
            if (wl.isHeld()) {
                wl.release();
                map.remove(tag);
                if (debug) Log.d(TAG, "Wakelock " + tag + " was released");
            } else {
                map.remove(tag);
                Log.w(TAG, "Wakelock " + tag + " was already released!");
            }

        } else {
            Log.w(TAG, "There is no wakelock " + tag + " known to me");
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
            String wlTag = intent.getStringExtra(WakeLockManager.EXTRA_WAKELOCK_TAG);
            releasePartialWakeLock(wlTag);
        }
    }

}
