#!/bin/bash
mvn clean verify -f no.resheim.eclipse.utils-parent 
mvn sonar:sonar -Psonar -f no.resheim.eclipse.utils-parent
