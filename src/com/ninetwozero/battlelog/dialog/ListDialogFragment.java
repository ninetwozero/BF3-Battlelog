package com.ninetwozero.battlelog.dialog;

import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_ID;
import static com.ninetwozero.battlelog.misc.Constants.SP_BL_PERSONA_CURRENT_POS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.PersonaData;

public class ListDialogFragment extends DialogFragment{

    private Map<Long, String> data;
    private Long[] personaId;
    private String[] personaName;
    private final String TAG;

    public static ListDialogFragment newInstance(Map<Long, String> data, String tag) {
        ListDialogFragment dialog = new ListDialogFragment(data, tag);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private ListDialogFragment(Map<Long, String> data, String tag) {
        this.data = data;
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.setSingleChoiceItems(personaName, -1, new SingleChoiceListener());
        return builder.create();

    }

    private Long[] personaId() {
        return data.keySet().toArray(new Long[data.size()]);
    }

    private String[] personaName() {
        List<String> name = new ArrayList<String>();
        for (Long id : data.keySet()) {
            name.add(data.get(id));
        }
        return name.toArray(new String[data.size()]);
    }

    private class SingleChoiceListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            OnCloseListDialogListener act = (OnCloseListDialogListener) getFragmentManager()
                    .findFragmentByTag(TAG);
            act.onDialogListSelection(item);
            dismiss();
        }
    }
}
