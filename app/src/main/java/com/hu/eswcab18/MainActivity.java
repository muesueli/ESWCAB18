package com.hu.eswcab18;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GridView gridviewFlags;

    private final String[][] rbTxt = new String[][]{{"EUR", "eu"}, {"USD", "us"},
            {"JPY", "jp"}, {"GBP", "gb"}, {"RUB", "ru"}, {"BRL", "br"},
            {"CAD", "ca"}, {"CNY", "cn"}, {"HKD", "hk"}, {"IDR", "id"},
            {"INR", "in"}, {"KRW", "kr"}, {"MXN", "mx"}, {"SGD", "sg"},
            {"ZAR", "sh"}, {"THB", "th"}};

    private Global global;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate (icicle);
        setContentView (R.layout.activity_main);
        Toolbar toolbar = findViewById (R.id.toolbar);
        setSupportActionBar (toolbar);

        gridviewFlags = findViewById (R.id.gridviewFlags);
        gridviewFlags.setAdapter (new MainActivity.ImageAdapter (this));
        gridviewFlags.setOnItemClickListener (new AdapterView.OnItemClickListener () {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (Global.exchangeRates.containsKey (rbTxt[arg2][0])) {
                    Intent intent = new Intent (getApplicationContext (),
                            BillsActivity.class);
                    Bundle bundle = new Bundle ();
                    bundle.putString ("currencySymbol", rbTxt[arg2][0]);
                    intent.putExtras (bundle);
                    startActivity (intent);
                }
            }

        });


        global = new Global (this);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        Global.maintainECB ();
    }

    @Override
    protected void onPause() {
        super.onPause ();
        System.out.println ("ECB MainActivity onPause rates " + Global.exchangeRates.toString ());
    }

    public static boolean isTablet(Context context) {
        return (context.getResources ().getConfiguration ().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    class ImageAdapter extends BaseAdapter {
        final Context MyContext;

        ImageAdapter(Context _MyContext) {
            MyContext = _MyContext;
        }

        @Override
        public int getCount() {
            /* Set the number of element we want on the grid */
            int l = rbTxt.length;
            return rbTxt.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View MyView = convertView;

            try {

                if (convertView == null) {
                    /* we define the view that will display on the grid */

                    // Inflate the layout
                    LayoutInflater inflater = (LayoutInflater) getSystemService (LAYOUT_INFLATER_SERVICE);
                    MyView = inflater.inflate (R.layout.grid_item, null);

                    // Add The Text!!!
                    String symbol = rbTxt[position][0];
                    String kursString;
                    if (Global.exchangeRates.containsKey (symbol)) {
                        float kursForeign = 1 / Global.exchangeRates.get (symbol);
                        kursString = Global.formatRate (kursForeign);
                } else {
                    kursString = "???";
                }
                TextView tv = MyView
                        .findViewById (R.id.grid_item_text);
                tv.setText (rbTxt[position][0] + ": " + kursString);

                // Add The Image!!!
                ImageView iv = MyView
                        .findViewById (R.id.grid_item_image);
                iv.setScaleType (ImageView.ScaleType.CENTER_CROP);
                iv.setPadding (8, 8, 8, 8);

                iv.setImageResource (mImageIds[position]);
            }
        } catch(
        Exception e)

        {
            Log.e ("inflator exception", "" + e);
        }

            return MyView;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    // references to our images
    private final Integer[] mImageIds = {R.drawable.eu, //
            R.drawable.us, //
            R.drawable.jp, //
            R.drawable.gb, //
            R.drawable.ru, //
            R.drawable.br, //
            R.drawable.ca, //
            R.drawable.cn, //
            R.drawable.hk, //
            R.drawable.id, //
            R.drawable.in, //
            R.drawable.kr, //
            R.drawable.mx, //
            R.drawable.sg, //
            R.drawable.sh, //
            R.drawable.th}; //

}
}
