package net.resheim.eclipse.launcher.core;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.resheim.eclipse.launcher.core.EclipseConfiguration;

/**
 * @since 2.0
 */
@SuppressWarnings("nls")
public class EclipseConfigurationTest {

	private InputStream eclipse_ini;

	@Before
	public void before() throws FileNotFoundException {
		File file = new File("src-test/resources/eclipse44-macosx.ini");
		eclipse_ini = new FileInputStream(file);
	}

	@Test
	public void testGetVmArguments() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(eclipse_ini);
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Dorg.eclipse.swt.internal.carbon.smallFonts", array[0]);
		assertEquals("-Xdock:icon=../Resources/Eclipse.icns", array[1]);
		assertEquals("-Dosgi.requiredJavaVersion=1.7", array[2]);
		assertEquals("-XstartOnFirstThread", array[3]);
		assertEquals("-XX:MaxPermSize=256m", array[4]);
		assertEquals("-Xms40m", array[5]);
		assertEquals("-Xmx512m", array[6]);
		// Last two items should have been removed (no duplicates allowed).
		assertEquals(7, vmArgs.size());
	}

	@Test
	public void testSetDebugParameters() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(eclipse_ini);
		cr.setRemoteDebug(8000, true);
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xdebug", array[7]);
		assertEquals("-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y", array[8]);
	}

	/**
	 * Removes the smallFonts option and verifies that the ini-file is still in
	 * working order.
	 *
	 * @throws IOException
	 */
	@Test
	public void testRemoveVMSetting() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(eclipse_ini);
		cr.removeVmSetting("-Dorg.eclipse.swt.internal.carbon.smallFonts");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xdock:icon=../Resources/Eclipse.icns", array[0]);
		assertEquals("-Dosgi.requiredJavaVersion=1.7", array[1]);
		assertEquals("-XstartOnFirstThread", array[2]);
		assertEquals("-XX:MaxPermSize=256m", array[3]);
		assertEquals("-Xms40m", array[4]);
		assertEquals("-Xmx512m", array[5]);
		// Last two items should have been removed (no duplicates allowed).
		assertEquals(6, vmArgs.size());
	}

	@Test
	public void testSetVmXmx() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(eclipse_ini);
		cr.setVmXmx("1024m");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xmx1024m", array[6]);
	}

	@Test
	public void testSetVmXms() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(eclipse_ini);
		cr.setVmXms("1024m");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xms1024m", array[5]);
	}
}
