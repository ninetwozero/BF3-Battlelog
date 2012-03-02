
package com.ninetwozero.battlelog.datatypes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.ninetwozero.battlelog.R;

public class PopupDialog extends Dialog {

    public PopupDialog(Context context, int theme) {

        super(context, theme);

    }

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(R.layout.popup_dialog_view);

    }

}
