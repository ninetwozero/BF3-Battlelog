package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3FragmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.datatype.Statistics;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.table.RankProgress;

import java.util.Map;
import java.util.Set;

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
        } else {
            guestStats = pos;
            fillView();
        }
    }

    private void fillView() {
        closeLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        populateRanks(userStats.getRankProgress(), guestStats.getRankProgress());
        addStats(userStats.getPersonaStats(), guestStats.getPersonaStats());
        addStats(userStats.getScoreStats(), guestStats.getScoreStats());
    }

    private void populateRanks(RankProgress userRank, RankProgress guestRank) {
        ((TextView) findViewById(R.id.user_name)).setText(userRank.getPersonaName());
        ((TextView) findViewById(R.id.guest_name)).setText(guestRank.getPersonaName());

        ((TextView) findViewById(R.id.user_rank)).setText(fromResource(userRank.getRank()));
        ((TextView) findViewById(R.id.guest_rank)).setText(fromResource(guestRank.getRank()));

        ((TextView) findViewById(R.id.user_score)).setText(String.valueOf(userRank.getScore()));
        ((TextView) findViewById(R.id.guest_score)).setText(String.valueOf(guestRank.getScore()));
    }

    private void addStats(Map<String, Statistics> user, Map<String, Statistics> guest) {
        TableLayout table = (TableLayout) findViewById(R.id.compare_soldiers_stats);
        Set<String> keys = user.keySet();
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
}
