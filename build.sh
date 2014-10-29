#!/bin/bash
# Perform the actual build with unit tests
mvn clean verify -Pskip-ui-tests -f no.resheim.eclipse.utils-parent 
# Execute UI tests
if [ $? -eq 0 ]; then
	mvn verify -f no.resheim.eclipse.utils.launcher.tests
fi
# Run SonarQube analysis
if [ $? -eq 0 ]; then
	mvn sonar:sonar -Psonar -f no.resheim.eclipse.utils-parent
fi
