package taygram.monshi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ir.taygram.messenger.R;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoCell;

import taygram.constant;

public class secretory extends BaseFragment {

    SharedPreferences preferences;
//    TextView txt_01, txt_02, txt_03, txt_deac, txt_state;
//    EditText ed;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();


        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
    }

    @Override
    public View createView(final Context context) {
        actionBar.setBackgroundColor(Theme.ACTION_BAR_MEDIA_PICKER_COLOR);
        actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString("autoanserr", R.string.autoanserr));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });


        preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);


        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;


        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);


//        EmptyCell emptyCell = new EmptyCell(context);
//        emptyCell.setHeight(AndroidUtilities.dp(20));
//        l.addView(emptyCell);


        final TextCheckCell textCheckCell = new TextCheckCell(context);
        textCheckCell.setTextAndCheck(LocaleController.getString("autoanserr", R.string.autoanserr), preferences.getBoolean("getAnsweringMachine", false), true);

        textCheckCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hide = preferences.getBoolean("getAnsweringMachine", false);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("getAnsweringMachine", !hide);
                editor.apply();

                textCheckCell.setChecked(!hide);

            }
        });
        l.addView(textCheckCell);

        TextInfoCell textInfoCell = new TextInfoCell(context);
        textInfoCell.setText(context.getResources().getString(R.string.helpanswering));

        l.addView(textInfoCell);

        final TextDetailSettingsCell textCell = new TextDetailSettingsCell(context);

        textCell.setMultilineDetail(true);
        textCell.setSummaryColor(R.color.primary_dark);
        String str = preferences.getString("seretory_ch", context.getString(R.string.txtanswering));

        textCell.setTextAndValue(LocaleController.getString("autoanserrText", R.string.autoanserrText), str, true);

        l.addView(textCell);
        textCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getParentActivity());
                View promptsView = li.inflate(R.layout.edit_box_dialog_secratory, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                String str = preferences.getString("seretory_ch", context.getString(R.string.txtanswering));
                builder.setView(promptsView);
                final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                userInput.setText(str);
                userInput.setHintTextColor(0xff979797);
                SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
                int defColor = themePrefs.getInt("themeColor", AndroidUtilities.defColor);
                userInput.getBackground().setColorFilter(themePrefs.getInt("dialogColor", defColor), PorterDuff.Mode.SRC_IN);
                AndroidUtilities.clearCursorDrawable(userInput);
                //builder.setMessage(LocaleController.getString("EnterName", R.string.EnterName));
                builder.setTitle(LocaleController.getString("saveautoanserrText", R.string.saveautoanserrText));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (userInput.getText().length() > 0)
                            preferences.edit().putString("seretory_ch", userInput.getText().toString()).commit();

                        String str = preferences.getString("seretory_ch", context.getString(R.string.txtanswering));

                        textCell.setTextAndValue(LocaleController.getString("autoanserrText", R.string.autoanserrText), str, true);


                    }
                });

                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
        });
        frameLayout.addView(l);
//        fragmentView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.answering, null);
//
//
//        txt_01 = (TextView) fragmentView.findViewById(R.id.text_01);
//        txt_02 = (TextView) fragmentView.findViewById(R.id.text_02);
//        txt_03 = (TextView) fragmentView.findViewById(R.id.text_password);
//        txt_deac = (TextView) fragmentView.findViewById(R.id.txt_state);
//        txt_state = (TextView) fragmentView.findViewById(R.id._txt_act_deact);
//        ed = (EditText) fragmentView.findViewById(R.id.check_01);
//        RelativeLayout ly_01 = (RelativeLayout) fragmentView.findViewById(R.id.ly_pass);

//        String str = preferences.getString("seretory_ch", context.getString(R.string.txtanswering));
//        boolean answer = preferences.getBoolean("getAnsweringMachine", false);
//        ed.setText(str);
//
//        if (answer) {
//            txt_03.setText(R.string.deactivatean);
//            txt_state.setText(R.string.activatean);
//            txt_state.setTextColor(Color.parseColor("#E800B630"));
//        } else {
//            txt_03.setText(R.string.activatean);
//            txt_state.setText(R.string.deactivatean);
//            txt_state.setTextColor(Color.parseColor("#FFCC030A"));
//        }
//
//        ly_01.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                changeState();
//            }
//        });


        return fragmentView;
    }

//    public void changeState() {
//        preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", Activity.MODE_PRIVATE);
//        boolean answer = preferences.getBoolean("getAnsweringMachine", false);
//        if (ed.getText().length() > 0)
//            preferences.edit().putString("seretory_ch", ed.getText().toString()).commit();
//
//        if (!answer) {
//            preferences.edit().putBoolean("getAnsweringMachine", true).commit();
//            txt_03.setText(R.string.deactivatean);
//            txt_state.setText(R.string.activatean);
//            txt_state.setTextColor(Color.parseColor("#E800B630"));
//            return;
//        }
//        preferences.edit().putBoolean("getAnsweringMachine", false).commit();
//        txt_03.setText(R.string.activatean);
//        txt_state.setText(R.string.deactivatean);
//        txt_state.setTextColor(Color.parseColor("#FFCC030A"));
//    }

    @Override
    public void onResume() {
        super.onResume();
        updateTheme();
        if (constant.AnalyticInitialized)
            ApplicationLoader.getInstance().trackScreenView("Voice Activity");
    }

    private void updateTheme() {
        SharedPreferences themePrefs = ApplicationLoader.applicationContext.getSharedPreferences(AndroidUtilities.THEME_PREFS, AndroidUtilities.THEME_PREFS_MODE);
        int def = themePrefs.getInt("themeColor", AndroidUtilities.defColor);
        actionBar.setBackgroundColor(themePrefs.getInt("prefHeaderColor", def));
        actionBar.setTitleColor(themePrefs.getInt("prefHeaderTitleColor", 0xffffffff));

        Drawable back = getParentActivity().getResources().getDrawable(R.drawable.ic_ab_back);
        back.setColorFilter(themePrefs.getInt("prefHeaderIconsColor", 0xffffffff), PorterDuff.Mode.MULTIPLY);
        actionBar.setBackButtonDrawable(back);
    }


}
