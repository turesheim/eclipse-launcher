## OS X Eclipse Launcher [![Build Status](https://travis-ci.org/turesheim/eclipse-launcher.svg?branch=master)](https://travis-ci.org/turesheim/eclipse-launcher) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=turesheim_eclipse-launcher&metric=alert_status)](https://sonarcloud.io/dashboard/index/turesheim_eclipse-launcher)

This feature will add an **Open Workspace** menu item that will detect the application of the running Eclipse instance 
and start another in a fashion similar to the **Switch Workspace** command.

<img src="https://raw.github.com/turesheim/eclipse-utilities/master/images/osx-launcher.jpg" width="50%"/>

Now with all these Eclipse instances up and running it is probably a good idea to be able to tell them apart. The 
workspace name can be set in **Preferences > General > Workspace** and it will show up in the icon badge. Otherwise the
last segment of the workspace path will be used.

Additionaly it is possible to use the **Advanced...** menu item that allowing you to specify launch options when opening
a new workspace. Using this you can easily debug a new instance of the running IDE without creating a launch 
configuration.

<img src="https://raw.github.com/turesheim/eclipse-utilities/master/images/Open_Workspace.png" width="50%"/>

Note that OS X 10.6 or newer is required as this feature relies on the 
[open](http://developer.apple.com/library/mac/#documentation/Darwin/Reference/ManPages/man1/open.1.html) command's 
ability to pass on arguments.

## Installing

You can install from the <a href="http://marketplace.eclipse.org/content/osx-eclipse-launcher">Eclipse Marketplace</a>
or drag <a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=364668" title="Drag and drop into a
running Eclipse workspace to install OSX Eclipse Launcher">
<img src="https://marketplace.eclipse.org/sites/all/modules/custom/marketplace/images/installbutton.png" 
style="border: 0px; margin:0px; padding:0px; vertical-align:bottom;"/></a> into an running Eclipse instance.

If you have a version older than 3.0 installed you must uninstall this before installing version 3.0 or newer. Go to 
**Eclipse > About Eclipse > Installation Details**, select "OS X Eclipse Launcher Utility" and click **Uninstall...**. 
Automatic update from 2.0 or older to 3.0 or newer will not work and keeping both versions installed will lead to 
unpredictable behaviour.

## Building

Clone the project and from the root execute:

    mvn clean verify
    
When successful there will be a Eclipse p2 repository at *net.resheim.eclipse.launcher-site/target/repository* which you
can install from.

## License

Copyright © 2012-2020 Torkild Ulvøy Resheim and contributors. All rights reserved. This program and the accompanying 
materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and
is available at http://www.eclipse.org/legal/epl-v10.html
