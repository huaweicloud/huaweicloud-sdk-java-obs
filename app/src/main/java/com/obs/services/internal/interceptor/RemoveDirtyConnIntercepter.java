package com.obs.services.internal.interceptor;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.connection.RealConnection;

public class RemoveDirtyConnIntercepter implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Connection connection = chain.connection();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            if (connection instanceof RealConnection) {
                RealConnection realConnection = (RealConnection) connection;
                realConnection.noNewExchanges();
            }
            throw e;
        }
        return response;
    }
}
