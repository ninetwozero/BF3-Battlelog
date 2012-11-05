/*
    This file is part of BF3 Battlelog

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */

package com.ninetwozero.battlelog.activity.platoon;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.*;
import com.ninetwozero.battlelog.misc.PublicUtils;

import java.util.List;

public class PlatoonStatsFragment extends Fragment implements DefaultFragment {
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // Elements
    private View mCacheView;
    private RelativeLayout mWrapGeneral;
    private RelativeLayout mWrapScore;
    private RelativeLayout mWrapSPM;
    private RelativeLayout mWrapTime;
    private RelativeLayout mWrapTopList;
    private TableLayout mTableScores;
    private TableLayout mTableSPM;
    private TableLayout mTableTime;
    private TableLayout mTableTopList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mLayoutInflater = inflater;

        View view = mLayoutInflater.inflate(R.layout.tab_content_platoon_stats, container, false);
        initFragment(view);
        return view;
    }

    public void initFragment(View view) {}

    @Override
    public void reload() {}
    public void show(PlatoonStats stats) {
    	if (mContext == null || stats == null ) {
            return;
        }
    	Activity activity = (Activity) mContext;

        if (mWrapGeneral == null) {
            mWrapGeneral = (RelativeLayout) activity.findViewById(R.id.wrap_general);
            mWrapScore = (RelativeLayout) activity.findViewById(R.id.wrap_score);
            mWrapSPM = (RelativeLayout) activity.findViewById(R.id.wrap_spm);
            mWrapTime = (RelativeLayout) activity.findViewById(R.id.wrap_time);
            mTableScores = (TableLayout) mWrapScore.findViewById(R.id.tbl_stats);
            mTableSPM = (TableLayout) mWrapSPM.findViewById(R.id.tbl_stats);
            mTableTime = (TableLayout) mWrapTime.findViewById(R.id.tbl_stats);
            mWrapTopList = (RelativeLayout) activity.findViewById(R.id.wrap_toplist);
            mTableTopList = (TableLayout) mWrapTopList.findViewById(R.id.tbl_stats);
        } else {
            mTableScores.removeAllViews();
            mTableSPM.removeAllViews();
            mTableTime.removeAllViews();
            mTableTopList.removeAllViews();
        }
        PlatoonStatsItem generalSPM = stats.getGlobalTop().get(0);
        PlatoonStatsItem generalKDR = stats.getGlobalTop().get(1);
        PlatoonStatsItem generalRank = stats.getGlobalTop().get(2);

        TableRow cacheTableRow = null;
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_spm)).setText(String.valueOf(generalSPM.getAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_spm)).setText(String.valueOf(generalSPM.getMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_spm)).setText(String.valueOf(generalSPM.getMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_spm)).setText(String.valueOf(generalSPM.getMin()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_rank)).setText(String.valueOf(generalRank.getAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_rank)).setText(String.valueOf(generalRank.getMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_rank)).setText(String.valueOf(generalRank.getMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_rank)).setText(String.valueOf(generalRank.getMin()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_kdr)).setText(String.valueOf(generalKDR.getDAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_kdr)).setText(String.valueOf(generalKDR.getDMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_kdr)).setText(String.valueOf(generalKDR.getDMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_kdr)).setText(String.valueOf(generalKDR.getDMin()));

        List<PlatoonTopStatsItem> topStats = stats.getTopPlayers();
        PlatoonTopStatsItem tempTopStats = null;

        int numCols = 2;
        for (int i = 0, max = topStats.size(); i < max; i++) {
            mCacheView = (RelativeLayout) mLayoutInflater.inflate(R.layout.grid_item_platoon_top_stats, null);
            if (cacheTableRow == null || (i % numCols) == 0) {
                mTableTopList.addView(cacheTableRow = new TableRow(mContext));
                cacheTableRow.setLayoutParams(
                    new TableRow.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                    )
                );
            }
            cacheTableRow.addView(mCacheView);

            tempTopStats = topStats.get(i);
            if (tempTopStats.hasProfile()) {
                ((ImageView) mCacheView.findViewById(R.id.image_avatar)).setImageBitmap(
                    BitmapFactory.decodeFile(
                        PublicUtils.getCachePath(mContext)+ tempTopStats.getProfile().getGravatarHash() + ".png"
                    )
                );
            } else {
                ((ImageView) mCacheView.findViewById(R.id.image_avatar)).setImageResource(R.drawable.default_avatar);
            }

            ((TextView) mCacheView.findViewById(R.id.text_label)).setText(tempTopStats.getLabel().toUpperCase());
            if (tempTopStats.hasProfile()) {
                ((TextView) mCacheView.findViewById(R.id.text_name)).setText(tempTopStats.getProfile().getUsername());
                ((TextView) mCacheView.findViewById(R.id.text_spm)).setText(String.valueOf(tempTopStats.getSPM()));
            } else {
                ((TextView) mCacheView.findViewById(R.id.text_name)).setText("N/A");
                ((TextView) mCacheView.findViewById(R.id.text_spm)).setText("0");
            }
        }
        generateTableRows(mTableScores, stats.getScores(), false);
        generateTableRows(mTableSPM, stats.getSpm(), false);
        generateTableRows(mTableTime, stats.getTime(), true);
    }

    public void generateTableRows(TableLayout parent, List<PlatoonStatsItem> stats, boolean isTime) {
        TableRow cacheTableRow = null;
        parent.removeAllViews();

        if (stats == null) {
            parent.addView(cacheTableRow = new TableRow(mContext));
            cacheTableRow.setLayoutParams(
	            new TableRow.LayoutParams(
		            TableRow.LayoutParams.FILL_PARENT,
		            TableRow.LayoutParams.WRAP_CONTENT
	            )
            );
            cacheTableRow.addView(mCacheView = new TextView(mContext));
            ((TextView) mCacheView).setText(R.string.info_stats_not_found);
            ((TextView) mCacheView).setGravity(Gravity.CENTER);
        } else {
            int numItems = stats.size() - 1;
            int avg;
            
            for (int i = 0, max = (numItems + 1); i < max; i++) {
                avg = (i == 0) ? (stats.get(i).getAvg() / numItems) : stats.get(i).getAvg();

                mCacheView = (RelativeLayout) mLayoutInflater.inflate(R.layout.grid_item_platoon_stats, null);
                if (cacheTableRow == null || (i % 3) == 0) {
                    parent.addView(cacheTableRow = new TableRow(mContext));
                    cacheTableRow.setLayoutParams(
                        new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                        )
                    );
                }
                cacheTableRow.addView(mCacheView);

                ((TextView) mCacheView.findViewById(R.id.text_label)).setText(stats.get(i).getLabel().toUpperCase());
                if (isTime) {
                    ((TextView) mCacheView.findViewById(R.id.text_average)).setText(PublicUtils.timeToLiteral(avg));
                    ((TextView) mCacheView.findViewById(R.id.text_max)).setText(PublicUtils.timeToLiteral(stats.get(i).getMax()));
                    ((TextView) mCacheView.findViewById(R.id.text_mid)).setText(PublicUtils.timeToLiteral(stats.get(i).getMid()));
                    ((TextView) mCacheView.findViewById(R.id.text_min)).setText(PublicUtils.timeToLiteral(stats.get(i).getMin()));
                } else {
                    ((TextView) mCacheView.findViewById(R.id.text_average)).setText(String.valueOf(avg));
                    ((TextView) mCacheView.findViewById(R.id.text_max)).setText(String.valueOf(stats.get(i).getMax()));
                    ((TextView) mCacheView.findViewById(R.id.text_mid)).setText(String.valueOf(stats.get(i).getMid()));
                    ((TextView) mCacheView.findViewById(R.id.text_min)).setText(String.valueOf(stats.get(i).getMin()));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public Menu prepareOptionsMenu(Menu menu) {
        ((MenuItem) menu.findItem(R.id.option_join)).setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_leave)).setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_fans)).setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_invite)).setVisible(false);
        ((MenuItem) menu.findItem(R.id.option_members)).setVisible(false);
        return menu;
    }

    public boolean handleSelectedOption(MenuItem item) {
        if (item.getItemId() == R.id.option_compare) {
            Toast.makeText(mContext, R.string.info_platoon_compare, Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
