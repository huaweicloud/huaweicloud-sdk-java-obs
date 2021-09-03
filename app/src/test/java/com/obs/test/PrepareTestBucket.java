package com.obs.test;

import com.obs.services.ObsClient;
import com.obs.test.tools.PropertiesTools;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class PrepareTestBucket implements TestRule {
    File configFile = new File("./app/src/test/resource/test_data.properties");
    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                String location = PropertiesTools.getInstance(configFile).getProperties("environment.location");
                String bucketName = description.getMethodName().replace("_", "-").toLowerCase(Locale.ROOT);

                ObsClient obsClient = TestTools.getPipelineEnvironment();
                assertEquals(200, TestTools.createBucket(obsClient, bucketName, location).getStatusCode());
                statement.evaluate();
                assertEquals(204, TestTools.delete_bucket(obsClient, bucketName).getStatusCode());
            }
        };
    }
}
