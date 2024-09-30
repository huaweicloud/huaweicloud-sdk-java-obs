package com.obs.services.internal.xml;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;
import com.obs.services.internal.utils.ServiceUtils;
import com.obs.services.model.CompleteMultipartUploadRequest;
import com.obs.services.model.PartEtag;

import java.util.List;

public class CompleteMultipartUploadXMLBuilder extends ObsSimpleXMLBuilder {
    private static final ILogger log = LoggerBuilder.getLogger("com.obs.services.ObsClient");

    public static void sortPartETags(List<PartEtag> partEtagList) {
        if (partEtagList == null) {
            log.error("partEtagList is null! sort canceled.");
            return;
        }
        partEtagList.sort(
                (o1, o2) -> {
                    if (o1 == o2) {
                        return 0;
                    }
                    if (o1 == null) {
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    return o1.getPartNumber().compareTo(o2.getPartNumber());
                });
    }

    public String buildXML(CompleteMultipartUploadRequest request) {
        CompleteMultipartUploadXMLBuilder.sortPartETags(request.getPartEtag());
        startElement("CompleteMultipartUpload");
        if (request.getPartEtag() == null) {
            log.error("CompleteMultipartUploadRequest.getPartEtag() is null.");
        } else if (request.getPartEtag().isEmpty()) {
            log.error("CompleteMultipartUploadRequest.getPartEtag() isEmpty.");
        } else {
            Integer partNumber;
            for (int i = 0; i < request.getPartEtag().size(); i++) {
                PartEtag etag = request.getPartEtag().get(i);
                if (etag == null) {
                    log.error("CompleteMultipartUploadRequest.getPartEtag().get(" +
                            i + ") is null when buildXML for CompleteMultipartUpload!");
                    continue;
                }
                startElement("Part");
                startElement("PartNumber");
                partNumber = etag.getPartNumber();
                append(partNumber == null ? "" : partNumber.toString());
                endElement("PartNumber");
                startElement("ETag");
                append(ServiceUtils.toValid(etag.getEtag()));
                endElement("ETag");
                endElement("Part");
            }
        }
        endElement("CompleteMultipartUpload");
        return getXmlBuilder().toString();
    }
}
