##OS X Eclipse Launcher##


This feature will add an **Open Workspace** menu item that will detect the application of the running Eclipse instance and start another in a fashion similar to the **Switch Workspace** command.

<img src="https://raw.github.com/turesheim/eclipse-utilities/master/images/osx-launcher.jpg" width="616px"/>

Now with all these Eclipse instances up and running it is probably a good idea to be able to tell them apart. The workspace name can be set in **Preferences > General > Workspace** and it will show up in the icon badge. Otherwise the last segment of the workspace path will be used.

Note that OS X 10.6 or newer is required as this feature relies on the [open](http://developer.apple.com/library/mac/#documentation/Darwin/Reference/ManPages/man1/open.1.html) command's ability to pass on arguments.

You can install from the <a href="http://marketplace.eclipse.org/content/osx-eclipse-launcher">Eclipse Marketplace</a> or drag <a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=364668" title="Drag and drop into a running Eclipse workspace to install OSX Eclipse Launcher"><img src="http://marketplace.eclipse.org/misc/installbutton.png" width="32px" style="border:0px;margin:0px;padding:0px" /></a> into an running Eclipse instance.