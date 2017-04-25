# DropboxConsoleApp


##Prerequisite
	1. install java 1.7 or greater
	2. install Apache Maven 3.3.9 (for development env)
	
##Packaging
	1. Download dropbox-client project
	2. Extract dropbox-client project
	3. cd <dropbox-client project path>
	4. run `mvn package`
	

##How to run
	1. Do package dropbox-client project (follow steps from Packaging)
	2. Go to target path. 
		`cd <dropbox-client project path>/target/`
	3. java -jar ./dropbox-client-0.0.1-SNAPSHOT-jar-with-dependencies.jar [command] [command parameters] [options]\
	
	
##Usage
```
Usage: <main class> [command] [command parameters] [options]

Options:
  --log  Prints debug logs in console
Commands:

  auth  This command authenticates Dropbox user's account. 
    Usage: <main class> auth  {appKey}  {appSecret}  {locale}
      Parameter   Description                           Required   
      -------------------------------------------------------------
      appKey      the Dropbox application key           true       
      appSecret   the Dropbox application secret code   true       
      locale      locale                                false      
      -------------------------------------------------------------


  help  This command prints command line usage.
    Usage: <main class> help


  info  This command retrieves information about Dropbox user's account.
    Usage: <main class> info  {accessToken}  {locale}
      Parameter     Description                                                           Required   
      -----------------------------------------------------------------------------------------------
      accessToken   the authorization code, which could be generated using auth command   true       
      locale        locale                                                                false      
      -----------------------------------------------------------------------------------------------


  list  This command list files and folders within the specified directory and some metadata about each entry
    Usage: <main class> list  {accessToken}  {path}  {locale}
      Parameter     Description                                                                       Required   
      -----------------------------------------------------------------------------------------------------------
      accessToken   Authorization code, which could be generated using auth command                   true       
      path          Path to a file or folder to list details about. Path should be in double quotes   true       
      locale        locale                                                                            false      
      -----------------------------------------------------------------------------------------------------------	
```