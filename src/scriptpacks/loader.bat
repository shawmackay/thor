@echo off
$[javaloc] -classpath $[additionallibs];$[jinilibs];$[serviceuiloc] org.jini.projects.thor.client.ThorLoader %*