package com.ninetwozero.battlelog.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.adapter.ProfileListAdapter;
import com.ninetwozero.battlelog.datatype.ProfileData;

public class ProfileListDialogFragment extends DialogFragment {

    private List<ProfileData> data;
    private int titleResource;
    private final String TAG;

    public static ProfileListDialogFragment newInstance(List<ProfileData> profiles, String tag) {
        ProfileListDialogFragment dialog = new ProfileListDialogFragment(profiles, tag, R.string.info_dialog_selection_profile);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    public static ProfileListDialogFragment newInstance(List<ProfileData> profiles, String tag, int title) {
        ProfileListDialogFragment dialog = new ProfileListDialogFragment(profiles, tag, title);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }
    
    private ProfileListDialogFragment(List<ProfileData> data, String tag, int title) {
        this.data = data;
        this.TAG = tag;
        this.titleResource = title;
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
    	LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View listViewLayout = layoutInflater.inflate(R.layout.dialog_generic_list, null);
    	ListView listView = (ListView) listViewLayout.findViewById(R.id.listView);
    	
    	// Construct the layout
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(listViewLayout);
        builder.setTitle(titleResource);
        listView.setAdapter( new ProfileListAdapter(getActivity(), data, layoutInflater) );
        listView.setOnItemClickListener(
        	new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> lv, View v, int pos, long id) {
	                OnCloseProfileListDialogListener act = (OnCloseProfileListDialogListener) getFragmentManager().findFragmentByTag(TAG);
	                act.onDialogListSelection(data.get(pos));
	                dismiss();	
				}
        	}
        		
        );
        // Fix the buttons
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        return builder.create();
    }
}
