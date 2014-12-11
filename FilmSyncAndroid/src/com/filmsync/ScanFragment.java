package com.filmsync;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.filmSynclib.Controller;
import com.filmSynclib.api.constants.ApiConstants;
import com.filmSynclib.api.servicehandler.ApiHandler;
import com.filmSynclib.api.servicehandler.GetCardDetails;
import com.filmSynclib.api.servicehandler.GetProjectDetails;
import com.filmSynclib.api.servicehandler.GetSessionID;
import com.filmSynclib.interfaces.CardDetectionListener;
import com.filmSynclib.interfaces.CodeDetectedListener;
import com.filmSynclib.interfaces.ProjectDectectionListener;
import com.filmSynclib.interfaces.SessionReceivedListener;
import com.filmSynclib.model.Card;
import com.filmSynclib.model.Project;
import com.filmSynclib.model.Session;
import com.filmSynclib.preferences.FilmSyncPreferences;
import com.filmsync.Utility.Utility;
import com.filmsync.constants.Constants;
import com.filmsync.database.FilmSyncDBHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Class to show scanning and scanned cards(Markers).
 * @author fingent
 *
 */
public class ScanFragment extends Fragment 
implements SessionReceivedListener, CodeDetectedListener,CardDetectionListener,ProjectDectectionListener{

	Controller controller; //Android filmsync library controller object to start and stop scanning.
	Button btnTwitter;
	TextView scanStatusMessage;
	TextView tvTitle; // card title
	ImageView scanAnimation; // Scan animation
	FilmSyncDBHelper dbHelper; //Database handler
	Card card;
	WebView wvCard; //card content web view
	FrameLayout webViewFrameLayout; //Web view and twitter button container layout.
	String twitter_search=""; //Card Twitter search tag
	Session session;
	ApiHandler apiHandler;
	AnimationDrawable scanAnimationDrawable;
	FilmSyncPreferences filmSyncPreferences;
	public static final String BLANK_URL="about:blank"; //blank url to reset the web view
	FilmSyncAnalytics filmSyncAnalytics;
	Bundle bundle; //Bundle to retrieve selected card details.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View v=inflater.inflate(R.layout.card_details, container, false);

		//initialize filmsync library controller object.
		controller=new Controller(getActivity().getApplicationContext(), ScanFragment.this);

		try{

			//initialize UI components. 
			btnTwitter=(Button) v.findViewById(R.id.btnTwitter);
			scanStatusMessage=(TextView) v.findViewById(R.id.tvDetectedText);
			tvTitle=(TextView) v.findViewById(R.id.tvCardTitle);
			webViewFrameLayout=(FrameLayout) v.findViewById(R.id.webViewContainer);
			scanAnimation=(ImageView) v.findViewById(R.id.imgScanAnimation);

			//set card content web view
			wvCard=(WebView) v.findViewById(R.id.webView_Card);
			wvCard.setWebChromeClient(new WebChromeClient());
			//set web view background to transparent. 
			wvCard.setBackgroundColor(0);
			WebSettings webSettings=wvCard.getSettings();
			webSettings.setJavaScriptEnabled(true);
			//webSettings.setBuiltInZoomControls(true);
			webSettings.setLoadsImagesAutomatically(true);
			webSettings.setLoadWithOverviewMode(true);
			webSettings.setUseWideViewPort(true);

			//instantiate database controller
			dbHelper=new FilmSyncDBHelper(getActivity().getApplicationContext(), FilmSyncDBHelper.DB_NAME, null, 1);

			apiHandler=new ApiHandler();

			//set base url
			ApiHandler.setBaseUrl(Constants.BASE_URL);

			//set API secret
			ApiHandler.setApiSecret(Constants.API_SECRET);

			filmSyncPreferences=new FilmSyncPreferences(getActivity().getApplicationContext());

			scanAnimation.setBackgroundResource(R.drawable.scan_animation_list);
			scanAnimationDrawable=(AnimationDrawable) scanAnimation.getBackground();

			setViews();

			try{
				//retrieve selected card details from bundle.
				bundle=getArguments();
				if(bundle!=null){
					System.out.println("Scan Fragment bundle is not null");
					int projectID = Integer.parseInt(bundle.getString(FilmSyncDBHelper.PROJECT_ID));
					String title = bundle.getString(FilmSyncDBHelper.CARD_TITLE);
					String contentUri = bundle.getString(FilmSyncDBHelper.CARD_CONTENT);
					twitter_search=dbHelper.getProjectTwitterSearch(projectID);

					if(title!=null && !title.equals("") &&
							contentUri!=null && !contentUri.equals("")){
						scanStatusMessage.setText("Loading...");

						//set Card tile view text.
						tvTitle.setText(title);

						if(Utility.isInternetAvailable(getActivity().getApplicationContext())) //Network Available
						{
							//load card content url.
							wvCard.loadUrl(contentUri);

						}else{//Network not available.
							Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
							//clear the status text.
							scanStatusMessage.setText("");
						}
					}else{
						System.out.println("Selected Card details are null");
					}
				}else{
					System.out.println("Scan Fragment bundle is null");
				}


			}catch(NullPointerException ne){

				ne.printStackTrace();
				scanStatusMessage.setText("");
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				scanStatusMessage.setText("");
			}


			//Google Analytics
			filmSyncAnalytics=new FilmSyncAnalytics(getActivity().getApplicationContext());
			Tracker t=filmSyncAnalytics.getTracker(FilmSyncAnalytics.TrackerName.APP_TRACKER);
			// Set screen name.
			t.setScreenName(FilmSyncAnalytics.FILMSYNC_SYNC_SCREEN);
			// Send a screen view.
			t.send(new HitBuilders.AppViewBuilder().build());

			//Show the twitter activity for sharing card information.
			btnTwitter.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					//Start twitter activity.
					try{
						Intent twitterActivityIntent=new Intent(getActivity(), TwitterActivity.class);
						twitterActivityIntent.putExtra(Constants.TWITTER_SEARCH_KEY, twitter_search);
						getActivity().startActivity(twitterActivityIntent);
					}catch(NullPointerException ne){
						ne.printStackTrace();

					}catch (Exception e) { 
						e.printStackTrace();
					}
				}
			});

			//card content Web view loading finished listener.
			wvCard.setWebViewClient(new WebViewClient(){

				@Override
				public void onPageFinished(WebView view, String url) {
					// TODO Auto-generated method stub
					super.onPageFinished(view, url);
					try{


						if(!url.equalsIgnoreCase(BLANK_URL)) //not a blank url.
						{
							//clear the scan status text
							scanStatusMessage.setText("");
							//stop the animation
							scanAnimationDrawable.stop();
							//remove animation view
							scanAnimation.setVisibility(View.GONE);
							//make card title visible 
							tvTitle.setVisibility(View.VISIBLE);

							//make card content and twitter button visible.
							webViewFrameLayout.setVisibility(View.VISIBLE);
						}
					}catch(NullPointerException ne){
						ne.printStackTrace();

					}catch (Exception e) {
						e.printStackTrace();
					}

				}
			});

		}catch(NullPointerException ne){
			ne.printStackTrace();

		}catch (Exception e) {
			e.printStackTrace();
		}


		return v; 
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		//Start Google analytics tracker.
		GoogleAnalytics.getInstance(getActivity().getApplicationContext()).reportActivityStart(getActivity());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try{
			if(scanAnimation.getVisibility()==View.VISIBLE){ //scan animation is visible
				scanAnimationDrawable.start(); //start the animation
			}

			//start listening the tune.
			controller.onStartListerning();

			//resume the card content web view
			wvCard.onResume();
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		//stop the animation
		scanAnimationDrawable.stop();

		//stop listening the tune
		controller.onStopListerning();
		
		//pause the card content web view
		wvCard.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		//Stop Google analytics tracker.
		GoogleAnalytics.getInstance(getActivity().getApplicationContext()).reportActivityStop(getActivity());
	}


	/**
	 * Function to set the visibility of scanStatusMessage, scanAnimation, webViewFrameLayout, tvTitle and start the progress wave animation.
	 */
	public void setViews(){

		try{
			scanStatusMessage.setVisibility(View.VISIBLE);
			scanAnimation.setVisibility(View.VISIBLE);
			webViewFrameLayout.setVisibility(View.GONE);
			tvTitle.setVisibility(View.GONE);
			scanAnimationDrawable.start();
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
			// TODO: handle exception
		}
	}


	/**
	 * Function executed when a valid marker(Card id) detected.
	 */
	@Override
	public void onCodeDetected(String cardID) {
		// TODO Auto-generated method stub
		try{

			//System.out.println("OnCode Detected....");
			//make status text visible.
			scanStatusMessage.setVisibility(View.VISIBLE);
			scanStatusMessage.setText(cardID);
			if(Utility.isInternetAvailable(getActivity().getApplicationContext())){//Network available
				//get the store session data from preference file.
				session=new Session(filmSyncPreferences.getSessionID(), filmSyncPreferences.getSessionStatus());

				//API call to get card details from server. 
				new GetCardDetails(cardID,this,this,session).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				//set scan status text.
				scanStatusMessage.setText("Loading...");

			}else{//Network not available.
				Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
				//clear the status text.
				scanStatusMessage.setText("");
			}

		}catch(NullPointerException ne){
			ne.printStackTrace();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when detect partial marker.
	 */

	@Override
	public void onCodeMissed() {
		// TODO Auto-generated method stub

		try{
			//set scan status text.
			scanStatusMessage.setText("Invalid Card!!!");
		}catch(NullPointerException ne){
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when a marker head H/h detected.
	 */
	@Override
	public void onCodeHeaderDetected(String msg) {
		// TODO Auto-generated method stub

		try{
			//reset the views
			setViews();
			//clear the card content web view
			wvCard.loadUrl("about:blank");
			System.out.println("Listening...."+scanStatusMessage);
			//set the scan status text
			scanStatusMessage.setText(msg);
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when a valid card detected as result of API call.
	 */
	@Override
	public void onCardDetected(Card card) {
		// TODO Auto-generated method stub

		try{
			if(card.getWarningMSG().equalsIgnoreCase(ApiConstants.BASE_URL_NOT_SET_WARNING_MSG)){//Base url and API secret is not valid.
				Toast.makeText(getActivity(), ApiConstants.BASE_URL_NOT_SET_WARNING_MSG, Toast.LENGTH_SHORT).show();

			}else{//Base url and API secret is valid


				twitter_search=card.getCardTwitterSearch();
				//set Card tile view text.
				tvTitle.setText(card.getCardTitle());

				if(Utility.isInternetAvailable(getActivity().getApplicationContext())) //Network Available
				{
					//load card content url.
					wvCard.loadUrl(card.getCardContent());
					//get stored session from preference file.
					session=new Session(filmSyncPreferences.getSessionID(), filmSyncPreferences.getSessionStatus());

					//API call to get project details from server.
					new GetProjectDetails(card,this,this,session).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

				}else{//Network not available.
					Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
					//Clear the scan status text.
					scanStatusMessage.setText("");
				}
			}
		}catch(NullPointerException ne){
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}

	}

	/**
	 * Function executed when a valid project detected as result of API call.
	 */
	@Override
	public void onProjectDetected(Project project) {

		// TODO Auto-generated method stub

		try{
			if(dbHelper.isProjectExist(Integer.parseInt(project.getProjectID()))) // Project already exist in the Local DB 
			{
				//update the project in local database.
				dbHelper.updateProject(project);
				Log.d("UPDATE STATUS", "Project details for "+project.getProjectID()+" updated");
			}
			else // Project not exist in the Local DB
			{
				//insert the new project in to local database.
				dbHelper.inserProject(project);
				Log.d("INSERT STATUS", "Project details for "+project.getProjectID()+" updated");
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}


	/**
	 * Function executed when project detected that not present server but present local database.
	 */
	@Override
	public void onProjectDeleted(int projectID) {
		// TODO Auto-generated method stub
		try{
			Toast.makeText(getActivity().getApplicationContext(), "Project Not Exist!!!", Toast.LENGTH_SHORT).show();
			//Deleted project detected.
			if(dbHelper.isProjectExist(projectID)){//project exist in the local DB.

				//delete the all cards from local database.
				dbHelper.deleteAllCardsForProject(projectID);

				//delete the project from local database.
				dbHelper.deleteProject(projectID);
			}
		}catch(NullPointerException ne){
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when Card detected that not present server but present local database.
	 */
	@Override
	public void onCardDeleted(String cardNo) {
		// TODO Auto-generated method stub

		//Card number detected which is not present in server.

		//Clear the scan status text.
		scanStatusMessage.setText("");
		Toast.makeText(getActivity().getApplicationContext(), "Card Not Exist!!!", Toast.LENGTH_SHORT).show();
		try{
			Integer pID=dbHelper.getProjectIDForCard(cardNo);

			if(pID!=null){
				if(dbHelper.isCardExist(pID, cardNo)){//card exist the local DB.

					//delete the card from local database..
					dbHelper.deleteCard(cardNo);
				}

				Card card=new Card();
				card.setCardID(cardNo);
				card.setCardProjectID(pID.toString());

				/**
				 * API call get project details of detected card. If the project is not present in server, it will also removed from local database.
				 */
				new GetProjectDetails(card,this,this,session).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}catch(NullPointerException ne){
			ne.printStackTrace();
		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when session expired while fetching card details. It re-authenticate the session. 
	 */
	@Override
	public void onSessionExpiredWithCard(String cardNo) {
		// TODO Auto-generated method stub
		try{
			if(Utility.isInternetAvailable(getActivity().getApplicationContext())){//Network available.

				//get new session
				new GetSessionID(this,cardNo).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			else{//Network not available.
				Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}


	/**
	 * Function executed when session expired while fetching project details. It re-authenticate the session. 
	 */
	@Override
	public void onSessionExpiredWithProject(Card card) {
		// TODO Auto-generated method stub
		try{
			if(Utility.isInternetAvailable(getActivity().getApplicationContext())){//Network available.

				//get new session.
				new GetSessionID(this,card).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
			else{//Network not available.
				Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when a valid session received as result of session re-authentication.
	 */
	@Override
	public void onSessionReceived(Session session) {
		// TODO Auto-generated method stub
		try{
			if(session.getSessionStatus().equals(ApiConstants.SESSION_ACTIVE_VALUE)){//valid session received.

				//save the new session in the preference file.
				filmSyncPreferences.putSessionID(session.getSessionID());
				filmSyncPreferences.putSessionStatus(session.getSessionStatus());

				if(session.getWarningMSG().equalsIgnoreCase(ApiConstants.BASE_URL_NOT_SET_WARNING_MSG)){
					Toast.makeText(getActivity(), ApiConstants.BASE_URL_NOT_SET_WARNING_MSG, Toast.LENGTH_SHORT).show();
				}
			}else{//Invalid session received.
				Toast.makeText(getActivity(), "API Secret is not valid!!!", Toast.LENGTH_SHORT).show();
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when a valid session received as result of session re-authentication while fetching card details.
	 */
	@Override
	public void onSessionReceivedForCard(Session session, String codeNo) {
		// TODO Auto-generated method stub
		try{
			if(session.getSessionStatus().equals(ApiConstants.SESSION_ACTIVE_VALUE)){//valid session received.

				//save the new session.
				filmSyncPreferences.putSessionID(session.getSessionID());
				filmSyncPreferences.putSessionStatus(session.getSessionStatus());

				onCodeDetected(codeNo);
			}else{//Invalid session received.
				Toast.makeText(getActivity(), "API Secret is not valid!!!", Toast.LENGTH_SHORT).show();
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) { 
			e.printStackTrace();
		}
	}

	/**
	 * Function executed when a valid session received as result of session re-authentication while fetching project details.
	 */
	@Override
	public void onSessionReceivedForProject(Session session, Card card) {
		// TODO Auto-generated method stub

		try{
			if(session.getSessionStatus().equals(ApiConstants.SESSION_ACTIVE_VALUE)){//valid session received.

				//save new session.
				filmSyncPreferences.putSessionID(session.getSessionID());
				filmSyncPreferences.putSessionStatus(session.getSessionStatus());

				if(card.getWarningMSG().equalsIgnoreCase(ApiConstants.BASE_URL_NOT_SET_WARNING_MSG)){//not a valid base url and API secret.

					Toast.makeText(getActivity(), ApiConstants.BASE_URL_NOT_SET_WARNING_MSG, Toast.LENGTH_SHORT).show();

				}else{//valid base url and API secret.

					//stop animation
					scanAnimationDrawable.stop();
					//remove the animation view.
					scanAnimation.setVisibility(View.GONE);
					//clear the status text
					scanStatusMessage.setText("");
					//make web view visible.
					webViewFrameLayout.setVisibility(View.VISIBLE);
					//load the card content.
					wvCard.loadUrl(card.getCardContent());
					twitter_search=card.getCardTwitterSearch();
					if(Utility.isInternetAvailable(getActivity().getApplicationContext())) //Internet Connectivity Available
					{
						//get stored session from preference file.
						session=new Session(filmSyncPreferences.getSessionID(), filmSyncPreferences.getSessionStatus());
						//API call to get project details.
						new GetProjectDetails(card,this,this,session).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

					}else{
						Toast.makeText(getActivity().getApplicationContext(), R.string.internet_connectivity_not_available, Toast.LENGTH_SHORT).show();
					}
				}
			}else{//Invalid session received.
				Toast.makeText(getActivity(), "API Secret is not valid!!!", Toast.LENGTH_SHORT).show();
			}
		}catch(NullPointerException ne){ 
			ne.printStackTrace();

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
