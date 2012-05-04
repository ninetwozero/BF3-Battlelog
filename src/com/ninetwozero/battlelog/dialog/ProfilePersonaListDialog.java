package com.ninetwozero.battlelog.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

public class ProfilePersonaListDialog extends DialogFragment implements DialogInterface.OnClickListener{
    private ProfileData profileData;
    private long[] personaId;
    private String[] personaName;

    public static ProfilePersonaListDialog newInstance(ProfileData profileData) {
        ProfilePersonaListDialog dialog = new ProfilePersonaListDialog(profileData);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private ProfilePersonaListDialog(ProfileData profileData){
        this.profileData = profileData;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.info_dialog_soldierselect);
        personaId = personaId().clone();
        personaName = personaName().clone();
        builder.setNegativeButton("Cancel", this);
        builder.setSingleChoiceItems(

                personaName, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                updateSharedPreference(item);
                dismiss();
            }
        });
        return builder.create();
    }

    private long[] personaId(){
        long[] id = new long[profileData.getNumPersonas()];
        for(int i = 0; i < profileData.getNumPersonas(); i++){
            id[i] = profileData.getPersona(i).getId();
        }
        return id;
    }

    private String[] personaName(){
        List<String> name = new ArrayList<String>();
        for(int i = 0; i < profileData.getNumPersonas(); i++){
            name.add(profileData.getPersona(i).getName());
        }
        return name.toArray(new String[profileData.getNumPersonas()]);
    }

    private void updateSharedPreference(int item){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId[item]);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, item);
        editor.commit();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
