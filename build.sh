#!/bin/bash
# Perform the actual build with unit tests
mvn clean install -Pskip-ui-tests -f net.resheim.eclipse.launcher-parent 
# Execute UI tests
if [ $? -eq 0 ]; then
	mvn verify -f net.resheim.eclipse.launcher.tests
fi
