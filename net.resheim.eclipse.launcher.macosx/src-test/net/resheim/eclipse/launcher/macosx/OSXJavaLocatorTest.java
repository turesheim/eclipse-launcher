package net.resheim.eclipse.launcher.macosx;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import net.resheim.eclipse.launcher.core.JRE;

/**
 * @since 2.0
 */
@SuppressWarnings("nls")
public class OSXJavaLocatorTest {

	private final String java_home_x = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
			+ "<plist version=\"1.0\">\n" + "<array>\n" + "	<dict>\n" + "		<key>JVMArch</key>\n"
			+ "		<string>x86_64</string>\n" + "		<key>JVMBlacklisted</key>\n" + "		<false/>\n"
			+ "		<key>JVMBundleID</key>\n" + "		<string>com.oracle.java.8u20.jdk</string>\n"
			+ "		<key>JVMEnabled</key>\n" + "		<true/>\n" + "		<key>JVMHomePath</key>\n"
			+ "		<string>/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home</string>\n"
			+ "		<key>JVMIsBuiltIn</key>\n" + "		<false/>\n" + "		<key>JVMName</key>\n"
			+ "		<string>Java SE 8</string>\n" + "		<key>JVMPlatformVersion</key>\n"
			+ "		<string>1.8</string>\n" + "		<key>JVMVendor</key>\n"
			+ "		<string>Oracle Corporation</string>\n" + "		<key>JVMVersion</key>\n"
			+ "		<string>1.8.0_20</string>\n" + "	</dict>\n" + "	<dict>\n" + "		<key>JVMArch</key>\n"
			+ "		<string>x86_64</string>\n" + "		<key>JVMBlacklisted</key>\n" + "		<false/>\n"
			+ "		<key>JVMBundleID</key>\n" + "		<string>com.oracle.java.7u55.jdk</string>\n"
			+ "		<key>JVMEnabled</key>\n" + "		<true/>\n" + "		<key>JVMHomePath</key>\n"
			+ "		<string>/Library/Java/JavaVirtualMachines/jdk1.7.0_55.jdk/Contents/Home</string>\n"
			+ "		<key>JVMIsBuiltIn</key>\n" + "		<false/>\n" + "		<key>JVMName</key>\n"
			+ "		<string>Java SE 7</string>\n" + "		<key>JVMPlatformVersion</key>\n"
			+ "		<string>1.7</string>\n" + "		<key>JVMVendor</key>\n"
			+ "		<string>Oracle Corporation</string>\n" + "		<key>JVMVersion</key>\n"
			+ "		<string>1.7.0_55</string>\n" + "	</dict>\n" + "</array>\n" + "</plist>\n" + "";

	// XXX: Bad test, this will only work on OSX, mock it.
	// @Test
	// public void testGetRuntimes() {
	// OSXJavaLocator locator = new OSXJavaLocator();
	// List<JRE> runtimes = locator.getRuntimes();
	// assertTrue(runtimes.size() > 0);
	// }

	@Test
	public void testParser() throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		SAXParser saxParser = factory.newSAXParser();

		OSXJavaLocator locator = new OSXJavaLocator();

		saxParser.parse(new ByteArrayInputStream(java_home_x.getBytes()), locator);
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
