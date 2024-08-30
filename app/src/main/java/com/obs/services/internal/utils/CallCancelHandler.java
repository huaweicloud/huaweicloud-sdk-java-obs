package com.obs.services.internal.utils;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;

import okhttp3.Call;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CallCancelHandler
{
    private final AtomicInteger maxCancelQueueCapacity = new AtomicInteger(0);
    protected static final ILogger log = LoggerBuilder.getLogger(CallCancelHandler.class);
    protected AtomicBoolean isCancelled = new AtomicBoolean(false);

    protected ConcurrentLinkedQueue<Call> calls = new ConcurrentLinkedQueue<>();

    /**
     * cancel
     *
     */
    public void cancel() {
        isCancelled.set(true);
        calls.forEach(
                call -> {
                    if (call != null && !call.isCanceled()) {
                        call.cancel();
                    }
                });
        calls.clear();
    }

    public boolean isCancelled() {
        return isCancelled.get();
    }

    public void setCall(Call call) {
        if (this.isCancelled.get()) {
            String msg = "transport is cancelled";
            if (call == null) {
                msg += ", call is null";
            } else if(call.request() == null) {
                msg += ", call's request is null";
            } else {
                msg += (", url :" + call.request().url());
            }
            log.warn(msg);
            throw new ObsException("transport is cancelled by cancelHandler");
        }
        if (calls.size() >= maxCancelQueueCapacity.get()) {
            log.debug(
                    this.getClass().getName()
                            + "'s calls Capacity is full. cancel may not working! "
                            + "try adjust it by setMaxCallCapacity");
        } else {
            this.calls.add(call);
        }
    }

    public void removeFinishedCall(Call call) {
        calls.remove(call);
    }

    public void resetCancelStatus() {
        calls.clear();
        isCancelled.set(false);
    }

    public int getMaxCallCapacity() {
        return maxCancelQueueCapacity.get();
    }

    public void setMaxCallCapacity(int maxCancelQueueCapacity) {
        this.maxCancelQueueCapacity.set(maxCancelQueueCapacity);
    }
}
