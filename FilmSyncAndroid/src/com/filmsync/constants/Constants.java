package com.filmsync.constants;

/**
 * Class to handle constants used in the project.
 * @author fingent
 *
 */

public class Constants {

	/* Shared preference keys */
	public static final String PREF_NAME = "sample_twitter_pref";
	public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	public static final String PREF_USER_NAME = "twitter_user_name";

	/* Any number for uniquely distinguish your request */
	public static final int WEBVIEW_REQUEST_CODE = 100;

	//Twitter console authentication secrets.
	public static String consumerKey = "SjWXIo0YUonKILGyz49htWG0L";
	public static String consumerSecret = "maIZ7G7WMwWo8x4qE4no8xxFRzA7FY5XnDQqukxKOAmHfbhw8i";
	public static String callbackUrl = "http://filmsync.org/";
	public static String oAuthVerifier = "oauth_verifier";
	
	//Twitter sharing tag
	public static String TWITTER_FILMSYNC_TEXT = "@FilmSyncApp";

	public static final String TWITTER_SEARCH_KEY="Twitter_Search_Key";
	
	//Base url and API secret.
	public static final String BASE_URL="http://filmsync.org";
	public static final String API_SECRET="1253698547";
}
