#!/bin/bash
mvn versions:set -DnewVersion=1.4.1-SNAPSHOT -f no.resheim.eclipse.utils-parent
mvn sonar:sonar -Psonar -f no.resheim.eclipse.utils-parent
