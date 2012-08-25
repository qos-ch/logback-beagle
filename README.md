## Contributor Guide

Here are the steps required for importing the logback-beagle project into Eclipse. Let _$LB-BEAGLE_ be the folder where you cloned [logback-beagle](https://github.com/qos-ch/logback-beagle) from Github.

### Generate the project settings

Here are the commands:

    cd $LB-BEAGLE
    mvn eclipse:clean 
    mvn eclipse:eclipse 

### Set the target platform

- In Eclipse, File -> Open File, select _$LB-BEAGLE_/logback-beagle.target. This will open a window labeled "Target Definition". Wait a few
moments for the required plug-ins to be downlaoded.

- Click on "Set as Target Platform" in the "Target Definition" window.

### Import required projects

Import following projects into Eclipse:

- ch.qos.logback.beagle

Please contact logback-dev@qos.ch [mailing list](http://logback.qos.ch/mailinglist.html) if you have
questions about importing logback-beagle into Eclipse.
