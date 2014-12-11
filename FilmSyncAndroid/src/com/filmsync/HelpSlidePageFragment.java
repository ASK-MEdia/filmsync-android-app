package com.filmsync;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class to show help slide page.
 * @author fingent
 *
 */
public class HelpSlidePageFragment extends Fragment {

	private int icon_res_id;
	private String msg;
	private ImageView iconImg;
	private TextView webUrl;
	private TextView textContent;
	private boolean show_web_link_flag=false;
	
	/**
	 * Constructor
	 * @param icon_res_id - icon resource id to show
	 * @param msg		  - message to display.	
	 * @param show_web_link_flag - Show web site link or not
	 */
	public HelpSlidePageFragment(int icon_res_id,String msg,boolean show_web_link_flag){
		
		this.icon_res_id=icon_res_id;
		this.msg=msg;
		this.show_web_link_flag=show_web_link_flag;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.help_screen_template, container, false);
		
		//Initialize the UI components.
		iconImg=(ImageView) rootView.findViewById(R.id.img_icon);
		webUrl=(TextView) rootView.findViewById(R.id.tvWebsite_link);
		textContent=(TextView) rootView.findViewById(R.id.tvtext_content);
		
		//set image resource
		iconImg.setImageResource(icon_res_id);
		//set message 
		textContent.setText(msg);
		
		//Show web link text view only last screen.
		if(show_web_link_flag){
			webUrl.setVisibility(View.VISIBLE);
		}else{
			webUrl.setVisibility(View.GONE);
		}
		return rootView;
	}
}