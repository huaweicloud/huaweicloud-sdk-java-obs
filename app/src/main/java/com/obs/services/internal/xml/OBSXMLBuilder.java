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
package com.obs.services.internal.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class OBSXMLBuilder {
    private static final String DEFAULT_PACKAGE = "com.sun.org.apache.xerces.internal";

    private static String xmlDocumentBuilderFactoryClass =
            "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";

    private Document xmlDocument;
    private Node xmlNode;

    public static void setXmlDocumentBuilderFactoryClass(String className) {
        if (null != className && !className.trim().equals("")) {
            xmlDocumentBuilderFactoryClass = className;
        }
    }

    protected OBSXMLBuilder(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
        this.xmlNode = xmlDocument.getDocumentElement();
    }

    protected OBSXMLBuilder(Node myNode, Node parentNode) {
        this.xmlNode = myNode;
        if (myNode instanceof Document) {
            this.xmlDocument = (Document) myNode;
        } else {
            this.xmlDocument = myNode.getOwnerDocument();
        }

        if (parentNode != null) {
            parentNode.appendChild(myNode);
        }
    }

    private static DocumentBuilderFactory findDocumentBuilderFactory() {
        if (xmlDocumentBuilderFactoryClass != null && xmlDocumentBuilderFactoryClass.startsWith(DEFAULT_PACKAGE)) {
            return DocumentBuilderFactory.newInstance();
        }

        return newInstance(DocumentBuilderFactory.class, xmlDocumentBuilderFactoryClass, null, true, false);
    }

    protected static Document createDocumentImpl(
            String name, String namespaceURI, boolean isNamespaceAware)
            throws ParserConfigurationException, FactoryConfigurationError {
        DocumentBuilderFactory factory = OBSXMLBuilder.findDocumentBuilderFactory();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setNamespaceAware(isNamespaceAware);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement;
        if (namespaceURI != null && namespaceURI.length() > 0) {
            rootElement = document.createElementNS(namespaceURI, name);
        } else {
            rootElement = document.createElement(name);
        }
        document.appendChild(rootElement);
        return document;
    }

    protected static Document parseDocumentImpl(
            InputSource inputSource, boolean isNamespaceAware)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = OBSXMLBuilder.findDocumentBuilderFactory();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setNamespaceAware(isNamespaceAware);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputSource);
        return document;
    }

    private static ClassLoader getContextClassLoader() throws SecurityException {
        return (ClassLoader)
                AccessController.doPrivileged(
                        (PrivilegedAction<Object>)
                                () -> {
                                    ClassLoader cl = Thread.currentThread().getContextClassLoader();

                                    if (cl == null) {
                                        cl = ClassLoader.getSystemClassLoader();
                                    }
                                    return cl;
                                });
    }

    private static Class<?> getProviderClass(
            String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader)
            throws ClassNotFoundException {
        try {
            if (cl == null) {
                if (useBSClsLoader) {
                    cl = OBSXMLBuilder.class.getClassLoader();
                } else {
                    cl = getContextClassLoader();
                    if (cl == null) {
                        throw new ClassNotFoundException();
                    }
                }
            }

            return Class.forName(className, false, cl);
        } catch (ClassNotFoundException e1) {
            if (doFallback) {
                return Class.forName(className, false, OBSXMLBuilder.class.getClassLoader());
            } else {
                throw e1;
            }
        }
    }

    private static <T> T newInstance(
            Class<T> type, String className, ClassLoader cl, boolean doFallback, boolean useBSClsLoader)
            throws FactoryConfigurationError {
        if (System.getSecurityManager() != null) {
            if (className != null && className.startsWith(DEFAULT_PACKAGE)) {
                cl = null;
                useBSClsLoader = true;
            }
        }

        try {
            Class<?> providerClass = getProviderClass(className, cl, doFallback, useBSClsLoader);
            if (!type.isAssignableFrom(providerClass)) {
                throw new ClassCastException(className + " cannot be cast to " + type.getName());
            }
            Object instance = providerClass.newInstance();
            return type.cast(instance);
        } catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError(x, "Provider " + className + " not found");
        } catch (Exception x) {
            throw new FactoryConfigurationError(x, "Provider " + className + " could not be instantiated: " + x);
        }
    }

    public static OBSXMLBuilder create(String name) throws ParserConfigurationException, FactoryConfigurationError {
        return new OBSXMLBuilder(createDocumentImpl(name, null, true));
    }

    public static OBSXMLBuilder parse(InputSource inputSource, boolean isNamespaceAware)
            throws ParserConfigurationException, SAXException, IOException {
        return new OBSXMLBuilder(parseDocumentImpl(inputSource, isNamespaceAware));
    }

    public static OBSXMLBuilder parse(InputSource inputSource)
            throws ParserConfigurationException, SAXException, IOException {
        return OBSXMLBuilder.parse(inputSource, true);
    }

    public OBSXMLBuilder importXMLBuilder(OBSXMLBuilder builder) {
        Node importedNode = this.getDocument().importNode(builder.getDocument().getDocumentElement(), true);
        this.xmlNode.appendChild(importedNode);
        return this;
    }

    public OBSXMLBuilder element(String name) {
        String namespaceURI = lookupNamespaceURI(name);
        return element(name, namespaceURI);
    }

    public OBSXMLBuilder elem(String name) {
        return element(name);
    }

    public OBSXMLBuilder e(String name) {
        return element(name);
    }

    public OBSXMLBuilder element(String name, String namespaceURI) {
        Element elem = elementImpl(name, namespaceURI);
        return new OBSXMLBuilder(elem, this.getElement());
    }

    public Element elementImpl(String name, String namespaceURI) {
        return namespaceURI == null
                ? this.getDocument().createElement(name)
                : this.getDocument().createElementNS(namespaceURI, name);
    }

    public OBSXMLBuilder attribute(String name, String value) {
        attributeImpl(name, value);
        return this;
    }

    public void textImpl(String value, boolean replaceText) {
        if (value == null) {
            throw new IllegalArgumentException("Illegal null text value");
        } else {
            if (replaceText) {
                this.xmlNode.setTextContent(value);
            } else {
                this.xmlNode.appendChild(this.getDocument().createTextNode(value));
            }
        }
    }

    public void attributeImpl(String name, String value) {
        if (!(this.xmlNode instanceof Element)) {
            throw new RuntimeException("Cannot add an attribute to non-Element underlying node: " + this.xmlNode);
        } else {
            ((Element) this.xmlNode).setAttribute(name, value);
        }
    }

    public OBSXMLBuilder attr(String name, String value) {
        return attribute(name, value);
    }

    public OBSXMLBuilder text(String value, boolean replaceText) {
        textImpl(value, replaceText);
        return this;
    }

    public OBSXMLBuilder text(String value) {
        return this.text(value, false);
    }

    public OBSXMLBuilder t(String value) {
        return text(value);
    }

    protected Node upImpl(int steps) {
        Node currNode = this.xmlNode;

        for (int stepCount = 0; currNode.getParentNode() != null && stepCount < steps; ++stepCount) {
            currNode = currNode.getParentNode();
        }

        return currNode;
    }

    public OBSXMLBuilder up(int steps) {
        Node currNode = upImpl(steps);
        if (currNode instanceof Document) {
            return new OBSXMLBuilder((Document) currNode);
        } else {
            return new OBSXMLBuilder(currNode, null);
        }
    }

    public OBSXMLBuilder up() {
        return up(1);
    }

    public Element getElement() {
        return this.xmlNode instanceof Element ? (Element) this.xmlNode : null;
    }

    protected String lookupNamespaceURI(String name) {
        String prefix = this.getPrefixFromQualifiedName(name);
        return this.xmlNode.lookupNamespaceURI(prefix);
    }

    protected String getPrefixFromQualifiedName(String qualifiedName) {
        int colonPos = qualifiedName.indexOf(58);
        return colonPos > 0 ? qualifiedName.substring(0, colonPos) : null;
    }

    public Document getDocument() {
        return this.xmlDocument;
    }

    public String asString() throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(this.getDocument()), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("|\r", "");
    }

    public int hashCode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public boolean equals(Object obj) {
        return obj == this;
    }
}
