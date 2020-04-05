## Changes

### Version 3.0.2 (2020-04-26)

* Added support to use a path to the `libjli.dylib` as `-vm` argument. In macOS the only way to use a JDK downloaded 
  with SDKMAN (or any other JDK that does not have the macOS directory layout) is specifying the path to the 
  libjli.dylib file, like `-vm /<PATH_TO_YOUR_JDK>/jre/lib/jli/libjli.dylib`. 

### Version 3.0.1 (2018-08-05)
* Fixed the "Advanced" dialog which was broken under Eclipse Photon.

### Version 3.0.0 (2015-09-27)
* Spring cleaning:
  * Removed the OS X Quick Look feature that was introduced in 1.3.0. It was not very popular and to put it frankly; not very useful.
  * Renamed all bundles from "no.resheim.eclipse.utils.*" to "net.resheim.eclipse.launcher" version numbering will now reflect the version of the launcher feature, Hence the jump from 1.4.0 to 3.0.0. This also means that the feature "no.resheim.eclipse.utils.launcher" cannot be upgraded, one must uninstall this and install "net.resheim.eclipse.launcher" instead. 
  * Travis-CI infrastructure is set up for the project. This enables continous building and makes it easier for contributors.
  * Updated branding and EULA.
* Added "-clean" feature to the "Advanced" dialog for clearing OSGi and runtime caches.

### Version 1.4.0 (2014-11-13)
* Various housekeeping: Set up Mylyn configuration for each project. Fixed broken test running from Tycho. Added Eclipse-SourceReferences in Manifest Headers. Set OS-filter in features.xml. Added license text etc.
* Added "Advanced" menu entry in for the "Eclipse Launcher" which is now at version 2.0.0. This opens a dialog with additional options for opening a new instance of Eclipse.
  * Which JRE to use, a list of detected runtimes are displayed.
  * Maximum and minimum memory to allocate for heap space.
  * Remote debugging enablement and port number.
  * Whether or not to reduce font size (default is on).

### Version 1.3.0 (2013-04-06)
* Added a new command that activates the [OS X Quick Look](http://www.apple.com/findouthow/mac/#quicklook) feature on a selected file when **Command+Y** is pressed. This is installed as a separate Eclipse feature.

### Version 1.2.3 (2013-03-24)
* Fixed a bug in the launcher which would cause the JVM path to be wrong on Eclipse 4.3. Eclipse 4.2 and older would use <code>JAVA_HOME</code> or equivalent while Eclipse 4.3 will need the absolute path to the Java executable.

### Version 1.2.2 (2012-11-13)
* Fixed a bug in the launcher which would cause the default JVM (<code>/System/Library/Frameworks/JavaVM.framework</code>) to always be used when a new instance was started. This mechanism is a relic from the days Apple were maintaining their own builds of Java; and could lead to a undesired version being picked up. See ["Juggeling multiple Java versions on OS X"](http://java.dzone.com/articles/juggling-multiple-versions) for a bit more information. When opening a new Eclipse instance this utility will now attempt to use the same JVM as the original.

### Version 1.2.1 (2012-10-19)
* Removed dependency on Java 1.7 - the utility will now also work if Java 1.6 is used.
* Removed dependency on Eclipse Juno (4.2) - Indigo (3.7) can now also be used.
* Fixed a bug that would sometimes cause the workspace selection dialog to pop up even if a workspace had been selected in the menu.

### Version 1.2.0 (2012-10-09)
* Eclipse can be started even if the path to it includes whitespace characters.
* Improved error logging when Eclipse could not be started.

### Version 1.1.0 (2012-08-20)
* Badge text is set to the last segment of the workspace path unless a name has been specified in preferences.

### Version 1.0.0 (2012-08-07)
* Badge text can be set using workspace preferences.
* **Open Workspace** command available from file menu.
  * Lists all known workspaces
  * **Other…** menu item brings up the selection dialog.