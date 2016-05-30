# Rosetta Wayback VPP

## About

This plugin enables [OpenWayback](http://www.netpreserve.org/openwayback) as an external viewer in Rosetta. It has two viewer modes:

  - Overview (default): Directs users to the overview page for a given URL. This is similar to the user searching directly in OpenWayback.
  - Detail: Directs users to a certain harvest represented by the requested IE.

Detail view mode needs a webserver redirect due to technical restrictions of Rosetta (see Installation for details).

The mode is selected by the viewer option `detail`. The value `true` selects detail mode, every other value (or none) selects overview mode.

### Options

The VPP supports several options. To set an option, go in Rosetta Admin Backend to your delivery rule, section `Output Parameters`. Add an option using `name=value`.

| Option   | Value     | Description |
| -------- | --------- | ----------- |
| `detail` | `true`    | Directs the user to the capture represented by the IE (URL at a certain harvest time). |
|          | `false`   | Directs the user to the overview page as if the user had searched for the URL in Wayback. |
|          | *default* | `false` |
| `marker` | *any*     | A string used to delimit the relevant URL in part for detail view for webserver redirects. |
|          | *default* | `@` |


## Installation

### Rosetta

1. Move the JAR-file to `./operational_shared/plugins/custom`.
1. Rosetta Admin Backend: Go to `Plugin-Management --> Custom Plugins` and add a new `Plugin Instance`
1. Rosetta Admin Backend: Go to `Delivery --> Viewers Management --> Add External Viewer`:
  - `Level` needs to be `IE`.
  - `URL` needs to be `http://<WAYBACK_HOSTNAME>/wayback/query`
1. Rosetta Admin Backend: Go to `Delivery --> IE Delivery Rules --> Add New Delivery Rule`. At `Output Parameters` add `detail=true` for detail mode.

### Apache Webserver

An example configuration for Apache Webserver. Only needed for detail mode. Same scheme would work with other webservers as well (fell free to contribute more configuration examples).

```apache
RewriteEngine on
RewriteCond %{QUERY_STRING} ^(([^&]*&)*)@(.*)@(.*)$
RewriteRule ^/wayback/query$ /wayback%3? [L]
```

## Development

To build this project the [Rosetta PDS SDK](https://developers.exlibrisgroup.com/rosetta/sdk) from [GitHub](https://github.com/ExLibrisGroup/Rosetta.dps-sdk-projects/tree/master/current/dps-sdk-plugins/lib) is needed as there is no public Maven repository. Note that the jar-Files for "deposit" and "plugin" are the same, but "deposit" has more Rosetta-Versions. To use the SDK, you have to download and install it locally:

```bash
curl -sS -O https://raw.githubusercontent.com/ExLibrisGroup/Rosetta.dps-sdk-projects/master/5.0.1/dps-sdk-deposit/lib/dps-sdk-5.0.1.jar
mvn install:install-file -Dfile=dps-sdk-5.0.1.jar -DgroupId=com.exlibris.dps -DartifactId=dps-sdk -Dversion=5.0.1 -Dpackaging=jar
```

Building a deployable jar file including all necessary configuration files:

    mvn clean install
