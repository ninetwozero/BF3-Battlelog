package com.ninetwozero.battlelog.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapters.FeedListAdapter;
import com.ninetwozero.battlelog.datatypes.FeedItem;
import com.ninetwozero.battlelog.datatypes.PostData;
import com.ninetwozero.battlelog.datatypes.WebsiteHandlerException;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.SessionKeeper;
import com.ninetwozero.battlelog.misc.WebsiteHandler;


public class FeedFragment extends ListFragment {

	//Attributes
	private Context context;
	private LayoutInflater layoutInflater;
	
	//Elements
	private ListView listFeed;
	private EditText fieldStatusUpdate;
	private FeedListAdapter feedListAdapter;
	private TextView feedStatusText;

	//Misc
	private ArrayList<FeedItem> feedItems;
	private String[] valueFieldsArray;
	private PostData[] postDataArray;
	private SharedPreferences sharedPreferences;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	//Set our attributes
    	context = getActivity().getApplicationContext();
    	layoutInflater = inflater;
    	sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	//Let's inflate & return the view
        View view = layoutInflater.inflate(R.layout.tab_content_dashboard_feed, container, false);
        
        //Do we have it already? If no, we init
		if( listFeed == null ) { 
			
			//Get the ListView
			listFeed = (ListView) view.findViewById( android.R.id.list ); 

			//Set the attributes
	        fieldStatusUpdate = (EditText) view.findViewById(R.id.field_status);
	        valueFieldsArray = new String[2];
	        postDataArray = new PostData[2];
	        
		}
        
		//If we don't have it defined, then we need to set it
		if( listFeed.getAdapter() == null ) {

			//Create a new FeedListAdapter
			feedListAdapter = new FeedListAdapter(context, feedItems, layoutInflater);
			listFeed.setAdapter( feedListAdapter );
			
		}
		
		return view;
		
    }

    @Override
    public void onResume() {

    	super.onResume();
    	refresh();
    	
    }
    
    @Override
    public void onActivityCreated( Bundle savedInstanceState ) {
    	
    	super.onActivityCreated( savedInstanceState );
    	registerForContextMenu(getListView());
    	
    }
    
    public void refresh() {
    	
    	//Feed refresh!
		new AsyncFeedRefresh(
			
			context,
			SessionKeeper.getProfileData().getProfileId()
				
		).execute();
    	
    }
    
	@Override
	public void onListItemClick( ListView l, View v, int pos, long id ) {

		getActivity().openContextMenu( v );
		
	}
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

    	//Get the actual menu item and tag
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	
    	//Show the menu
		if( !((FeedItem) ((View) info.targetView).getTag()).isLiked() ) {
			
			menu.add( Constants.MENU_ID_FEED, 0, 0, R.string.label_hooah);
		
		} else {
			
			menu.add( Constants.MENU_ID_FEED, 0, 0, R.string.label_unhooah);
			
		}
		menu.add( Constants.MENU_ID_FEED, 1, 0, R.string.label_single_post_view);
		menu.add( Constants.MENU_ID_FEED, 2, 0, "Goto item");
		
		return;

	}
	
	public class AsyncFeedRefresh extends AsyncTask<Void, Void, Boolean> {
        
    	//Attributes
    	private Context context;
    	private long activeProfileId;
    	
    	public AsyncFeedRefresh(Context c, long pId) {
    		
    		this.context = c;
    		this.activeProfileId = pId;
    		
    	}
    	
    	@Override
    	protected void onPreExecute() {}

		@Override
		protected Boolean doInBackground( Void... arg0 ) {
			
			try {
				
				//Get...
				feedItems = WebsiteHandler.getPublicFeed(

					context,
					sharedPreferences.getInt( Constants.SP_BL_NUM_FEED, Constants.DEFAULT_NUM_FEED ),
					activeProfileId
					
				);
				
				//...validate!
				return ( feedItems != null );
				
			} catch ( WebsiteHandlerException ex ) {
				
				ex.printStackTrace();
				return false;
				
			}

		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			
			//Fail?
			if( !result ) { 
				
				Toast.makeText( this.context, R.string.info_feed_empty, Toast.LENGTH_SHORT).show(); 
				return; 
			
			}
			
			//Update
			feedListAdapter.setItemArray( feedItems );
			
			//Get back here!
	        return;
		        
		}
		
    }
	
}