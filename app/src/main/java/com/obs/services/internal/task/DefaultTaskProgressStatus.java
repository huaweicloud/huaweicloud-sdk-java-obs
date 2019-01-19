package com.obs.services.internal.task;

import java.util.concurrent.atomic.AtomicInteger;

import com.obs.services.model.TaskProgressStatus;

public class DefaultTaskProgressStatus implements TaskProgressStatus {

    private AtomicInteger execTaskNum = new AtomicInteger();
    private AtomicInteger succeedTaskNum = new AtomicInteger();
    private AtomicInteger failTaskNum = new AtomicInteger();
    private AtomicInteger totalTaskNum = new AtomicInteger();

    public DefaultTaskProgressStatus() {
    }

    public void execTaskIncrement() {
        execTaskNum.incrementAndGet();
    }

    public void succeedTaskIncrement() {
        succeedTaskNum.incrementAndGet();
    }

    public void failTaskIncrement() {
        failTaskNum.incrementAndGet();
    }

    public void setTotalTaskNum(int totalNum) {
        this.totalTaskNum.set(totalNum);
    }

    @Override
    public int getExecPercentage() {
        if (totalTaskNum.get() <= 0) {
            return -1;
        } else {
            return execTaskNum.get() * 100 / totalTaskNum.get();
        }
    }

    @Override
    public int getTotalTaskNum() {
        return totalTaskNum.get();
    }

    @Override
    public int getExecTaskNum() {
        return execTaskNum.get();
    }

    @Override
    public int getSucceedTaskNum() {
        return succeedTaskNum.get();
    }

    @Override
    public int getFailTaskNum() {
        return failTaskNum.get();
    }

}
