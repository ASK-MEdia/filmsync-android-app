package com.filmsync;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filmsync.constants.Constants;

/**
 * Class to show twitter share view.
 * @author fingent
 *
 */
public class TwitterActivity extends FragmentActivity{

	private ProgressDialog pDialog;

	private static Twitter twitter;
	private static RequestToken requestToken;

	private static SharedPreferences mSharedPreferences;

	private EditText mShareEditText;
	private TextView userName;
	private View loginLayout;
	private View shareLayout;

	Bundle bundle;
	String twitterSearch="";

	Button btnLogin;
	Button btnShare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle=getIntent().getExtras();
		try{
			twitterSearch=bundle.getString(Constants.TWITTER_SEARCH_KEY);
		}catch(Exception e){
			e.printStackTrace();
		}

		/* Disable the detection of every problem in thread policy */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		/* Setting activity layout file */
		setContentView(R.layout.twitter_layout);

		loginLayout = (LinearLayout) findViewById(R.id.login_layout);
		shareLayout = (LinearLayout) findViewById(R.id.share_layout);
		mShareEditText = (EditText) findViewById(R.id.share_text);
		userName = (TextView) findViewById(R.id.tv_twitter_username);

		/* register button click listeners */
		btnLogin=(Button)findViewById(R.id.btn_login);
		btnShare=(Button)findViewById(R.id.btn_share);

		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loginToTwitter();
			}
		});

		//Twitter update twitter status.
		btnShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String status = mShareEditText.getText().toString();

				if (status.trim().length() > 0) {
					new updateTwitterStatus().execute(status);
				} else {
					Toast.makeText(getApplicationContext(), "Message is empty!!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		/* Check if required twitter keys are set */
		if (TextUtils.isEmpty(Constants.consumerKey) || TextUtils.isEmpty(Constants.consumerSecret)) {
			Toast.makeText(this, "Twitter key and secret not configured",
					Toast.LENGTH_SHORT).show();
			return;
		}

		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(Constants.PREF_NAME, 0);

		boolean isLoggedIn = mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);

		/*  if already logged in, then hide login layout and show share layout */
		if (isLoggedIn) {
			loginLayout.setVisibility(View.GONE);
			shareLayout.setVisibility(View.VISIBLE);
			mShareEditText.setText(Constants.TWITTER_FILMSYNC_TEXT+" "+twitterSearch+"\n");
			mShareEditText.setSelection(mShareEditText.getText().toString().length());
			String username = mSharedPreferences.getString(Constants.PREF_USER_NAME, "");
			userName.setText("Hi "+username);

		} else {
			loginLayout.setVisibility(View.VISIBLE);
			shareLayout.setVisibility(View.GONE);

			Uri uri = getIntent().getData();

			if (uri != null && uri.toString().startsWith(Constants.callbackUrl)) {

				String verifier = uri.getQueryParameter(Constants.oAuthVerifier);

				try {

					/* Getting oAuth authentication token */
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					/* Getting user id form access token */
					long userID = accessToken.getUserId();
					final User user = twitter.showUser(userID);
					final String username = user.getName();

					/* save updated token */
					saveTwitterInfo(accessToken);

					loginLayout.setVisibility(View.GONE);
					shareLayout.setVisibility(View.VISIBLE);
					userName.setText("Hi "+username);

				} catch (Exception e) {
					Log.e("Failed to login Twitter!!", e.getMessage());
				}
			}

		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * Saving user information, after user is authenticated for the first time.
	 * You don't need to show user to login, until user has a valid access toen
	 */
	private void saveTwitterInfo(AccessToken accessToken) {

		long userID = accessToken.getUserId();

		User user;
		try {
			user = twitter.showUser(userID);

			String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
			Editor e = mSharedPreferences.edit();
			e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
			e.putString(Constants.PREF_USER_NAME, username);
			e.commit();

		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Function to authorize the application to use twitter account details.
	 */
	private void loginToTwitter() {
		boolean isLoggedIn = mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);

		if (!isLoggedIn) {
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Constants.consumerKey);
			builder.setOAuthConsumerSecret(Constants.consumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(Constants.callbackUrl);

				/**
				 *  Loading twitter login page on webview for authorization 
				 *  Once authorized, results are received at onActivityResult
				 *  */
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
				startActivityForResult(intent, Constants.WEBVIEW_REQUEST_CODE);

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {

			loginLayout.setVisibility(View.GONE);
			shareLayout.setVisibility(View.VISIBLE);
		}
	}

	//Executed after twitter login activity finished.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) { //twitter login activity result is okay.
			
			String verifier = data.getExtras().getString(Constants.oAuthVerifier);
			try {
				AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

				long userID = accessToken.getUserId();
				final User user = twitter.showUser(userID);
				String username = user.getName();

				saveTwitterInfo(accessToken);

				loginLayout.setVisibility(View.GONE);
				shareLayout.setVisibility(View.VISIBLE);

				mShareEditText.setText(Constants.TWITTER_FILMSYNC_TEXT+" "+twitterSearch+"\n");
				mShareEditText.setSelection(mShareEditText.getText().toString().length());
				userName.setText("Hi "+username);

			} catch (Exception e) {
				Log.e("Twitter Login Failed", e.getMessage()+" ");
			}
		}else{//twitter login activity result is not okay.
			
			Toast.makeText(getApplicationContext(), "Login failed!!!", Toast.LENGTH_SHORT).show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * Class to update the twitter status(post to twitter).
	 * @author fingent
	 *
	 */
	class updateTwitterStatus extends AsyncTask<String, String, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(TwitterActivity.this);
			pDialog.setMessage("Posting to twitter...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		//Posting to twitter.
		protected Void doInBackground(String... args) {

			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(Constants.consumerKey);
				builder.setOAuthConsumerSecret(Constants.consumerSecret);

				// Access Token
				String access_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, "");

				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

				// Update status
				StatusUpdate statusUpdate = new StatusUpdate(status);

				twitter4j.Status response = twitter.updateStatus(statusUpdate);

				Log.d("Status", response.getText());

			} catch (TwitterException e) {
				Log.d("Failed to post!", e.getMessage());
			}
			return null;
		}

		//execute after posted to twitter.
		@Override
		protected void onPostExecute(Void result) {

			/* Dismiss the progress dialog after sharing */
			pDialog.dismiss();

			Toast.makeText(TwitterActivity.this, "Posted to Twitter!", Toast.LENGTH_SHORT).show();

			// Clearing EditText field
			mShareEditText.setText(Constants.TWITTER_FILMSYNC_TEXT+" "+twitterSearch+"\n");
			mShareEditText.setSelection(mShareEditText.getText().toString().length());
			
			//finish the activity after post.
			finishActivity();
		}
	}

	/**
	 * Function to finish the activity.
	 */
	public void finishActivity(){
		finish();
	}
}
