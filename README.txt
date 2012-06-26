
Here are the steps required for importing the logback-beagle project
into Eclipse. Let $LB-BEAGLE be the folder where you cloned
logback-beagle from github [1].

1) Generate the project settings. Here are the commands:

> cd $LB-BEAGLE
> mvn eclipse:clean 
> mvn eclipse:eclipse 

2) Set the target platform for the Eclipse workbench

- In Eclipse, File -> Open File, select $LB-BEAGLE/logback-beagle.target 

This will open a window labeled "Target Definition". Wait a few
moments for the required plug-ins to be downlaoded.

- Click on "Set as Target Platform" in the "Target Definition" window

3a) Import the ch.qos.logback.beagle.core project into Eclipse
3b) Import the ch.qos.logback.beagle project into Eclipse


Please contact logback-dev@qos.ch mailing list [2] if you have
questions about importing logback-beagle into Eclipse.

[1] https://github.com/qos-ch/logback-beagle
[2] http://logback.qos.ch/mailinglist.html