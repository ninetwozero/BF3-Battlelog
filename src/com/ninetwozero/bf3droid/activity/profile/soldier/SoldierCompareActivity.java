package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3FragmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.datatype.SimplePersona;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.dialog.ListDialogFragment;
import com.ninetwozero.bf3droid.misc.NumberFormatter;
import com.ninetwozero.bf3droid.model.SelectedOption;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class SoldierCompareActivity extends Bf3FragmentActivity implements ProfileStatsLoader.Callback {

    private PersonaOverviewStatistics userStats;
    private PersonaOverviewStatistics guestStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        startLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        getData();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void getData() {
        PersonaOverviewStatistics user = new PersonaStatisticsRestorer(this, User.USER).fetch();
        if (!user.isEmpty()) {
            userStats = user;
            getGuestStats();
        } else {
            new ProfileStatsLoader(this, getApplicationContext(), User.USER, getSupportLoaderManager()).restart();
        }
    }

    private void getGuestStats() {
        PersonaOverviewStatistics guest = new PersonaStatisticsRestorer(this, User.GUEST).fetch();
        if (!guest.isEmpty()) {
            guestStats = guest;
            fillView();
        } else {
            new ProfileStatsLoader(this, getApplicationContext(), User.GUEST, getSupportLoaderManager()).restart();
        }
    }

    @Override
    public void onLoadFinished(PersonaOverviewStatistics pos) {
        if (pos.getUserType().equals(User.USER)) {
            userStats = pos;
            getGuestStats();
        } else {
            guestStats = pos;
            fillView();
        }
    }

    @Subscribe
    public void selectionChanged(SelectedOption selectedOption) {
        if (isChangForUser(selectedOption.getChangedGroup())) {
            BF3Droid.getUserBy(selectedOption.getChangedGroup()).selectPersona(selectedOption.getSelectedId());
            getData();
        }
    }

    private boolean isChangForUser(String change) {
        return change.equals(User.USER) || change.equals(User.GUEST);
    }

    private void fillView() {
        closeLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        populateRanks(userStats.getRankProgress(), guestStats.getRankProgress());
        addStats(userStats.getPersonaStats(), guestStats.getPersonaStats(), PersonaStatistics.PERSONA_STATISTICS, true);
        addStats(userStats.getScoreStats(), guestStats.getScoreStats(), ScoreStatistics.SCORE_STATISTICS, false);
    }

    private void populateRanks(RankProgress userRank, RankProgress guestRank) {
        setUsername(R.id.user_name, userRank, User.USER);
        setUsername(R.id.guest_name, guestRank, User.GUEST);

        ((TextView) findViewById(R.id.user_rank)).setText(fromResource(userRank.getRank()));
        ((TextView) findViewById(R.id.guest_rank)).setText(fromResource(guestRank.getRank()));

        ((TextView) findViewById(R.id.user_score)).setText(NumberFormatter.format(userRank.getScore()));
        ((TextView) findViewById(R.id.guest_score)).setText(NumberFormatter.format(guestRank.getScore()));
    }

    private void setUsername(int fieldId, RankProgress rankProgress, final String userType){
        TextView nameView = (TextView) findViewById(fieldId);
        nameView.setText(rankProgress.getPersonaName() + rankProgress.getPlatform());
        if (personasCount(userType) == 1) {
            nameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.empty_drawable, 0);
        }
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                ListDialogFragment dialog = ListDialogFragment.newInstance(personasToMap(userType), userType);
                dialog.show(manager, SoldierCompareActivity.class.getSimpleName());
            }
        });
    }

    private void addStats(Map<String, Statistics> user, Map<String, Statistics> guest, String[] keys, boolean removeViews) {
        TableLayout table = (TableLayout) findViewById(R.id.compare_soldiers_stats);
        if(removeViews){
            table.removeAllViews();
        }
        for (String key : keys) {
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.compare_item_table_row, null);
            ((TextView) row.findViewById(R.id.header)).setText(user.get(key).getTitle());
            ((TextView) row.findViewById(R.id.user_value)).setText(user.get(key).getValue());
            ((TextView) row.findViewById(R.id.guest_value)).setText(guest.get(key).getValue());
            table.addView(row);
        }
    }

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }

    private int personasCount(String userType) {
        return BF3Droid.getUserBy(userType).getPersonas().size();
    }

    private Map<Long, String> personasToMap(String userType){
        Map<Long, String> map = new HashMap<Long, String>();
        for (SimplePersona simplePersona : BF3Droid.getUserBy(userType).getPersonas()) {
            map.put(simplePersona.getPersonaId(), simplePersona.getPersonaName() + " [" + simplePersona.getPlatform() + "]");
        }
        return map;
    }
}
