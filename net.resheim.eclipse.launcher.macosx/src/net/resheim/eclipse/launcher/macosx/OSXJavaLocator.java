/*******************************************************************************
 * Copyright (c) 2014-2019 Torkild U. Resheim.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/

package net.resheim.eclipse.launcher.macosx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import net.resheim.eclipse.launcher.core.IJavaLocatorService;
import net.resheim.eclipse.launcher.core.JRE;

/**
 * This type makes use of the OS X specific <i>/usr/libexec/java_home</i> to
 * locate all Java Runtime Environments installed and registered on the host
 * computer.
 *
 * @author Torkild U. Resheim
 * @since 2.0
 */
public class OSXJavaLocator extends DefaultHandler implements IJavaLocatorService {

	/** A list of plist keys used by /usr/libexec/java_home */
	private enum KEY {
		JVMArch, JVMBlacklisted, JVMBundleID, JVMEnabled, JVMHomePath, JVMIsBuiltIn, JVMName, JVMPlatformVersion, JVMVendor, JVMVersion
	}

	private static final String KEY_ELEMENT = "key"; //$NON-NLS-1$

	private static final String STRING_ELEMENT = "string"; //$NON-NLS-1$

	private JRE currentJRE;

	private KEY currentKey;

	private final List<JRE> runtimes;

	private final StringBuilder sb;

	public OSXJavaLocator() {
		super();
		runtimes = new ArrayList<JRE>();
		sb = new StringBuilder();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		sb.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// Indicates the end of an JVM declaration
		if ("dict".equals(qName) && currentJRE != null) { //$NON-NLS-1$
			runtimes.add(currentJRE);
			currentJRE = null;
		}
		if (KEY_ELEMENT.equals(qName)) {
			currentKey = KEY.valueOf(sb.toString());
		}
		if (STRING_ELEMENT.equals(qName) && currentKey != null) {
			// See if we have a useful key
			readElement();
		}
	}

	private void readElement() {
		switch (currentKey) {
		case JVMHomePath:
			currentJRE.setPath(sb.toString());
			break;
		case JVMName:
			currentJRE.setName(sb.toString());
			break;
		case JVMVersion:
			currentJRE.setVersion(sb.toString());
			break;
		case JVMArch:
			currentJRE.setArch(sb.toString());
			break;
		case JVMVendor:
			currentJRE.setVendor(sb.toString());
			break;
		default:
			break;
		}
		currentKey = null;
	}

	public List<JRE> getRuntimes() {
		if (runtimes.isEmpty()) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				factory.setValidating(false);
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); //$NON-NLS-1$
				SAXParser saxParser = factory.newSAXParser();

				Runtime rt = Runtime.getRuntime();
				String[] commands = { "/usr/libexec/java_home", "-X" }; //$NON-NLS-1$ //$NON-NLS-2$

				Process proc = rt.exec(commands, null, null);
				saxParser.parse(proc.getInputStream(), this);
			} catch (SAXException | IOException | ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
		return runtimes;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		// Indicates the start of a new JVM declaration
		if ("dict".equals(qName)) { //$NON-NLS-1$
			currentJRE = new JRE();
		}
		// Clear the character buffer
		sb.setLength(0);
	}
}
