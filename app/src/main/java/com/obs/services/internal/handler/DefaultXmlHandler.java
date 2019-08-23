/**
 * 
 * JetS3t : Java S3 Toolkit
 * Project hosted at http://bitbucket.org/jmurty/jets3t/
 *
 * Copyright 2006-2010 James Murty
 * 
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
package com.obs.services.internal.handler;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public abstract class DefaultXmlHandler extends DefaultHandler{

	private StringBuilder currText = null;
	private final LinkedList<String> context = new LinkedList<String>();

	public void startDocument() {
	}

	public void endDocument() {
	}

	public void startElement(String uri, String name, String qName, Attributes attrs) {
		this.currText = new StringBuilder();
		this.startElement(name, attrs);
	}

	public void startElement(String name, Attributes attrs) {
		this.startElement(name);
	}

	public void startElement(String name) {
		context.add(name);
	}

	public void endElement(String uri, String name, String qName) {
		String elementText = this.currText.toString();
		this.endElement(name, elementText);
	}

	public void endElement(String name, String content) {
		context.removeLast();
	}

	public void characters(char ch[], int start, int length) {
		this.currText.append(ch, start, length);
	}
}
