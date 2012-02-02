/*
	This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
*/   
package com.ninetwozero.battlelog;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.battlelog.adapters.ThreadPostListAdapter;
import com.ninetwozero.battlelog.asynctasks.AsyncPostInThread;
import com.ninetwozero.battlelog.datatypes.Board;
import com.ninetwozero.battlelog.datatypes.ShareableCookie;
import com.ninetwozero.battlelog.misc.BBCodeUtils;
import com.ninetwozero.battlelog.misc.Constants;
import com.ninetwozero.battlelog.misc.RequestHandler;
import com.ninetwozero.battlelog.misc.WebsiteHandler;

public class ForumThreadView extends ListActivity {

	//Attributes
	private final Context CONTEXT = this;
	private SharedPreferences sharedPreferences;
	private LayoutInflater layoutInflater;
	private Board.ThreadData currentThread;
	private long threadId;
	private String threadTitle;
	private int currentPage;
	private HashMap<Long, String> selectedQuotes;
	private Integer[] pageArray;
	
	//Elements
	private ListView listView;
	private SlidingDrawer slidingDrawer;
	private TextView slidingDrawerHandle;
	private OnDrawerOpenListener onDrawerOpenListener;
	private OnDrawerCloseListener onDrawerCloseListener;
	private EditText textareaContent;
	private RelativeLayout wrapButtons;
	private Button buttonPost, buttonJump, buttonPrev, buttonNext;
	
	@Override
    public void onCreate(Bundle icicle) {
    
    	//onCreate - save the instance state
    	super.onCreate(icicle);	
    	
    	//Did it get passed on?
    	if( icicle != null && icicle.containsKey( Constants.SUPER_COOKIES ) ) {
    		
    		RequestHandler.setCookies(
    				
    			(ArrayList<ShareableCookie> ) icicle.getParcelable(Constants.SUPER_COOKIES) 
    			
    		);
    	
    	}
    	
    	//Set the content view
        setContentView(R.layout.forum_thread_view);
    	
        //Prepare to tango
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        
        //Get the threadId
        threadId = getIntent().getLongExtra( "threadId", 0 );
        threadTitle = getIntent().getStringExtra( "threadTitle" );
        selectedQuotes = new HashMap<Long, String>();

        //Init
		initLayout();
		setupBottom();
	
	}        

	@Override
	public void onResume() {
		
		super.onResume();
		reloadLayout(); 
		
	}
	
	public void initLayout() {
		
		//Set the top
		if( threadTitle != null ) {
			
			( (TextView) findViewById( R.id.text_title_thread) ).setText( threadTitle );
			
		}
		
        //Get the listView
        if( listView == null ) { 
        	
        	listView = getListView(); 
        	
        	if( currentThread == null ) {
        	
        		listView.setAdapter( new ThreadPostListAdapter( this, null, layoutInflater ) );
        	
        	} else {
        		
            	listView.setAdapter( new ThreadPostListAdapter( this, currentThread.getPosts(), layoutInflater ) );
        	
        	}
    		
	    	//Let's get the button
	    	buttonJump = (Button) findViewById( R.id.button_jump );
	    	buttonPrev = (Button) findViewById( R.id.button_prev );
	    	buttonNext = (Button) findViewById( R.id.button_next );
	    	wrapButtons = (RelativeLayout) findViewById( R.id.wrap_buttons );
	    	
        	//Selector
        	listView.setDrawSelectorOnTop( false );
        	
        	//Fix the ContextMenu
        	registerForContextMenu( listView );
        	
        }
        
        //Update the title
    	this.setTitle( getTitle().toString().replace( "...", threadTitle ) );
    	
		
	}
	
    public void reloadLayout() { 
    	
    	if( currentThread == null ) {
    		
    		new AsyncGetPosts(this, listView).execute( threadId ); 
    	
    	} else {
    		
    		new AsyncGetPosts(null, listView).execute( threadId ); 
    		
    	}
    	
		currentPage = 1;
    	
    }
    
