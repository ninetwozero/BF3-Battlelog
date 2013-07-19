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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.activity.Bf3Fragment;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonScore;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonStat;
import com.ninetwozero.bf3droid.jsonmodel.platoon.PlatoonTopPlayer;
import com.ninetwozero.bf3droid.jsonmodel.soldierstats.User;
import com.ninetwozero.bf3droid.provider.BusProvider;
import com.ninetwozero.bf3droid.util.ImageLoader;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlatoonStatsFragment extends Bf3Fragment {
    private Context context;
    private LayoutInflater layoutInflater;
    private PlatoonStat platoonStat;
    private final List<Integer> GENERAL_ROW_LABELS = Arrays.asList(R.string.info_xml_average, R.string.info_xml_best, R.string.info_xml_median, R.string.info_xml_min);
    private final List<Integer> TOP_CLASSES_LABELS = Arrays.asList(R.string.platoon_stats_top_assault, R.string.platoon_stats_top_support, R.string.platoon_stats_top_recon, R.string.platoon_stats_top_engineer
            , R.string.platoon_stats_top_tank_driver, R.string.platoon_stats_top_helicopter_pilot, R.string.platoon_stats_top_ifv_driver, R.string.platoon_stats_top_jet_pilot, R.string.platoon_stats_top_aa_driver);
    private final List<String> TOP_CLASS_NAMES = Arrays.asList("assault", "support", "recon", "engineer", "tankdriver", "helipilot", "ifvdriver", "jetpilot", "aadriver");
    private Map<String, PlatoonTopPlayer> topPlayers;
    private Map<Long, User> platoonMembers;
    private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";
    private static final String BATTLELOG_SUFFIX = "?s=100&d=http%3A%2F%2Fbattlelog-cdn.battlefield.com%2Fcdnprefix%2Favatar1%2Fpublic%2Fbase%2Fshared%2Fdefault-avatar-100.png";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        layoutInflater = inflater;

        return layoutInflater.inflate(R.layout.tab_content_platoon_stats, container, false);
    }

    @Override
    public void reload() {
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void platoonStats(PlatoonStat platoonStat) {
        this.platoonStat = platoonStat;
        show();
    }

    private void show() {
        populateGeneralStatsTable();
        populateTopPlayerTable();
    }

    private void populateGeneralStatsTable() {
        TableLayout tableGeneral = (TableLayout) getView().findViewById(R.id.table_stats_general);
        for (int i = 0; i < 4; i++) {
            TableRow tableRowLabels = getLabelRow(i);
            tableGeneral.addView(tableRowLabels);
            TableRow tableRowScores = getScoreRow(i);
            tableGeneral.addView(tableRowScores);
        }
    }

    private TableRow getLabelRow(int index) {
        return createLabelRow(GENERAL_ROW_LABELS.get(index));
    }

    private TableRow getScoreRow(int index) {
        PlatoonScore scorePerMin = platoonStat.getMemberStats().getGeneralStats().get("scorePerMinute");
        PlatoonScore rank = platoonStat.getMemberStats().getGeneralStats().get("rank");
        PlatoonScore killDeath = platoonStat.getMemberStats().getGeneralStats().get("kd");
        TableRow row;
        switch (index) {
            case 0:
                row = createGeneralStatsRow(scorePerMin.getAverageScore(), rank.getAverageScore(), killDeath.getAverageScore());
                break;
            case 1:
                row = createGeneralStatsRow(scorePerMin.getBestScore(), rank.getBestScore(), killDeath.getBestScore());
                break;
            case 2:
                row = createGeneralStatsRow(scorePerMin.getMedianScore(), rank.getMedianScore(), killDeath.getMedianScore());
                break;
            case 3:
                row = createGeneralStatsRow(scorePerMin.getMinScore(), rank.getMinScore(), killDeath.getMinScore());
                break;
            default:
                row = new TableRow(getActivity().getApplicationContext());
        }
        return row;
    }

    private TableRow createLabelRow(int resourceId) {
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.platoon_general_stats_table_row_labels, null);
        ((TextView) row.findViewById(R.id.score_label_1)).setText(resourceId);
        ((TextView) row.findViewById(R.id.score_label_2)).setText(resourceId);
        ((TextView) row.findViewById(R.id.score_label_3)).setText(resourceId);
        return row;
    }

    private TableRow createGeneralStatsRow(double score1, double score2, double score3) {
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.platoon_stats_table_row_scores, null);
        ((TextView) row.findViewById(R.id.score_value_1)).setText(String.valueOf((int) score1));
        ((TextView) row.findViewById(R.id.score_value_2)).setText(String.valueOf((int) score2));
        ((TextView) row.findViewById(R.id.score_value_3)).setText(String.valueOf(score3));
        return row;
    }

    private void populateTopPlayerTable() {
        TableLayout topPlayersTable = (TableLayout) getView().findViewById(R.id.table_top_players);
        topPlayers = platoonStat.getMemberStats().getTopPlayers();
        platoonMembers = platoonStat.getPlatoonMembers();
        for (int i = 0; i < TOP_CLASS_NAMES.size(); i++) {
            TableRow row = createTopPlayerRow(i);
            topPlayersTable.addView(row);
        }
    }

    private TableRow createTopPlayerRow(int index) {
        TableRow row = (TableRow) layoutInflater.inflate(R.layout.platoon_top_player_row, null);
        ImageView imageView = (ImageView) row.findViewById(R.id.top_player_avatar);
        provideImageLoader().loadImage(imageView, imagePath(gravatarId(index)));
        ((TextView) row.findViewById(R.id.top_player_class_name)).setText(TOP_CLASSES_LABELS.get(index));
        ((TextView) row.findViewById(R.id.top_player_name)).setText(topPlayerName(index));
        ((TextView) row.findViewById(R.id.top_player_score_per_minute)).setText(String.valueOf(topPlayerAt(index).getScorePerMinute()));
        return row;
    }

    private String topPlayerName(int index) {
        PlatoonTopPlayer player = topPlayerAt(index);
        return platoonMembers.get(player.getPersonaId()).getUserName();
    }

    private PlatoonTopPlayer topPlayerAt(int index) {
        return topPlayers.get(TOP_CLASS_NAMES.get(index));
    }

    private String gravatarId(int index){
        PlatoonTopPlayer player =  topPlayerAt(index);
        return platoonMembers.get(player.getPersonaId()).getGravatarMd5();
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

    private String imagePath(String gravatarId) {
        return new StringBuilder(GRAVATAR_URL).append(gravatarId).append(BATTLELOG_SUFFIX).toString();
    }

    private ImageLoader provideImageLoader() {
        Context appContext = getGontext();
        LoaderSettings settings = new LoaderSettings.SettingsBuilder()
                .withDisconnectOnEveryCall(true)
                .withCacheManager(new LruBitmapCache(appContext))
                .build(appContext);
        return new ImageLoader(new ImageManager(appContext, settings));
    }

    private Context getGontext() {
        return getActivity().getApplicationContext();
    }
}
