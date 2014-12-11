package com.filmsync;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.filmsync.constants.Constants;

/**
 * Class to show twitter login view.
 * @author fingent
 *
 */
public class WebViewActivity extends Activity {
	
	private WebView webView;//twitter login web view.
	
	public static String EXTRA_URL = "extra_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.twitter_login_webview);
		
		setTitle("Login");

		final String url = this.getIntent().getStringExtra(EXTRA_URL);
		if (null == url) {
			Log.e("Twitter", "URL cannot be null");
			finish();
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(url);
	}


	class MyWebViewClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.contains(Constants.callbackUrl)) {
				Uri uri = Uri.parse(url);
				
				/* Sending results back */
				String verifier = uri.getQueryParameter(Constants.oAuthVerifier);
				Intent resultIntent = new Intent();
				resultIntent.putExtra(Constants.oAuthVerifier, verifier);
				setResult(RESULT_OK, resultIntent);
				
				/* closing webview */
				finish();
				return true;
			}
			return false;
		}
	}
}