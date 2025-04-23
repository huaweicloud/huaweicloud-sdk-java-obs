/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.obs.services.internal.xml;

import static com.obs.services.internal.ObsConstraint.CERTIFICATE;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_CHAIN;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_ID;
import static com.obs.services.internal.ObsConstraint.CUSTOM_DOMAIN_CERTIFICATE_CONFIG;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_NAME;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_PRIVATE_KEY;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.exception.ObsException;
import com.obs.services.model.CustomDomainCertificateConfig;

public class CustomDomainCertificateConfigXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    public String buildXML(CustomDomainCertificateConfig customDomainCertificateConfig) {
        checkCustomDomainCertificateConfig(customDomainCertificateConfig);
        startElement(CUSTOM_DOMAIN_CERTIFICATE_CONFIG);
        startElement(CERTIFICATE_NAME);
        append(customDomainCertificateConfig.getName());
        endElement(CERTIFICATE_NAME);
        if (customDomainCertificateConfig.hasCertificateId()) {
            startElement(CERTIFICATE_ID);
            append(customDomainCertificateConfig.getCertificateId());
            endElement(CERTIFICATE_ID);
        }
        startElement(CERTIFICATE);
        append(customDomainCertificateConfig.getCertificate());
        endElement(CERTIFICATE);
        if (customDomainCertificateConfig.hasCertificateChain()) {
            startElement(CERTIFICATE_CHAIN);
            append(customDomainCertificateConfig.getCertificateChain());
            endElement(CERTIFICATE_CHAIN);
        }
        startElement(CERTIFICATE_PRIVATE_KEY);
        append(customDomainCertificateConfig.getPrivateKey());
        endElement(CERTIFICATE_PRIVATE_KEY);
        endElement(CUSTOM_DOMAIN_CERTIFICATE_CONFIG);
        return getXmlBuilder().toString();
    }

    private void checkCustomDomainCertificateConfig(CustomDomainCertificateConfig customDomainCertificateConfig) {
        if (customDomainCertificateConfig == null) {
            String errorMessage = CUSTOM_DOMAIN_CERTIFICATE_CONFIG + " is null, failed to build request XML!";
            log.error(errorMessage);
            throw new ObsException(errorMessage);
        }
    }
}
