@echo off	
echo -------------------------
echo      Starting Thor
echo -------------------------
$[javaloc] -classpath .;$[jinilibs];$[serviceuiloc];$[additionallibs] -Djava.security.policy=$[policy] org.jini.projects.thor.service.Startup  $[config]
