package com.ninetwozero.battlelog.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatypes.ProfileData;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

public class ProfilePersonaListDialog extends AlertDialog {
    private ProfileData profileData;
    private long[] personaId;
    private String[] personaName;

    public ProfilePersonaListDialog(Context context, ProfileData profileData) {
        super(context);
        this.profileData = profileData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.info_dialog_soldierselect);
        personaId = personaId().clone();
        personaName = personaName().clone();

        builder.setSingleChoiceItems(

                personaName, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                updateSharedPreference(item);
            }
        });
        builder.create();

        super.onCreate(savedInstanceState);
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId[item]);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, item);
        editor.commit();
    }
}
