@echo off

$[javaloc] -Djava.security.policy=$[policy]  -jar $[jinidir]\start.jar config\thorstarter.config
