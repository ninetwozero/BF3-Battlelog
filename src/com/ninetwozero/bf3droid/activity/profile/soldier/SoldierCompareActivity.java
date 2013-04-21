package com.ninetwozero.bf3droid.activity.profile.soldier;

import android.os.Bundle;
import android.widget.TextView;

import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3FragmentActivity;
import com.ninetwozero.bf3droid.activity.profile.soldier.restorer.PersonaStatisticsRestorer;
import com.ninetwozero.bf3droid.datatype.PersonaOverviewStatistics;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.table.RankProgress;

public class SoldierCompareActivity extends Bf3FragmentActivity implements ProfileStatsLoader.Callback {
    private RankProgress userRank;
    private RankProgress guestRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        PersonaOverviewStatistics user = new PersonaStatisticsRestorer(this, User.USER).fetch();
        if (!user.isEmpty()) {
            setUserFields(user);
        } else {
            startLoadingDialog(SoldierCompareActivity.class.getSimpleName());
            new ProfileStatsLoader(this, getApplicationContext(), User.USER, getSupportLoaderManager()).restart();
        }
    }

    private void getGuestStats() {
        PersonaOverviewStatistics guest = new PersonaStatisticsRestorer(this, User.GUEST).fetch();
        if(!guest.isEmpty()){
            setGuestFields(guest);
        } else{
            startLoadingDialog(SoldierCompareActivity.class.getSimpleName());
            new ProfileStatsLoader(this, getApplicationContext(), User.GUEST, getSupportLoaderManager()).restart();
        }
    }

    @Override
    public void onLoadFinished(PersonaOverviewStatistics pos) {
        closeLoadingDialog(SoldierCompareActivity.class.getSimpleName());
        if(pos.getUserType().equals(User.USER)){
            setUserFields(pos);
        } else {
            setGuestFields(pos);
        }
    }

    private void populateView() {
        populateRanks();
    }

    private void populateRanks() {
        ((TextView) findViewById(R.id.user_name)).setText(selectedUserPersona(User.USER));
        ((TextView) findViewById(R.id.guest_name)).setText(selectedUserPersona(User.GUEST));

        ((TextView) findViewById(R.id.user_rank)).setText(fromResource(userRank.getRank()));
        ((TextView) findViewById(R.id.guest_rank)).setText(fromResource(guestRank.getRank()));

        ((TextView) findViewById(R.id.user_score)).setText(String.valueOf(userRank.getScore()));
        ((TextView) findViewById(R.id.guest_score)).setText(String.valueOf(guestRank.getScore()));
    }

    private void setUserFields(PersonaOverviewStatistics user) {
        userRank = user.getRankProgress();
        getGuestStats();
    }

    private void setGuestFields(PersonaOverviewStatistics guest) {
        guestRank = guest.getRankProgress();
        populateView();
    }

    private String selectedUserPersona(String userType) {
        return BF3Droid.getUserBy(userType).selectedPersona().getPersonaName();
    }

    private String fromResource(int rank) {
        return getResources().getStringArray(R.array.rank)[rank];
    }
}
