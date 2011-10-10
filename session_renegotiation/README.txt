This project is here to help test browsers reactions to session renegotiation as well as to test the building of such a library. Session renegotation allows servers behind https to request client certificates only for certain parts of the URL space. This is important if web sites are going to be 100% behind https, as otherwise users get asked for a certificate before they can even reach the web site.

It is worth looking at the code to understand what is going on - and the code should be cleaned up to make that easier. 
This code is built using the RESTlet library at http://restlet.org/ . It requires a couple of its modules that are in the trunk. They can be found here. (see the pom.xml file of this project to find out which modules)

 http://restlet.tigris.org/svn/restlet/trunk


Run it using maven with

$ mvn -e clean compile exec:java

Or of course if nothing has changed and all is compiled 

$ mvn exec:java

In order to enable debugging set the following environmental variable

$ export MAVEN_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

If you wish to run java so that it works with the unsafe renegotation enabled then add the following option
http://download.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html#tlsRenegotiation

$ export MAVEN_OPTS="$MAVEN_OPTS -Dsun.security.ssl.allowUnsafeRenegotiation=true"

You should then be able to point your browser to http://localhost:8443/
and reset the session using the online form, and ask the server to 
send a security exception the next time it needs to verify the X509Certificate
that will be sent for the next session.  There are a number of java exceptions
to select from, to see if that makes any difference.

If there are any other tricks one should try please let me know.

Wireshark
---------

If you want to see what is going on the wire you can use wireshark
follow the HOWTO at http://wiki.wireshark.org/SSL
You can use the following 

0.0.0.0,8443,http,$TLS_TEST_HOME/src/main/resources/localhost.p12,secret

(of course you must replace $TLS_TEST_HOME with the full path to the
directory continaing this file)
