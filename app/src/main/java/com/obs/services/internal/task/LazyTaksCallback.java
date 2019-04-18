package com.obs.services.internal.task;

import com.obs.services.exception.ObsException;
import com.obs.services.model.TaskCallback;

public class LazyTaksCallback<K, V> implements TaskCallback<K, V> {
   
    @Override
    public void onSuccess(K result) {
    }

    @Override
    public void onException(ObsException exception, V singleRequest) {
    }
}