package com.filmsync;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 3 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class HelpSlidePagerAdapter extends FragmentStatePagerAdapter {

	/**
	 * The number of pages.
	 */
	private static final int NUM_PAGES = 4;
	
	/**
	 * Help text content.
	 */
	private static String HELP_SCREEN_TEXT="FilmSync provides synced experiences that use the audio from a film, show or presentation you are watching. It is recommended that the volume of the Media device is turned up and is audible.";;
	private static String HELP_SYNC_SCREEN_TEXT="Press the large sync button here to begin syncing. Then wait for the video or broadcast to begin or get to the next synced moment. It sync based on the audio of the source so please have your volume turned up to the level you can hear it. The app will continue to stay in sync and listen for the next synced moment even when you are viewing content.";
	private static String HELP_PROJECT_LIST_TEXT="The left button provides access to any content you have already synced with.";		
	private static String HELP_WEBSITE_LINK_TEXT="For further support or information please go to";
	
	/**
	 * Constructor
	 * @param fragmentManager - Fragment manager.
	 */
	public HelpSlidePagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int position) {
		HelpSlidePageFragment pageFragment=null;

		switch(position){

		case 0: pageFragment=new HelpSlidePageFragment(R.drawable.help_normal_btn,HELP_SCREEN_TEXT,false);
		break;
		
		case 1:pageFragment=new HelpSlidePageFragment(R.drawable.scan_full_img,HELP_SYNC_SCREEN_TEXT,false);
		break;
		
		case 2:pageFragment=new HelpSlidePageFragment(R.drawable.plist_normal_btn,HELP_PROJECT_LIST_TEXT,false);
		break;
		
		case 3:pageFragment=new HelpSlidePageFragment(R.drawable.web_icon,HELP_WEBSITE_LINK_TEXT,true);
		break;
		
		default : pageFragment=new HelpSlidePageFragment(R.drawable.help_normal_btn,HELP_SCREEN_TEXT,false);
		}
		return pageFragment;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}
}