package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3FragmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.misc.NumberFormatter;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.table.PersonaStatistics;
import com.ninetwozero.bf3droid.provider.table.RankProgress;
import com.ninetwozero.bf3droid.provider.table.ScoreStatistics;

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
        startLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        getData();
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

    private void fillView() {
        closeLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        populateRanks(userStats.getRankProgress(), guestStats.getRankProgress());
        addStats(userStats.getPersonaStats(), guestStats.getPersonaStats(), PersonaStatistics.PERSONA_STATISTICS);
        addStats(userStats.getScoreStats(), guestStats.getScoreStats(), ScoreStatistics.SCORE_STATISTICS);
    }

    private void populateRanks(RankProgress userRank, RankProgress guestRank) {
        setUsername(R.id.user_name, userRank.getPersonaName(), User.USER);
        setUsername(R.id.guest_name, guestRank.getPersonaName(), User.GUEST);

        ((TextView) findViewById(R.id.user_rank)).setText(fromResource(userRank.getRank()));
        ((TextView) findViewById(R.id.guest_rank)).setText(fromResource(guestRank.getRank()));

        ((TextView) findViewById(R.id.user_score)).setText(NumberFormatter.format(userRank.getScore()));
        ((TextView) findViewById(R.id.guest_score)).setText(NumberFormatter.format(guestRank.getScore()));
    }

    private void setUsername(int fieldId, String name, String userType){
        TextView nameView = (TextView) findViewById(fieldId);
        nameView.setText(name);
        if (personasCount(userType) == 1) {
            nameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.empty_drawable, 0);
        }
    }

    private void addStats(Map<String, Statistics> user, Map<String, Statistics> guest, String[] keys) {
        TableLayout table = (TableLayout) findViewById(R.id.compare_soldiers_stats);
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
}
