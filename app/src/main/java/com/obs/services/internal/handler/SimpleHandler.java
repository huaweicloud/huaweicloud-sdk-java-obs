package com.obs.services.internal.handler;

import java.lang.reflect.Method;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.obs.log.ILogger;
import com.obs.log.LoggerBuilder;

public abstract class SimpleHandler extends DefaultHandler{
	private static final ILogger log = LoggerBuilder.getLogger(SimpleHandler.class);

	protected XMLReader xr = null;
	private StringBuffer textContent = null;
	protected SimpleHandler currentHandler = null;
	protected SimpleHandler parentHandler = null;

	public SimpleHandler(XMLReader xr) {
		this.xr = xr;
		this.textContent = new StringBuffer();
		currentHandler = this;
	}

	public void transferControlToHandler(SimpleHandler toHandler) {
		currentHandler = toHandler;
		toHandler.parentHandler = this;
		xr.setContentHandler(currentHandler);
		xr.setErrorHandler(currentHandler);
	}

	public void returnControlToParentHandler() {
		if (isChildHandler()) {
			parentHandler.currentHandler = parentHandler;
			parentHandler.controlReturned(this);
			currentHandler = parentHandler;
			xr.setContentHandler(currentHandler);
			xr.setErrorHandler(currentHandler);
		} else {
			if(log.isDebugEnabled()) {
				log.debug("Ignoring call to return control to parent handler, as this class has no parent: "
						+ this.getClass().getName());
			}
		}
	}

	public boolean isChildHandler() {
		return parentHandler != null;
	}

	public void controlReturned(SimpleHandler childHandler) {
	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes attrs) {
		try {
			Method method = currentHandler.getClass().getMethod("start" + name, new Class[] {});
			method.invoke(currentHandler, new Object[] {});
		} catch (NoSuchMethodException e) {
			if(log.isDebugEnabled()) {
				log.debug("Skipped non-existent SimpleHandler subclass's startElement method for '" + name + "' in "
						+ this.getClass().getName());
			}
		} catch (Throwable t) {
			if(log.isErrorEnabled()) {
				log.error("Unable to invoke SimpleHandler subclass's startElement method for '" + name + "' in "
						+ this.getClass().getName(), t);
			}
		}
	}

	@Override
	public void endElement(String uri, String name, String qName) {
		String elementText = this.textContent.toString().trim();
		try {
			Method method = currentHandler.getClass().getMethod("end" + name, new Class[] { String.class });
			method.invoke(currentHandler, new Object[] { elementText });
		} catch (NoSuchMethodException e) {
			if(log.isDebugEnabled()) {
				log.debug("Skipped non-existent SimpleHandler subclass's endElement method for '" + name + "' in "
						+ this.getClass().getName());
			}
		} catch (Throwable t) {
			if(log.isErrorEnabled()) {
				log.error("Unable to invoke SimpleHandler subclass's endElement method for '" + name + "' in "
						+ this.getClass().getName(), t);
			}
		}
		this.textContent = new StringBuffer();
	}

	@Override
	public void characters(char ch[], int start, int length) {
		this.textContent.append(ch, start, length);
	}

}
