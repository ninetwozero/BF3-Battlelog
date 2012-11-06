package com.ninetwozero.battlelog.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.jsonmodel.assignments.Criteria;
import com.ninetwozero.battlelog.jsonmodel.assignments.Mission;
import com.ninetwozero.battlelog.util.AssignmentsMap;
import com.ninetwozero.battlelog.util.TimeFormatter;

public class AssignmentDialog extends DialogFragment {

    private Mission mission;

    public static AssignmentDialog newInstance(Mission mission) {
        AssignmentDialog dialog = new AssignmentDialog(mission);
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        return dialog;
    }

    private AssignmentDialog(Mission mission) {
        this.mission = mission;
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
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialog = layoutInflater.inflate(R.layout.popup_dialog_view, null);
        LinearLayout wrapObjectives = (LinearLayout) dialog.findViewById(R.id.wrap_objectives);

        builder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }

        });
        builder.setIcon(R.drawable.lock);
        builder.setTitle(AssignmentsMap.get(mission.getMissionId()));
        builder.setView(dialog);
        builder.setCancelable(true);

        ImageView imageAssignment = ((ImageView) dialog.findViewById(R.id.image_assignment));
        imageAssignment.setImageResource(AssignmentsMap.assignmentDrawable(mission.getCode()));

        imageAssignment.setClickable(false);

        for (Criteria criteria : mission.getCriterias()) {

            View v = layoutInflater.inflate(R.layout.list_item_assignment_popup, null);

            ((TextView) v.findViewById(R.id.text_obj_title)).setText(AssignmentsMap.getCriteria(criteria.getDescriptionId()));
            if (criteria.getUnit().equalsIgnoreCase("time_hours")) {
                ((TextView) v.findViewById(R.id.text_obj_values)).setText(timeValues(criteria.getActualValue(), criteria.getCompletionValue()));
            } else {
                ((TextView) v.findViewById(R.id.text_obj_values)).setText((int) criteria.getActualValue() + "/" + (int) criteria.getCompletionValue());
            }
            wrapObjectives.addView(v);
        }

        ((ImageView) dialog.findViewById(R.id.image_reward)).setImageResource(AssignmentsMap.rewardDrawable(mission.getCode()));
        ((TextView) dialog.findViewById(R.id.text_rew_name)).setText(AssignmentsMap.rewardLabel(mission.getCode()));

        return builder.create();
    }

    private String timeValues(double actual, double completion) {
        return new StringBuilder()
                .append(TimeFormatter.timeString((int) actual))
                .append("/")
                .append(TimeFormatter.timeString((int) completion))
                .toString();
    }
}
