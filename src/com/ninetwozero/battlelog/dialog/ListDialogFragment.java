package com.ninetwozero.battlelog.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.PersonaData;

import java.util.ArrayList;
import java.util.List;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

public class ListDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private PersonaData[] personaData;
    private long[] personaId;
    private String[] personaName;
    private final String TAG;

    public static ListDialogFragment newInstance(PersonaData[] personaData, String tag) {
        ListDialogFragment dialog = new ListDialogFragment(personaData, tag);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private ListDialogFragment(PersonaData[] personaData, String tag) {
        this.personaData = personaData.clone();
        this.TAG = tag;
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

        builder.setSingleChoiceItems(personaName, -1, new SingleChoiceListener());
        return builder.create();

    }

    private long[] personaId() {
        long[] id = new long[personaData.length];
        for (int i = 0; i < personaData.length; i++) {
            id[i] = personaData[i].getId();
        }
        return id;
    }

    private String[] personaName() {
        List<String> name = new ArrayList<String>();
        for (int i = 0; i < personaData.length; i++) {
            name.add(personaData[i].getName() + personaData[i].resolvePlatformId());
        }
        return name.toArray(new String[personaData.length]);
    }

    private void updateSharedPreference(int item) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(SP_BL_PERSONA_CURRENT_ID, personaId[item]);
        editor.putInt(SP_BL_PERSONA_CURRENT_POS, item);
        editor.commit();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    private class SingleChoiceListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            updateSharedPreference(item);
            OnCloseListDialogListener act = (OnCloseListDialogListener) getFragmentManager().findFragmentByTag(TAG);
            act.onDialogListSelection();
            dismiss();
        }
    }
}