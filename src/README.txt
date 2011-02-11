README for Thor 1.0 beta 3

Configuring Thor
-----------------------

Thor uses two configuration files, one is always used, the second is more instance specific.
The configuration that is always used is 'config/exportmgr.config' - this details all the exporters that will be built
and tracked by the ExporterManager utility

There are 3 different exporter definitions that Thor uses:

Service 		- The proxy type that is registered with the LUS, and for sessions
ClientHandlers 	- Proxy type for exporting branches
Standard 	- The proxy that for other general proxies	

The second configuration file is for the particular instance 
that you are running and requires the following details to be specified

name - 
	Name of the Instance which will also be 
	shown as an attribute in a Lookup Browser
groups	- 
	The group names that Thor will join.
codebase	- 
	The address from which Thor's downloaded 
	classes will be served from 
policy - 
	The name of the security policy 
	file to run Thor under
	
Initialising a Thor tree from XML
------------------------------
In addition, to the configuration parameters above, add the following,
in the org.jini.projects.thor configuration component:

init=new Boolean(true);
xmlfile="<path to your initialisation xml file>";

Then start Thor normally. After the initialisation change the value of init to
init=new Boolean(false);

Otherwise the tree will be reinitialised every time thor is restarted.

Starting Thor with ServiceStarter
---------------------------------
Thor provides a service starter configuration in config/thorstarter.config
(This configuration will be automatically created for you when you run the 
post-install process after initial installation.