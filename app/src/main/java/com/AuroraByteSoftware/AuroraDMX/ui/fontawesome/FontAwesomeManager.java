package com.AuroraByteSoftware.AuroraDMX.ui.fontawesome;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.text.Layout;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by udit on 13/02/16.
 *
 * Using this instead of com.joanzapata.iconify because iconify isn't maintained and doesn't have fa_file_import
 */
public class FontAwesomeManager {

    public static final String ROOT = "fonts/";
    public static final String FONTAWESOME = ROOT + "fa_regular_400_web_pro_5_5_0.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void addFAIcon(Menu menu, int menuId, FontAwesomeIcons fa, Context context) {
        TextDrawable faIcon = createIcon(fa, context);
        MenuItem menuItem = menu.findItem(menuId);
        menuItem.setIcon(faIcon);
    }

    @NonNull
    public static TextDrawable createIcon(FontAwesomeIcons fa, Context context) {
        TextDrawable faIcon = new TextDrawable(context);
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        faIcon.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        faIcon.setTypeface(FontAwesomeManager.getTypeface(context, FontAwesomeManager.FONTAWESOME));
        faIcon.setText(Character.toString(fa.character()));
        return faIcon;
    }
}