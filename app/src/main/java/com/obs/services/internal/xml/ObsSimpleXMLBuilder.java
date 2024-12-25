package com.obs.services.internal.xml;

public class ObsSimpleXMLBuilder {
    private final StringBuilder xmlBuilder = new StringBuilder();

    public void startElement(String tag) {
        xmlBuilder.append("<").append(tag).append(">");
    }

    public void append(String value) {
        xmlBuilder.append(value);
    }
    public void append(int value) {
        xmlBuilder.append(value);
    }

    public void endElement(String tag) {
        xmlBuilder.append("</").append(tag).append(">");
    }

    public StringBuilder getXmlBuilder() {
        return xmlBuilder;
    }
}
