package com.filmsync;

import java.io.File;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;


/**
 * Class to show filmsync main view. 
 * @author fingent
 *
 */
public class FilmSyncMainScreen extends FragmentActivity {
	static Button btnProjectList;
	static Button btnHelp;
	static TextView tvHelp;
	static TextView tvPlist;
	static Button btnScan;

	final ScanFragment mainFragment = new ScanFragment();

	public static final String CONTROLLER_FRAGMENT_TAG="CONTROLLER_FRAGMENT";
	static boolean isScanBtnSelected=true;// Scan button selected or not
	static boolean isProjectListBtnSelected=false;// project list button selected or not
	static boolean isHelpBtnSelected=false; //Help button selected or not

	FilmSyncAnalytics filmSyncAnalytics;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_container_layout);



		//initialize the UI components. 
		btnProjectList=(Button) findViewById(R.id.btnProjectList);
		btnScan=(Button) findViewById(R.id.btnScan);
		btnHelp=(Button) findViewById(R.id.btnHelp);
		tvHelp=(TextView) findViewById(R.id.tvHelp);
		tvPlist=(TextView) findViewById(R.id.tvPlist);

		setButtonBG();

		if (savedInstanceState == null) {

			//show scan fragment view as a first view
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.containerFrame, mainFragment, CONTROLLER_FRAGMENT_TAG);
			transaction.commit();
		}

		/* Disable the detection of every problem in thread policy */
		if (android.os.Build.VERSION.SDK_INT > 11) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}


		//Shows the project expandable list view.
		btnProjectList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//set button selection flags
				isScanBtnSelected=false;
				isProjectListBtnSelected=true;
				isHelpBtnSelected=false;
				setButtonBG();

				//Present project list fragment.
				ProjectDetailsFragment fragment = new ProjectDetailsFragment();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.containerFrame, fragment);
				transaction.commit();
			}
		});

		//Shows the scanning audio view.
		btnScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//set button selection flags
				isScanBtnSelected=true;
				isProjectListBtnSelected=false;
				isHelpBtnSelected=false;
				setButtonBG();

				//present scan fragment.
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

				if(!mainFragment.isVisible()){//Scan fragment is not already visible.

					//present scan fragment
					transaction.replace(R.id.containerFrame, mainFragment, CONTROLLER_FRAGMENT_TAG);
					transaction.commit();

				}else{

					//reset the views
					mainFragment.setViews();
				}

			}
		});

		//Shows the help screen view.
		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				//set button flags.
				isScanBtnSelected=false;
				isProjectListBtnSelected=false;
				isHelpBtnSelected=true;
				setButtonBG();

				//Present the help fragment
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				HelpSlidePagerFragment fragment = new HelpSlidePagerFragment();
				transaction.replace(R.id.containerFrame, fragment);
				transaction.commit();
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * function to set button background and text color.
	 */
	public static void setButtonBG(){
		if(isScanBtnSelected){ //Scan button selected
			btnScan.setBackgroundResource(R.drawable.scan_btn);
		}else{ //scan button not selected
			btnScan.setBackgroundResource(R.drawable.scan_btn);
		}

		if(isProjectListBtnSelected){ //Project button selected
			btnProjectList.setBackgroundResource(R.drawable.plist_btn);
			tvPlist.setTextColor(Color.rgb(142, 213, 255));

		}else{ //Project button not selected
			btnProjectList.setBackgroundResource(R.drawable.plist_normal_btn);
			tvPlist.setTextColor(Color.WHITE);
		}

		if(isHelpBtnSelected){ //Help button selected
			btnHelp.setBackgroundResource(R.drawable.help_btn);
			tvHelp.setTextColor(Color.rgb(142, 213, 255));
		}else{ //Help button not selected
			btnHelp.setBackgroundResource(R.drawable.help_normal_btn);
			tvHelp.setTextColor(Color.WHITE);
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		trimCache(getApplicationContext());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		trimCache(getApplicationContext());
	}

	/**
	 * Function to clear the cache memory.
	 * @param context
	 */
	public void trimCache(Context context)
	{
		try
		{
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory())
			{
				deleteDir(dir);
			}
			context.deleteDatabase("webview.db");
			context.deleteDatabase("webviewCache.db");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// TODO: handle exception
		}
		System.out.println("Cleared CACHE");
	}
	
	/**
	 * Function to delete file.
	 * @param file - file to delete. 
	 * @return
	 */
	public static boolean deleteDir(File file) {
		if (file != null && file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return file.delete();
	}
}
