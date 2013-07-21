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

package com.ninetwozero.bf3droid.datatype;

import android.os.Parcel;
import android.os.Parcelable;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

public class ShareableCookie implements Parcelable {

    private String name;
    private String value;
    private String domain;

    public ShareableCookie(String n, String v, String d) {

        this.name = n;
        this.value = v;
        this.domain = d;

    }

    public ShareableCookie(Cookie cookie) {

        this.name = cookie.getName();
        this.value = cookie.getValue();
        this.domain = cookie.getDomain();

    }

    public ShareableCookie(Parcel in) {

        this.name = in.readString();
        this.value = in.readString();
        this.domain = in.readString();

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getDomain() {
        return domain;
    }

    public Cookie toCookie() {

        // Create & return
        BasicClientCookie cookie = new BasicClientCookie(this.name, this.value);
        cookie.setDomain(this.domain);
        return cookie;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.name);
        dest.writeString(this.value);
        dest.writeString(this.domain);

    }

    public static final Parcelable.Creator<ShareableCookie> CREATOR = new Parcelable.Creator<ShareableCookie>() {

        public ShareableCookie createFromParcel(Parcel in) {
            return new ShareableCookie(in);
        }

        public ShareableCookie[] newArray(int size) {
            return new ShareableCookie[size];
        }

    };

}
