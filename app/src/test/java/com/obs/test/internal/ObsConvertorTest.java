/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.test.internal;

import org.junit.Assert;
import org.junit.Test;

import com.obs.services.internal.IConvertor;
import com.obs.services.internal.ObsConvertor;
import com.obs.services.model.BucketLoggingConfiguration;
import com.obs.services.model.CanonicalGrantee;
import com.obs.services.model.DeleteDataEnum;
import com.obs.services.model.GrantAndPermission;
import com.obs.services.model.GranteeInterface;
import com.obs.services.model.GroupGrantee;
import com.obs.services.model.HistoricalObjectReplicationEnum;
import com.obs.services.model.Permission;
import com.obs.services.model.ReplicationConfiguration;
import com.obs.services.model.RuleStatusEnum;
import com.obs.services.model.StorageClassEnum;
import com.obs.test.RequestPaymentTest;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObsConvertorTest {
    private static final Logger logger = LogManager.getLogger(ObsConvertorTest.class);
    
    @Test
    public void test_transBucketLoggingConfiguration_1() {
        IConvertor obsConvertor = ObsConvertor.getInstance();
        
        BucketLoggingConfiguration config = new BucketLoggingConfiguration("test_bucket", "object/prefix");
        config.setAgency("log_config_agency");
        
        CanonicalGrantee grantee = new CanonicalGrantee("domain_id_grantee");
        
        GrantAndPermission targetGrants = new GrantAndPermission(grantee, Permission.PERMISSION_READ);
        config.addTargetGrant(targetGrants);
        String result = obsConvertor.transBucketLoggingConfiguration(config);
        
        logger.info(result);
        String assertStr = "<BucketLoggingStatus><Agency>log_config_agency</Agency><LoggingEnabled><TargetBucket>test_bucket</TargetBucket><TargetPrefix>object/prefix</TargetPrefix><TargetGrants><Grant><Grantee><ID>domain_id_grantee</ID></Grantee><Permission>READ</Permission></Grant></TargetGrants></LoggingEnabled></BucketLoggingStatus>";
        
        assertEquals(assertStr, result);
    }
    
    @Test
    public void test_transBucketLoggingConfiguration_2() {
        IConvertor obsConvertor = ObsConvertor.getInstance();
        
        BucketLoggingConfiguration config = new BucketLoggingConfiguration("test_bucket", "object/prefix");
        config.setAgency("log_config_agency");
        
        GrantAndPermission targetGrants = new GrantAndPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
        config.addTargetGrant(targetGrants);
        String result = obsConvertor.transBucketLoggingConfiguration(config);
        
        logger.info(result);
        String assertStr = "<BucketLoggingStatus><Agency>log_config_agency</Agency><LoggingEnabled><TargetBucket>test_bucket</TargetBucket><TargetPrefix>object/prefix</TargetPrefix><TargetGrants><Grant><Grantee><Canned>Everyone</Canned></Grantee><Permission>READ</Permission></Grant></TargetGrants></LoggingEnabled></BucketLoggingStatus>";
        
        assertEquals(assertStr, result);
    }

    /**
     * Test method for transReplicationConfiguration.
     * This test case verifies that a ReplicationConfiguration with DeleteDataEnum ENABLED
     * is correctly converted into an XML string.
     */
    @Test
    public void should_build_xml_correctly_for_bucket_replication_configuration_with_deleteData_enabled() {
        IConvertor obsConvertor = ObsConvertor.getInstance();
        ReplicationConfiguration config = new ReplicationConfiguration();
        config.setAgency("replication_agency");

        ReplicationConfiguration.Rule rule1 = new ReplicationConfiguration.Rule();
        rule1.setId("rule1");
        rule1.setPrefix("prefix1");
        rule1.setStatus(RuleStatusEnum.ENABLED);
        rule1.setHistoricalObjectReplication(HistoricalObjectReplicationEnum.DISABLED);
        ReplicationConfiguration.Destination destination1 = new ReplicationConfiguration.Destination();
        destination1.setBucket("destination_bucket1");
        destination1.setDeleteData(DeleteDataEnum.ENABLED);
        destination1.setObjectStorageClass(StorageClassEnum.STANDARD);
        rule1.setDestination(destination1);
        config.getRules().add(rule1);

        String result = obsConvertor.transReplicationConfiguration(config);

        System.out.println("Generated XML for transReplicationConfiguration: \n" + result);

        String expectedXml = "<ReplicationConfiguration><Agency>replication_agency</Agency><Rule><ID>rule1</ID><Prefix>prefix1</Prefix><Status>Enabled</Status>"
            + "<HistoricalObjectReplication>Disabled</HistoricalObjectReplication>"
            + "<Destination><Bucket>destination_bucket1</Bucket><StorageClass>STANDARD</StorageClass><DeleteData>Enabled</DeleteData></Destination></Rule></ReplicationConfiguration>";

        assertEquals(expectedXml, result);
    }

    /**
     * Test method for transReplicationConfiguration.
     * This test case verifies that a ReplicationConfiguration with DeleteDataEnum DISABLED
     * is correctly converted into an XML string.
     */
    @Test
    public void should_build_xml_correctly_for_bucket_replication_configuration_with_deleteData_disabled() {
        IConvertor obsConvertor = ObsConvertor.getInstance();

        ReplicationConfiguration config = new ReplicationConfiguration();
        config.setAgency("replication_agency");

        ReplicationConfiguration.Rule rule1 = new ReplicationConfiguration.Rule();
        rule1.setId("rule1");
        rule1.setPrefix("prefix1");
        rule1.setStatus(RuleStatusEnum.ENABLED);
        rule1.setHistoricalObjectReplication(HistoricalObjectReplicationEnum.ENABLED);
        ReplicationConfiguration.Destination destination1 = new ReplicationConfiguration.Destination();
        destination1.setBucket("destination_bucket1");
        destination1.setDeleteData(DeleteDataEnum.DISABLED);
        destination1.setObjectStorageClass(StorageClassEnum.STANDARD);
        rule1.setDestination(destination1);
        config.getRules().add(rule1);

        String result = obsConvertor.transReplicationConfiguration(config);

        System.out.println("Generated XML for transReplicationConfiguration: \n" + result);

        String expectedXml = "<ReplicationConfiguration><Agency>replication_agency</Agency><Rule><ID>rule1</ID><Prefix>prefix1</Prefix><Status>Enabled</Status>"
            + "<HistoricalObjectReplication>Enabled</HistoricalObjectReplication>"
            + "<Destination><Bucket>destination_bucket1</Bucket><StorageClass>STANDARD</StorageClass><DeleteData>Disabled</DeleteData></Destination></Rule></ReplicationConfiguration>";

        assertEquals(expectedXml, result);
    }

    @Test
    public void should_transStorageClass_correctly_for_HIGH_PERFORMANCE_OBS() {
        ObsConvertor obsConvertor = new ObsConvertor();
        Assert.assertEquals(obsConvertor.transStorageClass(StorageClassEnum.HIGH_PERFORMANCE),
            StorageClassEnum.HIGH_PERFORMANCE.getCode());
    }
}
