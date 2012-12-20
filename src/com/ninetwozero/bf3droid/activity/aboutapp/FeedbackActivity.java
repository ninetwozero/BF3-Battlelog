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

package com.ninetwozero.bf3droid.activity.aboutapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ninetwozero.bf3droid.R;
import com.ninetwozero.bf3droid.datatype.DefaultActivity;

public class FeedbackActivity extends Activity implements DefaultActivity {

	private WebView mWebView;
	public static final String URL = "http://ninetwozero.uservoice.com/forums/180898-bf3-battelog";
	
	@Override
	public void onCreate(final Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_feedback);

		init();
		setup();
	}

	public final void init() {
		mWebView = (WebView) findViewById(R.id.webview);
	}

	public void setup() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.setWebViewClient( new WebViewClient() );
		mWebView.loadUrl(URL);
	}

	@Override
	public void reload() {}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
