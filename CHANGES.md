##Changes

###Version 1.4.0 (xxxx-xx-xx)
* Housekeeping: Set up Mylyn configuration for each project. Fixed broken test running. Added Eclipse-SourceReferences in Manifest Headers. Set OS-filter in features.xml. Added license text.

###Version 1.3.0 (2013-04-06)
* Added a new command that activates the [OS X Quick Look](http://www.apple.com/findouthow/mac/#quicklook) feature on a selected file when **Command+Y** is pressed. This is installed as a separate Eclipse feature.

###Version 1.2.3 (2013-03-24)
* Fixed a bug in the launcher which would cause the JVM path to be wrong on Eclipse 4.3. Eclipse 4.2 and older would use <code>JAVA_HOME</code> or equivalent while Eclipse 4.3 will need the absolute path to the Java executable.

###Version 1.2.2 (2012-11-13)
* Fixed a bug in the launcher which would cause the default JVM (<code>/System/Library/Frameworks/JavaVM.framework</code>) to always be used when a new instance was started. This mechanism is a relic from the days Apple were maintaining their own builds of Java; and could lead to a undesired version being picked up. See ["Juggeling multiple Java versions on OS X"](http://java.dzone.com/articles/juggling-multiple-versions) for a bit more information. When opening a new Eclipse instance this utility will now attempt to use the same JVM as the original.

###Version 1.2.1 (2012-10-19)
* Removed dependency on Java 1.7 - the utility will now also work if Java 1.6 is used.
* Removed dependency on Eclipse Juno (4.2) - Indigo (3.7) can now also be used.
* Fixed a bug that would sometimes cause the workspace selection dialog to pop up even if a workspace had been selected in the menu.

###Version 1.2.0 (2012-10-09)
* Eclipse can be started even if the path to it includes whitespace characters.
* Improved error logging when Eclipse could not be started.

###Version 1.1.0 (2012-08-20)
* Badge text is set to the last segment of the workspace path unless a name has been specified in preferences.

###Version 1.0.0 (2012-08-07)
* Badge text can be set using workspace preferences.
* **Open Workspace** command available from file menu.
  * Lists all known workspaces
  * **Other…** menu item brings up the selection dialog.