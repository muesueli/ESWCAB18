package com.hu.eswcab18;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.Map;

public class BillsActivity extends AppCompatActivity {

    private final String[][] rbTxt = new String[][]{{"EUR", "eu"}, {"USD", "us"},
            {"JPY", "jp"}, {"GBP", "gb"}, {"RUB", "ru"}, {"BRL", "br"},
            {"CAD", "ca"}, {"CNY", "cn"}, {"HKD", "hk"}, {"IDR", "id"},
            {"INR", "in"}, {"KRW", "kr"}, {"MXN", "mx"}, {"SGD", "sg"},
            {"ZAR", "sh"}, {"THB", "th"}};


    private float kursFloat;

    private String currencySymbol;
    private String[] resultat;

    private TextWatcher watcherForeign;

    private TextWatcher watcherCHF;

    TableLayout table21Tl;
    TableLayout table22Tl;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle bundle) {

        super.onCreate (bundle);
        setContentView (R.layout.swissmoney);

        try {
            Bundle extras = getIntent ().getExtras ();
            currencySymbol = extras.getString ("currencySymbol");
        } catch (Exception e) {
            errorMessage ("error", "foreign currenca invalid");
            return;
        }

        processRates (currencySymbol);

        for (int i = 0; i < rbTxt.length; i++) {
            if (rbTxt[i][0].equals (currencySymbol)) {
                final ImageView foreignFlagView = findViewById (R.id.foreignFlag);
                int imageResource = getResources ()
                        .getIdentifier ("drawable/" + rbTxt[i][1], "drawable",
                                getPackageName ());
                Drawable image = ResourcesCompat.getDrawable(getResources(), imageResource, null);
                foreignFlagView.setImageDrawable (image);
                break;
            }
        }

        TextView symbolForeign = findViewById (R.id.symbolForeign);
        symbolForeign.setText (currencySymbol);

        TextView einsCHFToForeign = findViewById (R.id.einsCHFToForeign);
        float f1 = 100.0f / kursFloat;
        String s1 = Global.formatRate (f1);
        einsCHFToForeign.setText (s1);
        System.out.println ("ECB f1  " + f1   +"/" + s1 +" kursFloat " + kursFloat +" symbol "+currencySymbol);

        TextView einsForeignToCHF = findViewById (R.id.einsForeignToCHF);
        float f2 = 100.0f * kursFloat;
        String s2 = Global.formatRate (f2);
        einsForeignToCHF.setText (s2);
        System.out.println ("ECB f2  " + f2   +"/" + s2);

        final EditText editTextForeign = findViewById (R.id.editTextForeign);
        watcherForeign = new TextWatcher () {
            @Override
            public void afterTextChanged(Editable s) {
                // Log.e("afterTextChanged", "afterTextChanged");
                exchange ("to");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Log.e("beforeTextChanged", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // Log.e("onTextChanged", "onTextChanged");
            }
        };
        editTextForeign.addTextChangedListener (watcherForeign);

        final EditText editTextCHF = findViewById (R.id.editTextCHF);
        watcherCHF = new TextWatcher () {
            @Override
            public void afterTextChanged(Editable s) {
                // Log.e("afterTextChanged", "afterTextChanged");
                exchange ("from");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Log.e("beforeTextChanged", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // Log.e("onTextChanged", "onTextChanged");
            }
        };
        editTextCHF.addTextChangedListener (watcherCHF);

        editTextForeign.setOnFocusChangeListener (new View.OnFocusChangeListener () {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextForeign.addTextChangedListener (watcherForeign);

                } else {
                    editTextForeign.removeTextChangedListener (watcherForeign);
                }
            }
        });

        editTextCHF.setOnFocusChangeListener (new View.OnFocusChangeListener () {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextCHF.addTextChangedListener (watcherCHF);

                } else {
                    editTextCHF.removeTextChangedListener (watcherCHF);
                }
            }
        });

        ToggleButton bill_coinBt = findViewById (R.id.bill_coinBt);
        bill_coinBt.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setVisibilityTables (View.GONE, View.VISIBLE);
                } else {
                    setVisibilityTables (View.VISIBLE, View.GONE);
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Global.maintainECB ();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println ("ECB BillsActivity onPause rates " + Global.exchangeRates.toString ());
    }

    private void setVisibilityTables(int visiBill, int visiCoin) {

        findViewById (R.id.tableRow11).setVisibility (visiBill);
        findViewById (R.id.tableRow12).setVisibility (visiBill);
        findViewById (R.id.tableRow13).setVisibility (visiBill);
        findViewById (R.id.tableRow14).setVisibility (visiBill);
        findViewById (R.id.tableRow15).setVisibility (visiBill);
        findViewById (R.id.tableRow16).setVisibility (visiBill);

        findViewById (R.id.tableRow21).setVisibility (visiCoin);
        findViewById (R.id.tableRow22).setVisibility (visiCoin);
        findViewById (R.id.tableRow23).setVisibility (visiCoin);
        findViewById (R.id.tableRow24).setVisibility (visiCoin);
        findViewById (R.id.tableRow25).setVisibility (visiCoin);
        findViewById (R.id.tableRow26).setVisibility (visiCoin);
        findViewById (R.id.tableRow27).setVisibility (visiCoin);
    }


    private void exchange(String direction) {
        EditText editTextCHF = findViewById (R.id.editTextCHF);
        EditText editTextForeign = findViewById (R.id.editTextForeign);

        if (direction.equals ("from")) {
            try {
                editTextForeign.removeTextChangedListener (watcherForeign);
                String amountCHFString = editTextCHF.getText ().toString ();
                if (amountCHFString.equals ("")) {
                    editTextForeign.setText ("");
                } else {
                    String acs = amountCHFString.replace (",", ""); // reject komma
                    float amountCHFFloat = Float.parseFloat (acs);
                    float wechselBetrag = amountCHFFloat / kursFloat;
                    editTextForeign.setText (Global.formatRate (wechselBetrag));
                }
                editTextForeign.addTextChangedListener (watcherForeign);
            } catch (Exception e) {
                errorMessage ("exchange", "CHF amount invalid - enter 9999.99");
            }
        } else {
            try {
                editTextCHF.removeTextChangedListener (watcherCHF);
                String amountForeignString = editTextForeign.getText ()
                        .toString ();
                if (amountForeignString.equals ("")) {
                    editTextCHF.setText ("");
                } else {
                    String aff = amountForeignString.replace (",", "");
                    float amountForeignFloat = Float.parseFloat (aff);
                    float wechselBetrag = amountForeignFloat * kursFloat;
                    editTextCHF.setText (Global.formatRate (wechselBetrag));
                }
                editTextCHF.addTextChangedListener (watcherCHF);
            } catch (Exception e) {
                errorMessage ("exchange",
                        "foreign amount invalid - enter 9999.99");
            }
        }
    }

    // Control your touch screen with the following actions:
    // ●● Tap: Touch once with your finger to select or launch a
    // menu, option, or application.
    // ●● Tap and hold: Tap an item and hold it for more than 2
    // seconds to open a pop-up option list.
    // ●● Drag: Tap and drag your finger up, down, left, or right to
    // move to items on lists.
    // ●● Drag and drop: Tap and hold your finger on an item, and
    // then drag your finger to move the item.
    // ●● Double-tap: Tap twice quickly with your finger to zoom in
    // or out while viewing photos or web pages.

    private void processRates(String currencySymbol) {

        try {
            String[] result = getResultat ();

            if (result == null) {
                errorMessage ("connection",
                        "exchange rates not avaialble: activate your internet connection");
                return;
            }

            String s1 = result[0] + " from ECB\n- no liability assumed -";
            Toast.makeText (this, s1, Toast.LENGTH_LONG).show ();

            TextView bill1000 = findViewById (R.id.bill1000);
            bill1000.setText (result[1]);
            TextView bill200 = findViewById (R.id.bill200);
            bill200.setText (result[2]);
            TextView bill100 = findViewById (R.id.bill100);
            bill100.setText (result[3]);
            TextView bill50 = findViewById (R.id.bill50);
            bill50.setText (result[4]);
            TextView bill20 = findViewById (R.id.bill20);
            bill20.setText (result[5]);
            TextView bill10 = findViewById (R.id.bill10);
            bill10.setText (result[6]);

            TextView coins500 = findViewById (R.id.coins500);
            coins500.setText (result[7]);
            TextView coins200 = findViewById (R.id.coins200);
            coins200.setText (result[8]);
            TextView coins100 = findViewById (R.id.coins100);
            coins100.setText (result[9]);
            TextView coins50 = findViewById (R.id.coins50);
            coins50.setText (result[10]);
            TextView coins20 = findViewById (R.id.coins20);
            coins20.setText (result[11]);
            TextView coins10 = findViewById (R.id.coins10);
            coins10.setText (result[12]);
            TextView coins5 = findViewById (R.id.coins5);
            coins5.setText (result[13]);

        } catch (Exception e) {
            System.out.println ("xDownload ExRateECB back" + Arrays.toString (resultat) + " " + e);
        }
    }

    // public void onClick(View view) {
    // finish();
    // }
    //
    // @Override
    // public void finish() {
    // Intent data = new Intent();
    // data.putExtra("returnKey1", "Swinging on a star. ");
    // data.putExtra("returnKey2", "You could be better then you are. ");
    // setResult(RESULT_OK, data);
    // super.finish();
    // }


    private String[] getResultat() {

        Map<String, Float> exchangeRates = Global.maintainECB ();
        System.out.println ("rates "+Global.exchangeRates.toString ());

        try {


            kursFloat  = exchangeRates.get (currencySymbol);

            float[] denomination = {1000.0f, 200.0f, 100.0f, 50.0f, 20.0f,
                    10.0f, 5.00f, 2.00f, 1.00f, .50f, .20f, .10f, .050f};
            resultat = new String[denomination.length + 1];
            for (int i = 0; i < denomination.length; i++) {
                float r1 = denomination[i] / kursFloat;
                resultat[i + 1] = Global.formatRate (r1);
            }
            resultat[0] = Global.zeit;

        } catch (Exception e) {
            System.out.println ("xDownloadError processong xml " + e);
            return null;
        }

        System.out.println ("xDownload ExRateECB" +
                "Kurs " + kursFloat + " resultat " + Arrays.toString (resultat));
        return resultat;
    }

    private void errorMessage(String title, String message) {
        new AlertDialog.Builder (this).setTitle (title).setMessage (message)
                .setNeutralButton ("Close", null).show ();
    }

}
