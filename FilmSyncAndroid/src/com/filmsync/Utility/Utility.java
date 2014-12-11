package com.filmsync.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class to handle utility tools.
 * @author fingent
 *
 */
public class Utility {
	
	/**
	 * Function to check network connectivity available or not.
	 * @param context - context at which function called.
	 * @return true if network connectivity available, else false.
	 */
	public static boolean isInternetAvailable(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
 
          }
          return false;
    }

}
