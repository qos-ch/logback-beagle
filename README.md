## Contributor Guide

Here are the steps required for importing the logback-beagle project into Eclipse. Let _$LB-BEAGLE_ be the folder where you cloned [logback-beagle](https://github.com/qos-ch/logback-beagle) from Github.

### Import required projects

Import all projects into Eclipse:

- ch.qos.logback.beagle
- ch.qos.logback.beagle.feature
- ch.qos.logback.beagle.releng
- ch.qos.logback.beagle.repository
- ch.qos.logback.beagle.target
- ch.qos.logback.beagle.tests (compilation errors unresolved)

### Set the target platform

- In Eclipse go to project ch.qos.logback.beagle.target and open ch.qos.logback.beagle.target.target file. This will open a window labeled "Target Definition". Wait a few moments for the required plug-ins to be downloaded.
- Click on "Set as Target Platform" in the "Target Definition" window.

### Building update site

From console go to _$LB-BEAGLE_/ch.qos.logback.beagle.releng and issue command:

    mvn clean package

Update site will be inside _$LB-BEAGLE_/ch.qos.logback.beagle.repository/target/repository folder.


Please contact logback-dev@qos.ch [mailing list](http://logback.qos.ch/mailinglist.html) if you have
questions about importing logback-beagle into Eclipse.
