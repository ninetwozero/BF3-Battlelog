
package com.ninetwozero.battlelog.dialog;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.ProfileData;

public final class ProfilePersonaListDialog extends DialogFragment implements
        DialogInterface.OnClickListener {

    // Attributes
    private ProfileData mProfileData;
    private long[] mPersonaId;
    private String[] mPersonaName;

    public static ProfilePersonaListDialog newInstance(ProfileData profileData) {
        ProfilePersonaListDialog dialog = new ProfilePersonaListDialog(profileData);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private ProfilePersonaListDialog(ProfileData profileData) {
        this.mProfileData = profileData;
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
        mPersonaId = personaId().clone();
        mPersonaName = personaName().clone();
        builder.setNegativeButton("Cancel", this);
        builder.setSingleChoiceItems(

                mPersonaName, -1, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        updateSharedPreference(item);
                        dismiss();
                    }
                });
        return builder.create();

    }

    private long[] personaId() {
        long[] id = new long[mProfileData.getNumPersonas()];
        for (int i = 0; i < mProfileData.getNumPersonas(); i++) {
            id[i] = mProfileData.getPersona(i).getId();
        }
        return id;
    }

    private String[] personaName() {
        List<String> name = new ArrayList<String>();
        for (int i = 0; i < mProfileData.getNumPersonas(); i++) {
            name.add(mProfileData.getPersona(i).getName());
        }
        return name.toArray(new String[mProfileData.getNumPersonas()]);
    }

    private void updateSharedPreference(int item) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, mPersonaId[item]);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, item);
        editor.commit();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
