package no.resheim.eclipse.utils.launcher.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

public class EclipseConfigurationTest {

	@Test
	public void testGetVmArguments() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(this.getClass().getResourceAsStream("eclipse.ini"));
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Dosgi.requiredJavaVersion=1.6", array[0]);
		assertEquals("-XstartOnFirstThread", array[1]);
		assertEquals("-Dorg.eclipse.swt.internal.carbon.smallFonts", array[2]);
		assertEquals("-XX:MaxPermSize=256m", array[3]);
		assertEquals("-Xms40m", array[4]);
		assertEquals("-Xmx512m", array[5]);
		assertEquals("-Xdock:icon=../Resources/Eclipse.icns", array[6]);
		// Last two items should have been removed (no duplicates allowed).
		assertEquals(7, vmArgs.size());
	}

	@Test
	public void testSetDebugParameters() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(this.getClass().getResourceAsStream("eclipse.ini"));
		cr.setRemoteDebug(8000, true);
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xdebug", array[7]);
		assertEquals("-Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y", array[8]);
	}

	@Test
	public void testRemoveVMSetting() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(this.getClass().getResourceAsStream("eclipse.ini"));
		cr.removeVmSetting("-Dorg.eclipse.swt.internal.carbon.smallFonts");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Dosgi.requiredJavaVersion=1.6", array[0]);
		assertEquals("-XstartOnFirstThread", array[1]);
		assertEquals("-XX:MaxPermSize=256m", array[2]);
		assertEquals("-Xms40m", array[3]);
		assertEquals("-Xmx512m", array[4]);
		assertEquals("-Xdock:icon=../Resources/Eclipse.icns", array[5]);
		// Last two items should have been removed (no duplicates allowed).
		assertEquals(6, vmArgs.size());
	}

	@Test
	public void testSetVmXmx() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(this.getClass().getResourceAsStream("eclipse.ini"));
		cr.setVmXmx("1024m");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xmx1024m", array[5]);
	}

	@Test
	public void testSetVmXms() throws IOException {
		EclipseConfiguration cr = new EclipseConfiguration(this.getClass().getResourceAsStream("eclipse.ini"));
		cr.setVmXms("1024m");
		Set<String> vmArgs = cr.getVmArgs();
		String[] array = vmArgs.toArray(new String[0]);
		assertEquals("-Xms1024m", array[4]);
	}
}