    @Override
	public boolean onCreateOptionsMenu( Menu menu ) {

    	//Inflate!!
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.option_forumthreadview, menu );		
		return super.onCreateOptionsMenu( menu );
	
    }
    
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		//Let's act!
		if( item.getItemId() == R.id.option_reload ) {
	
			this.reloadLayout();
			
		} else if( item.getItemId() == R.id.option_search ) {
			
			startActivity( new Intent(this, ForumSearchView.class ) );
			
		} else if( item.getItemId() == R.id.option_back ) {
			
			this.finish();
			
		} else if( item.getItemId() == R.id.option_page ) {
			
			
			generateDialogPage( this ).show();
			
		}
		
		// Return true yo
		return true;

	}
	
    @Override
    public void onConfigurationChanged(Configuration newConfig){        
        super.onConfigurationChanged(newConfig);
    }  
    
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(Constants.SUPER_COOKIES, RequestHandler.getCookies());
	
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int p, long id) { openContextMenu( v ); }

	private class AsyncGetPosts extends AsyncTask<Long, Void, Boolean>{

		//Attributes
		private Context context;
		private ProgressDialog progressDialog;
		private ListView list;
		
		//Construct
		public AsyncGetPosts(Context c, ListView l) {
						
			context = c;
			list = l;
			
		}
		
		@Override
		protected void onPreExecute() {

			if( context != null ) {
				
				progressDialog = new ProgressDialog(this.context);
				progressDialog.setTitle( R.string.general_wait );
				progressDialog.setMessage( "Downloading the posts..." );
				progressDialog.show();
			
			}	
				
		}
		
		@Override
		protected Boolean doInBackground( Long... arg0 ) {

			try {
				
				currentThread = WebsiteHandler.getPostsForThread( arg0[0] );
				return ( currentThread != null );
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return false;
				
			}
			
		}
		
		@Override
		protected void onPostExecute(Boolean results) {
		
			if( context != null && this.progressDialog != null ) { 
					
				this.progressDialog.dismiss();
			
			}
			
			if( results ) { 

				if( list.getAdapter() != null ) { 
				
					( (ThreadPostListAdapter) list.getAdapter() ).set( currentThread.getPosts() ); 

				}
				
				if( context != null ) {

			    	//Update the title
			    	((Activity) context).setTitle( ((Activity) context).getTitle().toString().replace( threadTitle, currentThread.getTitle() ) );
			    	((TextView)((Activity) context).findViewById( R.id.text_title_thread )).setText( currentThread.getTitle() );
				
			    	if( currentThread.getNumPages() > 1 ) { 
						
						wrapButtons.setVisibility( View.VISIBLE );
						buttonJump.setText( getString( R.string.info_xml_feed_button_jump ) );
						buttonPrev.setEnabled(false);
						buttonNext.setEnabled(true);
						buttonJump.setEnabled(true);
					
			    	} else {
						
						wrapButtons.setVisibility( View.GONE );
						buttonPrev.setEnabled(false);
						buttonNext.setEnabled(false);
						buttonJump.setEnabled(false);
						
					}
			    	
				}
				
			}
			
			
		}
	
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
	
	   	//Show the menu
		menu.add( 0, 0, 0, "View profile" );
		menu.add( 0, 1, 0, "Quote post" );
		menu.add( 0, 2, 0, "Report post" );
	
		//RETURN
		return;
	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	
		//Declare...
		AdapterView.AdapterContextMenuInfo info;
		
		//Let's try to get some menu information via a try/catch
		try {
			
		    info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		} catch (ClassCastException e) {
		
			e.printStackTrace();
			return false;
		
		}
		
		try {
			
			//Let's get the item
			Board.PostData data = (Board.PostData) info.targetView.getTag();
			
			//Divide & conquer 
			if( item.getGroupId() == 0 ) {
				
				//REQUESTS
				switch( item.getItemId() ) {
					
					case 0:
						startActivity( new Intent(this, ProfileView.class).putExtra( "profile", data.getProfileData() ) );
						break;
						
					case 1:
						Toast.makeText( this, "Quote added to the textarea, don't edit the tag.", Toast.LENGTH_SHORT).show();
						textareaContent.setText( 

							textareaContent.getText().insert( 
									
								textareaContent.getSelectionStart(), 	
								Constants.BBCODE_TAG_QUOTE_IN.replace(
								
									"{number}",
									data.getPostId() + ""
										
								).replace(
									
									"{username}",
									data.getProfileData().getAccountName()
									
								)

							)
								
						);
						selectedQuotes.put( data.getPostId(), data.getContent() );
						break;
					
					case 2:
						Toast.makeText(this, "Report post #" + data.getPostId(), Toast.LENGTH_SHORT).show();
						break;
						
					default:
						Toast.makeText(this, "Unsupported option", Toast.LENGTH_SHORT).show();
						break;
					
				}
				
			}
			
		} catch( Exception ex ) {
		
			ex.printStackTrace();
			return false;
			
		}
	
		return true;
	
	}
	
	//Define the SlidingDrawer
	public void setupBottom() {
		
		if( slidingDrawer == null ) {
	
			slidingDrawer = (SlidingDrawer) findViewById( R.id.post_slider);
			slidingDrawerHandle = (TextView) findViewById( R.id.post_slide_handle_text );
			
			//Set the drawer listeners
			onDrawerCloseListener = new OnDrawerCloseListener() {
	
				@Override
				public void onDrawerClosed() { slidingDrawer.setClickable( false ); }
			
			};
			onDrawerOpenListener = new OnDrawerOpenListener() { 
			
				@Override 
				public void onDrawerOpened() { slidingDrawer.setClickable( true ); } 
				
			};
			
			//Attach the listeners
			slidingDrawer.setOnDrawerOpenListener( onDrawerOpenListener );
			slidingDrawer.setOnDrawerCloseListener( onDrawerCloseListener );
			
			//Grab the field + button for further reference!
			textareaContent = (EditText) findViewById( R.id.textarea_content );
			buttonPost = (Button) findViewById( R.id.button_new );
			
		}
		
	}
	
	public void onPostSubmit(View v) {
	
		//Let's get the content
		String content = textareaContent.getText().toString();
		
		//Parse for the BBCODE!
		content = BBCodeUtils.toBBCode( content, selectedQuotes );
		
		//Ready... set... go!
		new AsyncPostInThread(this, threadId).execute( content, sharedPreferences.getString( Constants.SP_BL_CHECKSUM, "" ) );
		
	}
	
	public void resetPostFields() {
		
		//Reset
		textareaContent.setText( "" );
		selectedQuotes.clear();
		
	}
	
	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {

		// Hotkeys
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
		
			if( slidingDrawer.isOpened() ) {
			
				slidingDrawer.animateClose();
				
			} else {
				
				((Activity)this).finish();
			
			}	
			return true;
			
		}
		return super.onKeyDown( keyCode, event );
	
	}
	
	public void onClick( View v ) {
		
		if( v.getId() == R.id.button_next ) {

			//Increment
			currentPage++;
			
			//Let's do this
			new AsyncLoadPage(this, threadId).execute( currentPage );	
			
		} else if( v.getId() == R.id.button_prev ) {
			
			//Increment
			currentPage--;
			
			//Do the "get more"-thing
			new AsyncLoadPage(this, threadId).execute( currentPage );
			
		} else if( v.getId() == R.id.button_jump ) {
			
			generateDialogPage(this).show();
			
		}
		
	}	

	public class AsyncLoadPage extends AsyncTask<Integer, Void, Boolean> {
		
		//Attributes
		private Context context;
		private long threadId;
		private int page;
		private ArrayList<Board.PostData> posts;
		
		//Constructs
		public AsyncLoadPage( Context c, long t ) {
		
			this.context = c;
			this.threadId = t;
			
		}
		
		@Override
		protected void onPreExecute() {

			if( context instanceof ForumThreadView ) {
				
				buttonJump.setText( getString(R.string.label_downloading) );
				buttonJump.setEnabled( false );
				buttonPrev.setEnabled( false );
				buttonNext.setEnabled( false );
			
			}
			
		}
		
		@Override
		protected Boolean doInBackground( Integer... arg0 ) {
		
			try {
				
				page = arg0[0];
				posts = WebsiteHandler.getPostsForThread( this.threadId, page );
				return true;
				
			} catch( Exception ex ) {
				
				ex.printStackTrace();
				return false;
				
			}
		
		}
		
		@Override
		protected void onPostExecute( Boolean results ) {
		
			if( context instanceof ForumThreadView ) {
				
				if( results ) {

					((ThreadPostListAdapter)((ForumThreadView) context).getListView().getAdapter()).set( posts );
					buttonJump.setText( getString( R.string.info_xml_feed_button_jump ) );
					
				} else {
					
					Toast.makeText( context, R.string.info_xml_threads_more_false, Toast.LENGTH_SHORT ).show();
					
				}
				
				if( page != 1 ) { buttonPrev.setEnabled( true ); } else { buttonPrev.setEnabled( false ); } 
				if( page != currentThread.getNumPages() ) { buttonNext.setEnabled( true ); } else { buttonNext.setEnabled( false ); }
				buttonJump.setEnabled( true );			
				
				
				
			}
			

		}
	
	}
	
	public Dialog generateDialogPage(final Context context) {
		
		//Attributes
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
	    final View layout = inflater.inflate(R.layout.dialog_thread_page_select, (ViewGroup) findViewById(R.id.dialog_root));
		
	    //Set the title and the view
		builder.setTitle( R.string.info_txt_pageselect );
		builder.setView(layout);

		//How many pages do we have?
		if( pageArray == null || ( pageArray.length != currentThread.getNumPages() ) ) {
			
			pageArray = new Integer[currentThread.getNumPages()];
			for( int i = 0, max = pageArray.length; i < max; i++ ) {
				
				pageArray[i] = i;
				
			}
			
		}
		
		//Grab the elements
		final TextView textView = (TextView) layout.findViewById( R.id.text_desc );
		final EditText textPage = (EditText) layout.findViewById(R.id.text_page);
		
		//Set the text
		textView.setText( getString(R.string.info_xml_enternumber).replace("{min}", "1").replace( "{max}", currentThread.getNumPages() + "" ) );
		
		//Dialog options
		builder.setPositiveButton(
				
			android.R.string.ok, 
			new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
			      
					int page = Integer.parseInt( textPage.getText().toString() );
					if( 0 < page && page <= currentThread.getNumPages() ) {
						
						currentPage = page;
						new AsyncLoadPage(context, threadId).execute( currentPage );
			   
					} else {
						
						Toast.makeText( context, "Invalid page number.", Toast.LENGTH_SHORT).show();
						
					}
				}
				
			}
			
		);
		
		//Padding fix
		AlertDialog theDialog = builder.create();
	    theDialog.setView( layout, 0, 0, 0, 0);
	    return theDialog;
		
	}
	
}