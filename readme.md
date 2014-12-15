## Introduction

Code to detect the embedded high frequency audio file and fetch corresponding project and card details. The detected card, the corresponding project and the other associated cards in the same project will be stored in an local SQLite database. Every fetched card also has an option to send a tweet from the same page.

# Adding support library projects.

  1) Google Play Services
	
	Add the "google-play-services_lib" library project by using eclipse (project > properties > Android > library)

  2) App compactability

	Add the "appcompat" library project by using eclipse (project > properties > Android > library)
 
  3) View pager library
	
	Add the "viewpager_library" library project by using eclipse (project > properties > Android > library)

![Alt text](filmsync_support_library_add_help.png?raw=true "Support Library Projects")

  4) Use Android API level 21 (Android 5.0).

# Integrating FilmSync library project to FilmSync demo project.

### Before start implementing the FilmSync Library to the FilmSync demo project

* FilmSync Android Library downloaded from filmSync site.
* The API secret key can be obtained from the FilmSync web tool (FilmSync.org).

### Key Steps to Integrate Library File

1.Copy the jar file to libs folder and add it to android application build path.

2.Create com.fingent.filmsynclib.Controller class object.

	eg:- Controller controller=new Controller(getActivity().getApplicationContext(), ScanFragment.this).

3.Start the audio stream listening by calling onStartListerning() at onResume of activity.

	eg:- controller.onStartListerning();

4.Set the base url and api secret.

	//set base url
	ApiHandler.setBaseUrl(BASE_URL);

	//set API secret
	ApiHandler.setApiSecret(API_SECRET);

5. Implement com.fingent.filmSynclib.interfaces.CodeDetectedListener interface, 
	
		onCodeHeaderDetected function will get executed when a code header H/h detected.
		onCodeDetected function will get executed when code detected.
		onCodeMissed function will get executed when a code header detected but not catched the full code.
6. Execute com.fingent.filmSynclib.api.servicehandler.GetCardDetails(AsyncTask) for fetching card details from server. 

	 Implement com.fingent.filmSynclib.interfaces.CardDetectionListener interface,
	
		onCardDetected function will get executed when a card for the detected code present in the server and finished downloading the card data.

		onCardDeleted function will get executed when a card detected present in the local data base but deleted from server database.
 
	 Execute com.fingent.filmSynclib.api.servicehandler.GetProjectDetails AsynTask to get project details of detected card.
	
	 Implement com.fingent.filmSynclib.interfaces.ProjectDectectionListener interface,

		onProjectDetected function will get executed when a project for the detected card detected and finished downloading the project data.
	
		onProjectDeleted function will get executed when a project detected present in the local data base but deleted from server database.

 	Execute com.fingent.filmSynclib.api.servicehandler.GetSessionID AsyncTask for session management.
 	Implement com.fingent.filmSynclib.interfaces.SessionReceivedListener interface,

		onSessionExpiredWithCard function will get executed when session expired during fetching card details.

		onSessionExpiredWithProject function will get executed when session expired during fetching project details.

		onSessionReceived function will executed when a valid session data received.
	
		onSessionReceivedForCard function will get executed when session re-authenticated while receiving card details.

		onSessionReceivedForProject function will get executed when session re-authenticated while receiving project details.

	

	
