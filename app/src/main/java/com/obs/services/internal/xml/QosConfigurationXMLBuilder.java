package com.obs.services.internal.xml;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.model.Qos.QosConfiguration;

import java.util.Optional;

public class QosConfigurationXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    public String buildXML(QosConfiguration qosConfiguration) throws ObsException {
        checkQosConfiguration(qosConfiguration);
        startElement("PutBucketQoSRequestBody");
        startElement("QoSConfiguration");
        qosConfiguration.getRules().forEach(rule -> {
            startElement("QoSRule");
            Optional.ofNullable(rule.getNetworkType())
                    .ifPresent(nt -> {
                        startElement("NetworkType");
                        append(nt.getCode());
                        endElement("NetworkType");
                    });
            Optional.of(rule.getConcurrentRequestLimit())
                    .ifPresent(crl -> {
                        startElement("ConcurrentRequestLimit");
                        append(String.valueOf(crl));
                        endElement("ConcurrentRequestLimit");
                    });
            Optional.ofNullable(rule.getQpsLimit())
                    .ifPresent(qpsLimit -> {
                        startElement("QpsLimit");
                        Optional.of(qpsLimit.getQpsGetLimit())
                                .ifPresent(getQps -> {
                                    startElement("Get");
                                    append(String.valueOf(getQps));
                                    endElement("Get");
                                });
                        Optional.of(qpsLimit.getQpsPutPostDeleteLimit())
                                .ifPresent(putPostDeleteQps -> {
                                    startElement("PutPostDelete");
                                    append(String.valueOf(putPostDeleteQps));
                                    endElement("PutPostDelete");
                                });
                        Optional.of(qpsLimit.getQpsListLimit())
                                .ifPresent(listQps -> {
                                    startElement("List");
                                    append(String.valueOf(listQps));
                                    endElement("List");
                                });
                        Optional.of(qpsLimit.getQpsTotalLimit())
                                .ifPresent(totalQps -> {
                                    startElement("Total");
                                    append(String.valueOf(totalQps));
                                    endElement("Total");
                                });
                        endElement("QpsLimit");
                    });
            Optional.ofNullable(rule.getBpsLimit())
                    .ifPresent(bpsLimit -> {
                        startElement("BpsLimit");
                        Optional.of(bpsLimit.getBpsGetLimit())
                                .ifPresent(getBps -> {
                                    startElement("Get");
                                    append(String.valueOf(getBps));
                                    endElement("Get");
                                });
                        Optional.of(bpsLimit.getBpsPutPostLimit())
                                .ifPresent(putPostBps -> {
                                    startElement("PutPost");
                                    append(String.valueOf(putPostBps));
                                    endElement("PutPost");
                                });
                        Optional.of(bpsLimit.getBpsTotalLimit())
                                .ifPresent(totalBps -> {
                                    startElement("Total");
                                    append(String.valueOf(totalBps));
                                    endElement("Total");
                                });
                        endElement("BpsLimit");
                    });
            endElement("QoSRule");
        });
        endElement("QoSConfiguration");
        endElement("PutBucketQoSRequestBody");
        return getXmlBuilder().toString();
    }

    protected void checkQosConfiguration(QosConfiguration qosConfiguration) {
        if (qosConfiguration == null) {
            String errorMessage = "qosConfiguration is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        } else if (qosConfiguration.getRules() == null || qosConfiguration.getRules().isEmpty()) {
            String errorMessage = "qosConfiguration's QosRuleList is null or empty, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }
}