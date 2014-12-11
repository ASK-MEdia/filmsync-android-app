package com.filmsync.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.filmSynclib.model.Project;
import com.filmsync.R;

/**
 * Adapter Class for project expandable list.
 * @author fingent
 *
 */
public class ProjectListAdapter extends BaseExpandableListAdapter {

	ArrayList<Project> projectList;//project collection.
	Context context;
	LayoutInflater inflater;

	/**
	 * Constructor
	 * @param context - context at which list presented.
	 * @param projectList - project collection.
	 */
	public ProjectListAdapter(Context context,ArrayList<Project> projectList){
		this.projectList=projectList;
		this.context=context;
		inflater=LayoutInflater.from(context);
	}

	/**
	 * Function to get child data from project collection( card data as child data).
	 */
	 
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return projectList.get(groupPosition).getCards().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Function inflate the child view for the project expandable list( card data as child data)..
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub


		if (convertView == null) {

			//Inflate the layout.
			convertView = inflater.inflate(R.layout.list_item, null);
		}
		
		//Initialize the views
		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);

		//set card title.
		txtListChild.setText(projectList.get(groupPosition).getCards().get(childPosition).getCardTitle());
		
		return convertView;
	}

	/**
	 * Function to get child data count (card count under a project).
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return projectList.get(groupPosition).getCards().size();
	}

	/**
	 * Function to get the list header data (Project data as header).
	 */
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return projectList.get(groupPosition);
	}

	/**
	 * Function to get group count(Project count as group count).
	 */
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return projectList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Function inflate the group view for the project expandable list(Project data as header/group).
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {

			convertView = inflater.inflate(R.layout.list_header, null);
		}

		//initialize view
		TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
		TextView lbListCount=(TextView) convertView.findViewById(R.id.tvCountText);

		//Set list header test as project title.
		lblListHeader.setText(projectList.get(groupPosition).getProjectTitle());
		//Set project cards count.
		lbListCount.setText(context.getString(R.string.cards_count_txt)+" "+(projectList.get(groupPosition).getCards()).size());
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
