#!/bin/bash
mvn versions:set -DnewVersion=1.4.0 -f no.resheim.eclipse.utils-parent
mvn sonar:sonar -Psonar -f no.resheim.eclipse.utils-parent
