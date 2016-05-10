# Rosetta Wayback VPP

## About

This plugin enables [OpenWayback](http://www.netpreserve.org/openwayback) as an external viewer in Rosetta. It has two viewer modes:

  - Overview (default): Directs users to the overview page for a given URL. This is similar to the user searching directly in OpenWayback.
  - Detail: Directs users to a certain harvest represented by the requested IE.

Detail view mode needs a webserver redirect due to technical restrictions of Rosetta (see Installation for details).

The mode is selected by the viewer option `detail`. The value `true` selects detail mode, every other value (or none) selects overview mode.

## Installation

1. Move the JAR-file to `./operational_shared/plugins/custom`.
2. Create a new viewer in the admin backend of Rosetta, selecting the jar file.

## Development

To build this project the Rosetta PDS SDK from GitHub is needed (there is no public Maven repository). To install the SDK, you have to download and install it locally:

```bash
curl -sS -O https://raw.githubusercontent.com/ExLibrisGroup/Rosetta.dps-sdk-projects/master/5.0.1/dps-sdk-deposit/lib/dps-sdk-5.0.1.jar
mvn install:install-file -Dfile=dps-sdk-5.0.1.jar -DgroupId=com.exlibris.dps -DartifactId=dps-sdk -Dversion=5.0.1 -Dpackaging=jar
```

Building a deployable jar file including all necessary configuration files:

    mvn clean install
