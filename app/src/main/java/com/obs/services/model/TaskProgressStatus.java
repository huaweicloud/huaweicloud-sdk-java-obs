package com.obs.services.model;

public interface TaskProgressStatus {
    
    public int getExecPercentage();
    
    public int getExecTaskNum();
    
    public int getSucceedTaskNum();
    
    public int getFailTaskNum();
    
    public int getTotalTaskNum();
}
