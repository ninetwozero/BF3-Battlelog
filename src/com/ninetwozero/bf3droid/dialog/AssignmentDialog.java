package com.ninetwozero.bf3droid.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.jsonmodel.assignments.Criteria;
import com.ninetwozero.bf3droid.jsonmodel.assignments.Mission;
import com.ninetwozero.bf3droid.util.AssignmentsMap;
import com.ninetwozero.bf3droid.util.DateTimeFormatter;

import java.util.List;

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
        builder.setIcon(getIcon());
        builder.setTitle(AssignmentsMap.get(mission.getMissionId()));
        builder.setView(dialog);
        builder.setCancelable(true);

        ImageView imageAssignment = ((ImageView) dialog.findViewById(R.id.image_assignment));
        imageAssignment.setImageResource(AssignmentsMap.assignmentDrawable(mission.getCode()));

        imageAssignment.setClickable(false);

        showCriteria(layoutInflater, wrapObjectives);

        LinearLayout wrapRewards = (LinearLayout) dialog.findViewById(R.id.wrap_rewards);
        List<Integer> rewards = AssignmentsMap.rewardResources(mission.getCode());
        showRewards(wrapRewards, rewards);

        return builder.create();
    }

    private void showCriteria(LayoutInflater layoutInflater, LinearLayout wrapObjectives) {
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
    }

    private void showRewards(LinearLayout wrapRewards, List<Integer> rewards) {
        for(int i = 0; i < rewards.size(); i += 2){
            TextView reward = new TextView(getActivity().getApplicationContext());
            reward.setText(rewards.get(i+1));
            reward.setTextColor(getResources().getColor(R.color.black));
            reward.setGravity(Gravity.CENTER_HORIZONTAL);
            reward.setCompoundDrawablesWithIntrinsicBounds(0, rewards.get(i), 0, 0);
            wrapRewards.addView(reward);
        }
    }

    private int getIcon() {
        return mission.getCompletion() < 100 ? R.drawable.lock : R.drawable.unlocked;
    }

    private String timeValues(double actual, double completion) {
        return new StringBuilder(DateTimeFormatter.timeString((int) actual))
                .append("/")
                .append(DateTimeFormatter.timeString((int) completion))
                .toString();
    }
}
