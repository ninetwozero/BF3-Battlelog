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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.ninetwozero.battlelog.R;
import com.ninetwozero.battlelog.datatype.DefaultFragment;
import com.ninetwozero.battlelog.datatype.PlatoonInformation;
import com.ninetwozero.battlelog.datatype.PlatoonStats;
import com.ninetwozero.battlelog.datatype.PlatoonStatsItem;
import com.ninetwozero.battlelog.datatype.PlatoonTopStatsItem;
import com.ninetwozero.battlelog.misc.PublicUtils;

public class PlatoonStatsFragment extends Fragment implements DefaultFragment {

    // Attributes
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

    // Misc
    private PlatoonInformation mPlatoonInformation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        // Set our attributes
        mContext = getActivity();
        mLayoutInflater = inflater;

        // Let's inflate & return the view
        View view = mLayoutInflater.inflate(R.layout.tab_content_platoon_stats,
                container, false);

        // Init
        initFragment(view);

        // Return
        return view;

    }

    public void initFragment(View view) {
    }

    public void showStats(PlatoonInformation pi) {

        // Get the activity
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Let's see what we can do
        mPlatoonInformation = pi;
        PlatoonStats pd = mPlatoonInformation.getStats();

        // Let's start drawing the... layout
        ((TextView) activity.findViewById(R.id.text_name_platoon_tab2)).setText(

                mPlatoonInformation.getName() + " [" + mPlatoonInformation.getTag() + "]"

                );

        // Do we have it??
        if (pd == null) {
            return;
        }

        // Are they null?
        if (mWrapGeneral == null) {

            // General ones
            mWrapGeneral = (RelativeLayout) activity.findViewById(R.id.wrap_general);

            // Kits & vehicles
            mWrapScore = (RelativeLayout) activity.findViewById(R.id.wrap_score);
            mWrapSPM = (RelativeLayout) activity.findViewById(R.id.wrap_spm);
            mWrapTime = (RelativeLayout) activity.findViewById(R.id.wrap_time);
            mTableScores = (TableLayout) mWrapScore.findViewById(R.id.tbl_stats);
            mTableSPM = (TableLayout) mWrapSPM.findViewById(R.id.tbl_stats);
            mTableTime = (TableLayout) mWrapTime.findViewById(R.id.tbl_stats);

            // Top list
            mWrapTopList = (RelativeLayout) activity.findViewById(R.id.wrap_toplist);
            mTableTopList = (TableLayout) mWrapTopList
                    .findViewById(R.id.tbl_stats);

        } else {

            mTableScores.removeAllViews();
            mTableSPM.removeAllViews();
            mTableTime.removeAllViews();
            mTableTopList.removeAllViews();

        }
        
        // Create a table row
        TableRow cacheTableRow = null;

        // Let's grab the different data
        PlatoonStatsItem generalSPM = pd.getGlobalTop().get(0);
        PlatoonStatsItem generalKDR = pd.getGlobalTop().get(1);
        PlatoonStatsItem generalRank = pd.getGlobalTop().get(2);

        // Set the general stats
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_spm))
                .setText(String.valueOf(generalSPM.getAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_spm))
                .setText(String.valueOf(generalSPM.getMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_spm))
                .setText(String.valueOf(generalSPM.getMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_spm))
                .setText(String.valueOf(generalSPM.getMin()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_rank))
                .setText(String.valueOf(generalRank.getAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_rank))
                .setText(String.valueOf(generalRank.getMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_rank))
                .setText(String.valueOf(generalRank.getMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_rank))
                .setText(String.valueOf(generalRank.getMin()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_average_kdr))
                .setText(String.valueOf(generalKDR.getDAvg()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_max_kdr))
                .setText(String.valueOf(generalKDR.getDMax()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_mid_kdr))
                .setText(String.valueOf(generalKDR.getDMid()));
        ((TextView) mWrapGeneral.findViewById(R.id.text_min_kdr))
                .setText(String.valueOf(generalKDR.getDMin()));

        // Top Players
        List<PlatoonTopStatsItem> topStats = pd.getTopPlayers();
        PlatoonTopStatsItem tempTopStats = null;

        // Loop over them, *one* by *one*
        int numCols = 2;
        for (int i = 0, max = topStats.size(); i < max; i++) {

            // Oh well, couldn't quite cache it could we?
            mCacheView = (RelativeLayout) mLayoutInflater.inflate(
                    R.layout.grid_item_platoon_top_stats, null);

            // Add the new TableRow
            if (cacheTableRow == null || (i % numCols) == 0) {

                mTableTopList.addView(cacheTableRow = new TableRow(mContext));
                cacheTableRow.setLayoutParams(

                        new TableRow.LayoutParams(

                                TableLayout.LayoutParams.FILL_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT

                        )

                        );

            }

            // Add the *layout* into the TableRow
            cacheTableRow.addView(mCacheView);

            // Grab *this* item
            tempTopStats = topStats.get(i);

            // Say cheese... (mister Bitmap)
            if (tempTopStats.hasProfile()) {

                ((ImageView) mCacheView.findViewById(R.id.image_avatar))
                        .setImageBitmap(

                        BitmapFactory.decodeFile(

                                PublicUtils.getCachePath(mContext)
                                        + tempTopStats.getProfile().getGravatarHash()
                                        + ".png"

                                )

                        );

            } else {

                ((ImageView) mCacheView.findViewById(R.id.image_avatar))
                        .setImageResource(R.drawable.default_avatar);

            }

            // Set the TextViews accordingly
            ((TextView) mCacheView.findViewById(R.id.text_label))
                    .setText(tempTopStats.getLabel().toUpperCase());
            if (tempTopStats.hasProfile()) {

                ((TextView) mCacheView.findViewById(R.id.text_name))
                        .setText(tempTopStats.getProfile().getUsername()
                        );
                ((TextView) mCacheView.findViewById(R.id.text_spm))
                        .setText(String.valueOf(tempTopStats.getSPM()));

            } else {

                ((TextView) mCacheView.findViewById(R.id.text_name))
                        .setText("N/A");
                ((TextView) mCacheView.findViewById(R.id.text_spm)).setText("0");

            }
        }

        // Let's generate the table rows!
        generateTableRows(mTableScores, pd.getScores(), false);
        generateTableRows(mTableSPM, pd.getSpm(), false);
        generateTableRows(mTableTime, pd.getTime(), true);

    }

    public void generateTableRows(TableLayout parent,
            List<PlatoonStatsItem> stats, boolean isTime) {

        // Create a table row && remove all the views
        TableRow cacheTableRow = null;
        parent.removeAllViews();

        // Loop over them, *one* by *one*
        if (stats == null) {

            // Create a new row
            parent.addView(cacheTableRow = new TableRow(mContext));
            cacheTableRow.setLayoutParams(

                    new TableRow.LayoutParams(

                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT

                    )

                    );

            // Add a TextView & set it up
            cacheTableRow.addView(mCacheView = new TextView(mContext));
            ((TextView) mCacheView).setText(R.string.info_stats_not_found);
            ((TextView) mCacheView).setGravity(Gravity.CENTER);

        } else {

            // The number of items (-1) as the overall is a field that shouldn't
            // be counted
            int numItems = stats.size() - 1;
            int avg;

            // Iterate over the stats
            for (int i = 0, max = (numItems + 1); i < max; i++) {

                // Set the average
                avg = (i == 0) ? (stats.get(i).getAvg() / numItems) : stats
                        .get(i).getAvg();

                // Is it null?
                mCacheView = (RelativeLayout) mLayoutInflater.inflate(
                        R.layout.grid_item_platoon_stats, null);

                // Add the new TableRow
                if (cacheTableRow == null || (i % 3) == 0) {

                    parent.addView(cacheTableRow = new TableRow(mContext));
                    cacheTableRow.setLayoutParams(

                            new TableRow.LayoutParams(

                                    TableRow.LayoutParams.FILL_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT

                            )

                            );

                }

                // Add the *layout* into the TableRow
                cacheTableRow.addView(mCacheView);

                // Set the label
                ((TextView) mCacheView.findViewById(R.id.text_label))
                        .setText(stats.get(i).getLabel().toUpperCase());

                // If (i == 0) => Overall
                if (isTime) {

                    ((TextView) mCacheView.findViewById(R.id.text_average))
                            .setText(PublicUtils.timeToLiteral(avg));
                    ((TextView) mCacheView.findViewById(R.id.text_max))
                            .setText(PublicUtils.timeToLiteral(stats.get(i)
                                    .getMax()));
                    ((TextView) mCacheView.findViewById(R.id.text_mid))
                            .setText(PublicUtils.timeToLiteral(stats.get(i)
                                    .getMid()));
                    ((TextView) mCacheView.findViewById(R.id.text_min))
                            .setText(PublicUtils.timeToLiteral(stats.get(i)
                                    .getMin()));

                } else {

                    ((TextView) mCacheView.findViewById(R.id.text_average))
                            .setText(String.valueOf(avg));
                    ((TextView) mCacheView.findViewById(R.id.text_max))
                            .setText(String.valueOf(stats.get(i).getMax()));
                    ((TextView) mCacheView.findViewById(R.id.text_mid))
                            .setText(String.valueOf(stats.get(i).getMid()));
                    ((TextView) mCacheView.findViewById(R.id.text_min))
                            .setText(String.valueOf(stats.get(i).getMin()));

                }

            }

        }

    }

    public void reload() {

        showStats(mPlatoonInformation);

    }

    public void setPlatoonInformation(PlatoonInformation p) {

        mPlatoonInformation = p;

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

            Toast.makeText(mContext, R.string.info_platoon_compare,
                    Toast.LENGTH_SHORT).show();

        }
        return false;

    }

}
