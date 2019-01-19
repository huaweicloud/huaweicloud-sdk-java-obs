package com.obs.services.internal.task;

public class BlankTaskProgressStatus extends DefaultTaskProgressStatus {

    @Override
    public void execTaskIncrement() {
    }

    @Override
    public void succeedTaskIncrement() {
    }

    @Override
    public void failTaskIncrement() {
    }

    @Override
    public void setTotalTaskNum(int totalNum) {
    }

    @Override
    public int getExecPercentage() {
        return -1;
    }

    @Override
    public int getTotalTaskNum() {
        return -1;
    }

    @Override
    public int getExecTaskNum() {
        return -1;
    }

    @Override
    public int getSucceedTaskNum() {
        return -1;
    }

    @Override
    public int getFailTaskNum() {
        return -1;
    }

}
