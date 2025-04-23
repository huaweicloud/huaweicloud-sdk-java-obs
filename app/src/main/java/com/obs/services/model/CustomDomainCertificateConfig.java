package com.obs.services.model;

import static com.obs.services.internal.ObsConstraint.CERTIFICATE;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_CHAIN;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_ID;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_NAME;
import static com.obs.services.internal.ObsConstraint.CERTIFICATE_PRIVATE_KEY;
import static com.obs.services.internal.ObsConstraint.CUSTOM_DOMAIN_MAX_SIZE_KB;

import com.obs.services.internal.ObsConstraint;
import com.obs.services.internal.utils.ServiceUtils;

import java.util.Objects;

public class CustomDomainCertificateConfig {

    private String name;

    private String certificateId;

    private String certificate;

    private String certificateChain;

    private String privateKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public boolean hasCertificateChain() {
        return ServiceUtils.isValid(certificateChain);
    }

    public boolean hasCertificateId() {
        return ServiceUtils.isValid(certificateId);
    }

    public static void checkCertificateConfig(CustomDomainCertificateConfig config) throws IllegalArgumentException {
        if (Objects.nonNull(config)) {
            ServiceUtils.assertParameterNotNull(config.getName(), "Certificate name cannot be null");
            ServiceUtils.assertParameterNotNull(config.getCertificate(), "Certificate cannot be null");
            ServiceUtils.assertParameterNotNull(config.getPrivateKey(), "Private key cannot be null");

            ServiceUtils.checkParameterLength(CERTIFICATE_NAME, config.getName(),
                ObsConstraint.CUSTOM_DOMAIN_NAME_MIN_LENGTH, ObsConstraint.CUSTOM_DOMAIN_NAME_MAX_LENGTH);

            if (Objects.nonNull(config.getCertificateId())) {
                ServiceUtils.checkParameterLength(CERTIFICATE_ID, config.getCertificateId(),
                    ObsConstraint.CUSTOM_DOMAIN_CERTIFICATE_ID_MIN_LENGTH, ObsConstraint.CUSTOM_DOMAIN_CERTIFICATE_ID_MAX_LENGTH);
            }

            ServiceUtils.checkParameterSize(CERTIFICATE,
                ServiceUtils.getSizeInKB(config.getCertificate()), CUSTOM_DOMAIN_MAX_SIZE_KB);

            if (Objects.nonNull(config.getCertificateChain())) {
                ServiceUtils.checkParameterSize(CERTIFICATE_CHAIN,
                    ServiceUtils.getSizeInKB(config.getCertificateChain()), CUSTOM_DOMAIN_MAX_SIZE_KB);
            }

            ServiceUtils.checkParameterSize(CERTIFICATE_PRIVATE_KEY,
                ServiceUtils.getSizeInKB(config.getPrivateKey()), CUSTOM_DOMAIN_MAX_SIZE_KB);
        }
    }
}
