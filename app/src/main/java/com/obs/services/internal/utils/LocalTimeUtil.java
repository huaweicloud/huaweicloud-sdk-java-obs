package com.obs.services.internal.utils;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.ServiceException;
import okhttp3.Response;

import java.text.ParseException;
import java.util.Date;

import static com.obs.services.internal.Constants.CommonHeaders.DATE;
import static com.obs.services.internal.Constants.ERROR_CODE_HEADER_AMZ;
import static com.obs.services.internal.Constants.ERROR_CODE_HEADER_OBS;
import static com.obs.services.internal.Constants.REQUEST_TIME_TOO_SKEWED_CODE;

public class LocalTimeUtil
{
    private static volatile long timeDiffInMs = 0L;
    private boolean enableAutoRetryForSkewedTime = true;
    private static final ILogger log = LoggerBuilder.getLogger(LocalTimeUtil.class);

    public static void getTimeDiffBetweenServerAndLocal(Response response) {
        String serverTime = getServerDateFromHeader(response);
        if (serverTime == null) {
            log.error("failed to getTimeDiffBetweenServerAndLocal "
                    + "cause RequestTimeTooSkewed not found in header and response xml is null.");
            return;
        }
        log.info("parsedServerTimeToString:" + serverTime);
        try {
            Date serverTimeDate = ServiceUtils.parseRfc822Date(serverTime);
            long serverTimeDateLong = serverTimeDate.getTime();
            long localTimeDateLong = System.currentTimeMillis();
            setTimeDiffInMs(serverTimeDateLong - localTimeDateLong);
        } catch (ParseException e) {
            log.error("parsedServerTimeToDate Failed:", e);
        }
    }

    public static Date dateWithTimeDiff(Date now) {
        long timeDiff = LocalTimeUtil.timeDiffInMs;
        now.setTime(now.getTime() + timeDiff);
        return now;
    }

    public static long currentTimeMillisWithTimeDiff() {
        return System.currentTimeMillis() + LocalTimeUtil.timeDiffInMs;
    }

    /***
     * Sets the time difference between the server and local time.
     * localTimeInMs + timeDiffInMs = serverTime(correctTime)
     * @param timeDiffInMs
     */
    public static synchronized void setTimeDiffInMs(long timeDiffInMs) {
        LocalTimeUtil.timeDiffInMs = timeDiffInMs;
    }

    public boolean isEnableAutoRetryForSkewedTime() {
        return enableAutoRetryForSkewedTime;
    }

    public void setEnableAutoRetryForSkewedTime(boolean enableAutoRetryForSkewedTime) {
        this.enableAutoRetryForSkewedTime = enableAutoRetryForSkewedTime;
    }

    public static boolean isRequestTimeTooSkewed(ServiceException serviceException, Response response) {
        if (serviceException == null || response == null) {
            return false;
        }
        String errorCode = serviceException.getErrorCode();
        if (errorCode != null) {
            return REQUEST_TIME_TOO_SKEWED_CODE.equals(errorCode);
        }
        return REQUEST_TIME_TOO_SKEWED_CODE.equals(response.header(ERROR_CODE_HEADER_OBS))
                || REQUEST_TIME_TOO_SKEWED_CODE.equals(response.header(ERROR_CODE_HEADER_AMZ));
    }

    public static String getServerDateFromHeader(Response response) {
        if (response == null) {
            return null;
        }
        return response.header(DATE);
    }
}
