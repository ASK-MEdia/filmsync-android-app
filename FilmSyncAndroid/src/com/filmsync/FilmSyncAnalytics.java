package com.filmsync;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Class to manage google analytics.
 * @author fingent
 *
 */
public class FilmSyncAnalytics extends Application {

	// The following line should be changed to include the correct property id.
	private static final String PROPERTY_ID ="UA-34218099-3";
	
	//Screen names to send to google analysis.
	static final String FILMSYNC_SYNC_SCREEN="Sync Screen";
	static final String FILMSYNC_PROJECT_LIST_SCREEN="Project List Screen";
	static final String FILMSYNC_HELP_SCREEN="Help Screen";
	
	Context context;
	
	//Logging TAG
	private static final String TAG = "FilmSync";

	public static int GENERAL_TRACKER = 0;

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();


	/**
	 * Constructor
	 * @param context - Context.
	 */
	public FilmSyncAnalytics(Context context) {
		super();
		this.context=context;
	}

	/**
	 * Function to get the google analytics tracker.
	 * @param trackerId - Tracker ID.
	 * @return Tracker object.
	 */
	synchronized Tracker getTracker(TrackerName trackerId) {
	    if (!mTrackers.containsKey(trackerId)) {

	      GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
	      Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
	          : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
	              : analytics.newTracker(R.xml.ecommerce_tracker);
	      mTrackers.put(trackerId, t);

	    }
	    return mTrackers.get(trackerId);
	  }
}