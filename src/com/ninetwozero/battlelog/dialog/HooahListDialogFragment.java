package com.ninetwozero.battlelog.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.ProfileListAdapter;
import com.ninetwozero.battlelog.datatype.FeedItem;
import com.ninetwozero.battlelog.datatype.ProfileData;
import com.ninetwozero.battlelog.datatype.WebsiteHandlerException;
import com.ninetwozero.battlelog.http.FeedClient;

public class HooahListDialogFragment extends DialogFragment {

	private FeedItem mFeedItem;
	private List<ProfileData> mData;
	private final String TAG;
	private ListView mListView;

	public static HooahListDialogFragment newInstance(FeedItem feedItem,
			String tag) {
		HooahListDialogFragment dialog = new HooahListDialogFragment(feedItem,
				tag);
		Bundle bundle = new Bundle();
		dialog.setArguments(bundle);
		return dialog;
	}

	private HooahListDialogFragment(FeedItem data, String tag) {
		this.mFeedItem = data;
		this.TAG = tag;
		this.mData = new ArrayList<ProfileData>();
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(true);
		int style = DialogFragment.STYLE_NORMAL, theme = 0;
		setStyle(style, theme);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Grab the custom layout
		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listViewLayout = layoutInflater.inflate(
				R.layout.dialog_generic_list, null);
		mListView = (ListView) listViewLayout.findViewById(R.id.listView);

		// Construct the layout
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(listViewLayout);
		builder.setTitle(R.string.info_dialog_selection_profile);
		mListView.setAdapter(new ProfileListAdapter(getActivity(), mData,
				layoutInflater));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> lv, View v, int pos, long id) {
				OnCloseProfileListDialogListener act = (OnCloseProfileListDialogListener) getFragmentManager()
						.findFragmentByTag(TAG);
				act.onDialogListSelection(mData.get(pos));
				dismiss();
			}
		}

		);
		// Fix the buttons
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				// do nothing
			}
		});
		return builder.create();
	}

	@Override
	public void onResume() {
		super.onResume();
		new AsyncGetHooah(getActivity(), mFeedItem).execute();
	}

	private class AsyncGetHooah extends AsyncTask<Void, Void, Boolean> {

		// Attributes
		private final Context context;
		private FeedItem feedItem;
		private View tempView;

		public AsyncGetHooah(Context c, FeedItem item) {
			context = c;
			feedItem = item;
		}

		@Override
		protected void onPreExecute() {
			tempView = getDialog().findViewById(R.id.empty);
			tempView.setVisibility(View.VISIBLE);
			getDialog().setTitle(R.string.general_wait);
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				mData = new FeedClient(feedItem.getId(), feedItem.getType())
						.getHooahs();
				return (mData.size() > 0);
			} catch (WebsiteHandlerException ex) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {

			if (result) {
				getDialog().setTitle(R.string.info_dialog_selection_profile);
				tempView.setVisibility(View.GONE);
				((ProfileListAdapter) mListView.getAdapter()).setData(mData);
			} else {
				dismiss();
				Toast.makeText(context, "No hooahs found!", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

}
