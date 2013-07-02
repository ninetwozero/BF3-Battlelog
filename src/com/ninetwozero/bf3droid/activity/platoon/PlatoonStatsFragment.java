/*
    This file is part of BF3 Droid

    BF3 Droid is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Droid is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.bf3droid.activity.platoon;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ninetwozero.bf3droid.BF3Droid;
import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.datatype.DefaultFragment;
import com.ninetwozero.bf3droid.datatype.PlatoonStats;
import com.ninetwozero.bf3droid.datatype.PlatoonStatsItem;
import com.ninetwozero.bf3droid.datatype.PlatoonTopStatsItem;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonStat;
import com.ninetwozero.bf3droid.loader.Bf3Loader;
import com.ninetwozero.bf3droid.loader.CompletedTask;
import com.ninetwozero.bf3droid.misc.PublicUtils;
import com.ninetwozero.bf3droid.model.User;
import com.ninetwozero.bf3droid.provider.UriFactory;
import com.ninetwozero.bf3droid.server.Bf3ServerCall;

import org.apache.http.client.methods.HttpGet;

import java.util.List;

public class PlatoonStatsFragment extends Bf3Fragment implements LoaderManager.LoaderCallbacks<CompletedTask> {
    private static final int PLATOON_STATS = 40;
    private static final int BATTLEFIELD_3 = 2;
    private Context context;
    private LayoutInflater layoutInflater;
    private View cacheView;
    private RelativeLayout wrapGeneral;
    private RelativeLayout scoreWrapper;
    private RelativeLayout spmWrapper;
    private RelativeLayout timeWrapper;
    private RelativeLayout topListWrapper;
    private TableLayout scoresTable;
    private TableLayout SPMTable;
    private TableLayout timeTable;
    private TableLayout topListTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        View view = layoutInflater.inflate(R.layout.tab_content_platoon_stats, container, false);
        initFragment(view);
        return view;
    }

    public void initFragment(View view) {
    }

    @Override
    public void reload() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PLATOON_STATS, new Bundle(), this);
        showUI();
    }

    private void showUI() {
        wrapGeneral = (RelativeLayout) getView().findViewById(R.id.wrap_general);
        scoreWrapper = (RelativeLayout) getView().findViewById(R.id.wrap_score);
        spmWrapper = (RelativeLayout) getView().findViewById(R.id.wrap_spm);
        timeWrapper = (RelativeLayout) getView().findViewById(R.id.wrap_time);
        topListWrapper = (RelativeLayout) getView().findViewById(R.id.wrap_toplist);
        scoresTable = (TableLayout) scoreWrapper.findViewById(R.id.tbl_stats);
        SPMTable = (TableLayout) spmWrapper.findViewById(R.id.tbl_stats);
        timeTable = (TableLayout) timeWrapper.findViewById(R.id.tbl_stats);
        topListTable = (TableLayout) topListWrapper.findViewById(R.id.tbl_stats);
    }

    public void show(PlatoonStats stats) {
        if (context != null && stats != null) {
            scoresTable.removeAllViews();
            SPMTable.removeAllViews();
            timeTable.removeAllViews();
            topListTable.removeAllViews();

            PlatoonStatsItem generalSPM = stats.getGlobalTop().get(0);
            PlatoonStatsItem generalKDR = stats.getGlobalTop().get(1);
            PlatoonStatsItem generalRank = stats.getGlobalTop().get(2);

            TableRow cacheTableRow = null;
            ((TextView) wrapGeneral.findViewById(R.id.text_average_spm)).setText(String.valueOf(generalSPM.getAvg()));
            ((TextView) wrapGeneral.findViewById(R.id.text_max_spm)).setText(String.valueOf(generalSPM.getMax()));
            ((TextView) wrapGeneral.findViewById(R.id.text_mid_spm)).setText(String.valueOf(generalSPM.getMid()));
            ((TextView) wrapGeneral.findViewById(R.id.text_min_spm)).setText(String.valueOf(generalSPM.getMin()));
            ((TextView) wrapGeneral.findViewById(R.id.text_average_rank)).setText(String.valueOf(generalRank.getAvg()));
            ((TextView) wrapGeneral.findViewById(R.id.text_max_rank)).setText(String.valueOf(generalRank.getMax()));
            ((TextView) wrapGeneral.findViewById(R.id.text_mid_rank)).setText(String.valueOf(generalRank.getMid()));
            ((TextView) wrapGeneral.findViewById(R.id.text_min_rank)).setText(String.valueOf(generalRank.getMin()));
            ((TextView) wrapGeneral.findViewById(R.id.text_average_kdr)).setText(String.valueOf(generalKDR.getDAvg()));
            ((TextView) wrapGeneral.findViewById(R.id.text_max_kdr)).setText(String.valueOf(generalKDR.getDMax()));
            ((TextView) wrapGeneral.findViewById(R.id.text_mid_kdr)).setText(String.valueOf(generalKDR.getDMid()));
            ((TextView) wrapGeneral.findViewById(R.id.text_min_kdr)).setText(String.valueOf(generalKDR.getDMin()));

            List<PlatoonTopStatsItem> topStats = stats.getTopPlayers();
            PlatoonTopStatsItem tempTopStats = null;

            int numCols = 2;
            for (int i = 0, max = topStats.size(); i < max; i++) {
                cacheView = layoutInflater.inflate(R.layout.grid_item_platoon_top_stats, null);
                if (cacheTableRow == null || (i % numCols) == 0) {
                    topListTable.addView(cacheTableRow = new TableRow(context));
                    cacheTableRow.setLayoutParams(
                            new TableRow.LayoutParams(
                                    TableLayout.LayoutParams.FILL_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT
                            )
                    );
                }
                cacheTableRow.addView(cacheView);

                tempTopStats = topStats.get(i);
                if (tempTopStats.hasProfile()) {
                    ((ImageView) cacheView.findViewById(R.id.image_avatar)).setImageBitmap(
                            BitmapFactory.decodeFile(
                                    PublicUtils.getCachePath(context) + tempTopStats.getProfile().getGravatarHash() + ".png"
                            )
                    );
                } else {
                    ((ImageView) cacheView.findViewById(R.id.image_avatar)).setImageResource(R.drawable.default_avatar);
                }

                ((TextView) cacheView.findViewById(R.id.text_label)).setText(tempTopStats.getLabel().toUpperCase());
                if (tempTopStats.hasProfile()) {
                    ((TextView) cacheView.findViewById(R.id.text_name)).setText(tempTopStats.getProfile().getUsername());
                    ((TextView) cacheView.findViewById(R.id.text_spm)).setText(String.valueOf(tempTopStats.getSPM()));
                } else {
                    ((TextView) cacheView.findViewById(R.id.text_name)).setText("N/A");
                    ((TextView) cacheView.findViewById(R.id.text_spm)).setText("0");
                }
            }
            generateTableRows(scoresTable, stats.getScores(), false);
            generateTableRows(SPMTable, stats.getSpm(), false);
            generateTableRows(timeTable, stats.getTime(), true);
        }
    }

    public void generateTableRows(TableLayout parent, List<PlatoonStatsItem> stats, boolean isTime) {
        TableRow cacheTableRow = null;
        parent.removeAllViews();

        if (stats == null) {
            parent.addView(cacheTableRow = new TableRow(context));
            cacheTableRow.setLayoutParams(
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    )
            );
            cacheTableRow.addView(cacheView = new TextView(context));
            ((TextView) cacheView).setText(R.string.info_stats_not_found);
            ((TextView) cacheView).setGravity(Gravity.CENTER);
        } else {
            int numItems = stats.size() - 1;
            int avg;

            for (int i = 0, max = (numItems + 1); i < max; i++) {
                avg = (i == 0) ? (stats.get(i).getAvg() / numItems) : stats.get(i).getAvg();

                cacheView = layoutInflater.inflate(R.layout.grid_item_platoon_stats, null);
                if (cacheTableRow == null || (i % 3) == 0) {
                    parent.addView(cacheTableRow = new TableRow(context));
                    cacheTableRow.setLayoutParams(
                            new TableRow.LayoutParams(
                                    TableRow.LayoutParams.FILL_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                            )
                    );
                }
                cacheTableRow.addView(cacheView);

                ((TextView) cacheView.findViewById(R.id.text_label)).setText(stats.get(i).getLabel().toUpperCase());
                if (isTime) {
                    ((TextView) cacheView.findViewById(R.id.text_average)).setText(PublicUtils.timeToLiteral(avg));
                    ((TextView) cacheView.findViewById(R.id.text_max)).setText(PublicUtils.timeToLiteral(stats.get(i).getMax()));
                    ((TextView) cacheView.findViewById(R.id.text_mid)).setText(PublicUtils.timeToLiteral(stats.get(i).getMid()));
                    ((TextView) cacheView.findViewById(R.id.text_min)).setText(PublicUtils.timeToLiteral(stats.get(i).getMin()));
                } else {
                    ((TextView) cacheView.findViewById(R.id.text_average)).setText(String.valueOf(avg));
                    ((TextView) cacheView.findViewById(R.id.text_max)).setText(String.valueOf(stats.get(i).getMax()));
                    ((TextView) cacheView.findViewById(R.id.text_mid)).setText(String.valueOf(stats.get(i).getMid()));
                    ((TextView) cacheView.findViewById(R.id.text_min)).setText(String.valueOf(stats.get(i).getMin()));
                }
            }
        }
    }

    public Menu prepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.option_join).setVisible(false);
        menu.findItem(R.id.option_leave).setVisible(false);
        menu.findItem(R.id.option_fans).setVisible(false);
        menu.findItem(R.id.option_invite).setVisible(false);
        menu.findItem(R.id.option_members).setVisible(false);
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_compare) {
            Toast.makeText(context, R.string.info_platoon_compare, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public Loader<CompletedTask> onCreateLoader(int i, Bundle bundle) {

        return new Bf3Loader(context, httpDataStats());
    }

    private Bf3ServerCall.HttpData httpDataStats() {
        return new Bf3ServerCall.HttpData(UriFactory.platoonMemeberStats(platoonId(), BATTLEFIELD_3, platformId()), HttpGet.METHOD_NAME, true);
    }

    @Override
    public void onLoadFinished(Loader<CompletedTask> completedTaskLoader, CompletedTask completedTask) {
        if(isTaskSuccess(completedTask.result)){
            Gson gson = new Gson();
            PlatoonStat platoonStat = gson.fromJson(completedTask.jsonObject, PlatoonStat.class);
        }
    }

    private boolean isTaskSuccess(CompletedTask.Result result) {
        return result == CompletedTask.Result.SUCCESS;
    }

    @Override
    public void onLoaderReset(Loader<CompletedTask> completedTaskLoader) {}

    private User user(){
        return BF3Droid.getUserBy(User.USER);
    }

    private long platoonId(){
        return user().selectedPlatoon().getPlatoonId();
    }

    private int platformId(){
        return user().selectedPlatoon().platformId();
    }
}
