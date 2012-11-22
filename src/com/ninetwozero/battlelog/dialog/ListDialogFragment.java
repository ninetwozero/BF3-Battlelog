package com.ninetwozero.battlelog.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.model.SelectedPersona;
import com.ninetwozero.battlelog.provider.BusProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListDialogFragment extends DialogFragment {

    private final Map<Long, String> data;
    private final int titleResource;
    private Long[] id;
    private String[] name;

    public static ListDialogFragment newInstance(Map<Long, String> data) {
        ListDialogFragment dialog = new ListDialogFragment(data, R.string.info_dialog_selection_generic);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    public static ListDialogFragment newInstance(Map<Long, String> data, int title) {
    	ListDialogFragment dialog = new ListDialogFragment(data, title);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;	
    }
    
    private ListDialogFragment(Map<Long, String> data, int title) {
        this.data = data;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(titleResource);
        id = id().clone();
        name = name().clone();
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        builder.setSingleChoiceItems(name, -1, new SingleChoiceListener());
        return builder.create();

    }

    private Long[] id() {
        return data.keySet().toArray(new Long[data.size()]);
    }

    private String[] name() {
        List<String> name = new ArrayList<String>();
        for (Long id : data.keySet()) {
            name.add(data.get(id));
        }
        return name.toArray(new String[data.size()]);
    }

    private class SingleChoiceListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int item) {
            Long[] ids = data.keySet().toArray(new Long[]{});
            BusProvider.getInstance().post(new SelectedPersona(ids[item], data.get(ids[item])));
            dismiss();
        }
    }
}
