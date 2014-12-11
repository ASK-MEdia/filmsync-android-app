package com.filmsync;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.viewpagerindicator.CirclePageIndicator;

public class HelpSlidePagerFragment extends Fragment{

	/**
	 * The pager widget, which handles animation and allows swiping horizontally to access previous
	 * and next wizard steps.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	CirclePageIndicator mIndicator;
	
	View helpScreenImg;
	Drawable drawable;
	FilmSyncAnalytics filmSyncAnalytics;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		View v=inflater.inflate(R.layout.help_screen_slide, container, false);

		// Instantiate a ViewPager and a PagerAdapter.
		//helpScreenImg=v.findViewById(R.id.help_screem_img);
		mPager = (ViewPager) v.findViewById(R.id.pager);
		mPagerAdapter = new HelpSlidePagerAdapter(getActivity().getSupportFragmentManager());
		mPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mPager.setAdapter(mPagerAdapter);

		// ViewPager Indicator
		mIndicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		drawable=getActivity().getResources().getDrawable(R.drawable.help_img_project_list);
		
		//Google Analytics
		filmSyncAnalytics=new FilmSyncAnalytics(getActivity().getApplicationContext());
		Tracker t=filmSyncAnalytics.getTracker(FilmSyncAnalytics.TrackerName.APP_TRACKER);
        // Set screen name.
        t.setScreenName(FilmSyncAnalytics.FILMSYNC_HELP_SCREEN);
        // Send a screen view.
        t.send(new HitBuilders.AppViewBuilder().build());
		
		
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
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		//Stop Google analytics tracker.
		GoogleAnalytics.getInstance(getActivity().getApplicationContext()).reportActivityStop(getActivity());
	}
}