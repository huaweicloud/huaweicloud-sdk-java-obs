/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
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
package com.obs.services.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.jamesmurty.utils.XMLBuilder;
import com.obs.services.internal.utils.ServiceUtils;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = -757626557833455141L;

    private String xmlMessage ;

    // Fields from error messages.
    private String errorCode;
    private String errorMessage;
    private String errorRequestId;
    private String errorHostId;

    // Map<String, String> - name => value pairs of response headers.
    private Map<String, String> responseHeaders;

    private int responseCode = -1;
    private String responseStatus;
    private String responseDate;
    private String requestVerb;
    private String requestPath;
    private String requestHost;
    private String errorIndicator;

    public ServiceException(String message, String xmlMessage) {
        this(message, xmlMessage, null);
    }

    public ServiceException(String message, String xmlMessage, Throwable cause) {
        super(message, cause);
        if (ServiceUtils.isValid(xmlMessage)) {
            parseXmlMessage(xmlMessage);
        }
    }

    public ServiceException() {
        super();
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    @Override
    public String toString() {
        String myString = super.toString();

        // Add request-specific information, if it's available.
        if (requestVerb != null) {
            myString +=
                " " + requestVerb
                + " '" + requestPath + "'"
                + (requestHost != null ? " on Host '" + requestHost + "'" : "")
                + (responseDate != null ? " @ '" + responseDate + "'" : "");
        }
        if (responseCode != -1) {
            myString +=
                " -- ResponseCode: " + responseCode
                + ", ResponseStatus: " + responseStatus;
        }
        if (isParsedFromXmlMessage()) {
            myString += ", XML Error Message: " + xmlMessage;
        }  else {
            if (errorRequestId != null) {
                myString += ", RequestId: " + errorRequestId
                    + ", HostId: " + errorHostId;
            }
        }
        return myString;
    }

    private String findXmlElementText(String xmlMessage, String elementName) {
        Pattern pattern = Pattern.compile(".*<" + elementName + ">(.*)</" + elementName + ">.*");
        Matcher matcher = pattern.matcher(xmlMessage);
        if (matcher.matches() && matcher.groupCount() == 1) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    private void parseXmlMessage(String xmlMessage) {
        xmlMessage = xmlMessage.replaceAll("\n", "");
        this.xmlMessage = xmlMessage;

        this.errorCode = findXmlElementText(xmlMessage, "Code");
        this.errorMessage = findXmlElementText(xmlMessage, "Message");
        this.errorRequestId = findXmlElementText(xmlMessage, "RequestId");
        this.errorHostId = findXmlElementText(xmlMessage, "HostId");

        // Add Details element present in some Google Storage error
        // messages to Message field.
        String errorDetails = findXmlElementText(xmlMessage, "Details");
        if (errorDetails != null && errorDetails.length() > 0) {
            this.errorMessage += " " + errorDetails;
        }
    }
    

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String code) {
        this.errorCode = code;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage= message;
    }

    public String getErrorHostId() {
        return errorHostId;
    }

    public void setErrorHostId(String hostId) {
        this.errorHostId = hostId;
    }

    public String getErrorRequestId() {
        return errorRequestId;
    }

    public void setErrorRequestId(String requestId) {
        this.errorRequestId = requestId;
    }

    public String getXmlMessage() {
        return xmlMessage;
    }

    public XMLBuilder getXmlMessageAsBuilder()
        throws IOException, ParserConfigurationException, SAXException
    {
        if (this.xmlMessage == null) {
            return null;
        }
        XMLBuilder builder = XMLBuilder.parse(
            new InputSource(new StringReader(this.xmlMessage)));
        return builder;
    }

    public boolean isParsedFromXmlMessage() {
        return (xmlMessage != null);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    public String getRequestVerb() {
        return requestVerb;
    }

    public void setRequestVerb(String requestVerb) {
        this.requestVerb = requestVerb;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getRequestHost() {
        return requestHost;
    }

    public void setRequestHost(String requestHost) {
        this.requestHost = requestHost;
    }

    public void setRequestAndHostIds(String errorRequestId, String errorHostId) {
        this.errorRequestId = errorRequestId;
        this.errorHostId = errorHostId;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

	public String getErrorIndicator() {
		return errorIndicator;
	}

	public void setErrorIndicator(String errorIndicator) {
		this.errorIndicator = errorIndicator;
	}

}
