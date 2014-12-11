package com.filmsync;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.filmSynclib.model.Project;
import com.filmsync.adapters.ProjectListAdapter;
import com.filmsync.database.FilmSyncDBHelper;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Class to show project expandable list.
 * @author fingent
 *
 */
public class ProjectDetailsFragment extends Fragment {

	ExpandableListView explProjectDetails;//Project list
	ArrayList<Project> projectList; //project collection
	FilmSyncDBHelper dbHelper; //database handler
	ProjectListAdapter projectListAdapter;
	Bundle bundle;
	FilmSyncAnalytics filmSyncAnalytics;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v=inflater.inflate(R.layout.project_details_activity, container, false);

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels; 

		//Google Analytics
		filmSyncAnalytics=new FilmSyncAnalytics(getActivity().getApplicationContext());
		Tracker t=filmSyncAnalytics.getTracker(FilmSyncAnalytics.TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName(FilmSyncAnalytics.FILMSYNC_PROJECT_LIST_SCREEN);
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());

		//Instantiate project list
		explProjectDetails=(ExpandableListView) v.findViewById(R.id.eplProjectDetails);

		//Move list indicator to right.
		explProjectDetails.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));

		//Instantiate database controller
		dbHelper=new FilmSyncDBHelper(getActivity().getApplicationContext(), FilmSyncDBHelper.DB_NAME, null, 1);

		//retrieve all projects in the local data base 
		projectList=dbHelper.getAllProjects();
		projectListAdapter=new ProjectListAdapter(getActivity().getApplicationContext(), projectList);

		try{
			//set project list adapter
			explProjectDetails.setAdapter(projectListAdapter);
		}catch(NullPointerException ne){
			ne.printStackTrace();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		//Project expandable list child click listener.
		explProjectDetails.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub

				Toast.makeText(getActivity().getApplicationContext(), projectList.get(groupPosition).getCards().get(childPosition).getCardTitle(), Toast.LENGTH_SHORT).show();

				//Put selected card details in a bundle
				bundle=new Bundle();
				bundle.putString(FilmSyncDBHelper.PROJECT_ID, projectList.get(groupPosition).getProjectID());
				bundle.putString(FilmSyncDBHelper.CARD_TITLE, projectList.get(groupPosition).getCards().get(childPosition).getCardTitle());
				bundle.putString(FilmSyncDBHelper.CARD_CONTENT, projectList.get(groupPosition).getCards().get(childPosition).getCardContent());
				FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

				//initialize card details fragment
				ScanFragment fragment = new ScanFragment();
				//set argument for card fragment.
				fragment.setArguments(bundle);
				
				//show Card details fragment.
				transaction.replace(R.id.containerFrame, fragment);
				transaction.commit();
				
				//set button selection flags
				FilmSyncMainScreen.isScanBtnSelected=true;
				FilmSyncMainScreen.isProjectListBtnSelected=false;
				FilmSyncMainScreen.isHelpBtnSelected=false;
				FilmSyncMainScreen.setButtonBG();

				return false;
			}
		});
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

	/**
	 *Function to convert the dps to pixels, based on density scale
	 * @param pixels - pixel to multiply with density scale
	 * @return 
	 */
	public int GetPixelFromDips(float pixels) {
		// Get the screen's density scale 
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

}
