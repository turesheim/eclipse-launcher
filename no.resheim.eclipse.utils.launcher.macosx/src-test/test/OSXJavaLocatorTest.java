package test;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import no.resheim.eclipse.utils.launcher.core.JRE;
import no.resheim.eclipse.utils.launcher.macosx.OSXJavaLocator;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @since 2.0
 */
public class OSXJavaLocatorTest {

	@SuppressWarnings("nls")
	@Test
	public void testParser() throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();
		OSXJavaLocator locator = new OSXJavaLocator();
		saxParser.parse(this.getClass().getResourceAsStream("java_home_x.txt"), locator);
		List<JRE> runtimes = locator.getRuntimes();
		assertEquals(2, runtimes.size());
		assertEquals("/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home", runtimes.get(0).getPath());
		assertEquals("x86_64", runtimes.get(0).getArch());
		assertEquals("Java SE 8", runtimes.get(0).getName());
		assertEquals("1.8.0_20", runtimes.get(0).getVersion());
		assertEquals("/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home", runtimes.get(1).getPath());
		assertEquals("x86_64", runtimes.get(1).getArch());
		assertEquals("Java SE 7", runtimes.get(1).getName());
		assertEquals("1.7.0_55", runtimes.get(1).getVersion());
	}
}
