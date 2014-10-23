package no.resheim.eclipse.utils.launcher.macosx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import no.resheim.eclipse.utils.launcher.core.JRE;

import org.junit.Before;
import org.junit.Test;

/**
 * @since 2.0
 */
@SuppressWarnings("nls")
public class OSXJavaLocatorTest {

	private String root = "";

	@Before
	public void before() {
		String property = System.getProperty("testResourceRoot");
		if (null != property) {
			root = property;
		}
	}

	@Test
	public void testStreamToString() {
		assertNotNull("Test file missing", getClass().getResource(root + "java_home_x.txt"));
	}

	@Test
	public void testParser() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();

		OSXJavaLocator locator = new OSXJavaLocator();

		saxParser.parse(getClass().getResourceAsStream(root + "java_home_x.txt"), locator);
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
