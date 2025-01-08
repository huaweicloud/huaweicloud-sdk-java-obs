package com.obs.services;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.security.BasicSecurityKey;
import com.obs.services.model.ISecurityKey;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class RefreshAkSkStsTokenProvider implements IObsCredentialsProvider {

    private static final ILogger log = LoggerBuilder.getLogger(RefreshAkSkStsTokenProvider.class);

    private ISecurityKey securityKey;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final long REFRESH_INTERVAL_MINUTES = 10;
    private final OkHttpClient httpClient;
    private String iamDomain;
    private String iamUser;
    private String iamPassword;
    private String iamEndpoint;
    private boolean refreshTaskStarted = false;

    private final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();

    public RefreshAkSkStsTokenProvider(String iamDomain, String iamUser, String iamPassword, String iamEndpoint, OkHttpClient httpClient) {
        this.iamDomain = iamDomain;
        this.iamUser = iamUser;
        this.iamPassword = iamPassword;
        this.iamEndpoint = iamEndpoint;
        this.httpClient = httpClient != null ? httpClient : new OkHttpClient();
    }

    public void startTokenRefreshTask() {
        if (!refreshTaskStarted) {
            scheduler.scheduleAtFixedRate(this::refreshSecurityKey, 0, REFRESH_INTERVAL_MINUTES, TimeUnit.MINUTES);
            refreshTaskStarted = true;
        }
    }

    @Override
    public void setSecurityKey(ISecurityKey securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public ISecurityKey getSecurityKey() {
        refreshSecurityKey();
        return this.securityKey;
    }

    private void refreshSecurityKey() {
        try {
            ISecurityKey newSecurityKey = fetchAkSkStsToken();
            setSecurityKey(newSecurityKey);
            log.debug("Security key refreshed successfully.");
        } catch (Exception e) {
            log.error("Failed to refresh security key: " + e.getMessage(), e);
        }
    }

    private ISecurityKey fetchAkSkStsToken() throws IOException {
        String subjectToken = getSubjectToken();
        if (subjectToken == null) {
            log.error("Failed to fetch subject token.");
            throw new IOException("Subject token is null.");
        }
        return getSecurityToken(subjectToken);
    }

    private String getSubjectToken() throws IOException {
        String jsonPayload = "{ " +
                    "\"auth\": { " +
                        "\"identity\": { " +
                            "\"methods\": [\"password\"], " +
                            "\"password\": { " +
                                "\"user\": { " +
                                    "\"domain\": { " +
                                         "\"name\": \"" + iamDomain + "\" " +
                                    "}, " +
                                    "\"name\": \"" + iamUser + "\", " +
                                    "\"password\": \"" + iamPassword + "\" " +
                                "} " +
                            "} " +
                        "}, " +
                        "\"scope\": { " +
                            "\"domain\": { " +
                                "\"name\": \"" + iamDomain + "\" " +
                            "} " +
                        "} " +
                    "} " +
                "}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(iamEndpoint + "/v3/auth/tokens").post(body).build();

        Call call = httpClient.newCall(request);
        if (call == null) {
            throw new IllegalStateException("OkHttpClient failed to create a new Call. " +
                    "Ensure httpClient is properly initialized with " + iamEndpoint + "/v3/auth/tokens");
        }

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.header("X-Subject-Token");
        }
    }

    private ISecurityKey getSecurityToken(String subjectToken) throws IOException {
        String jsonPayload = "{ " +
                    "\"auth\": { " +
                        "\"identity\": { " +
                            "\"methods\": [\"token\"], " +
                                "\"token\": { " +
                                    "\"id\": \"" + subjectToken + "\", " +
                                    "\"duration_seconds\": 900 " +
                                "} " +
                            "} " +
                        "} " +
                    "}";
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(iamEndpoint + "/v3.0/OS-CREDENTIAL/securitytokens").post(body).build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }

            String responseBodyString = responseBody.string();

            String newAccessKey = extractJsonField(responseBodyString, "access");
            String newSecretKey = extractJsonField(responseBodyString, "secret");
            String newSecurityToken = extractJsonField(responseBodyString, "securitytoken");

            if (newAccessKey == null || newSecretKey == null || newSecurityToken == null) {
                throw new IllegalStateException("Failed to extract security keys.");
            }

            return new BasicSecurityKey(newAccessKey, newSecretKey, newSecurityToken);
        }
    }


    private String extractJsonField(String json, String field) {
        java.util.regex.Pattern pattern = patternCache.computeIfAbsent(field, key ->
                java.util.regex.Pattern.compile("\"" + field + "\":\"([^\"]+)\"")
        );

        java.util.regex.Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

}

