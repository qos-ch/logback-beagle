
change version tags:


in ./pom.xml
in ./ch.qos.logback.beagle/pom.ml
in ./ch.qos.logback.beagle/META-INF/MANIFEST.MF

in ./ch.qos.logback.beagle.feature/pom.xml
in ./ch.qos.logback.beagle.feature/feature.xml

in ./ch.qos.logback.beagle.repository/pom.ml
in ./ch.qos.logback.beagle.repository/categoy.xml

in ./ch.qos.logback.beagle.tests/pom.ml
in ./ch.qos.logback.beagle.tests/META-INF/MANIFEST.MF



# script to manually check compiler ouptut (jdk 6 -> major version 50, jdk 7, major version 51)
cd ch.qos.logback.beagle/target/classes
CHECK=$(for i in $(find . -name "*.class"); do echo $i|tr '/' '.'|sed s/.class//|sed s/^\.\.//; done)
for i in $CHECK; do echo $i; javap -verbose $i|grep major|grep -v "major version: 50";done