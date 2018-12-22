package com.gmail.rajab1.ammar.seventhart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.text.Html.fromHtml;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AboutActivity extends AppCompatActivity {
    MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView feedback = (TextView) findViewById(R.id.somarEmail);
        feedback.setText(fromHtml("<a href=\"mailto:somareee@gmail.com\">somareee@gmail.com</a>"));
        feedback.setMovementMethod(LinkMovementMethod.getInstance());

        TextView dev = (TextView) findViewById(R.id.developer);
        dev.setText(fromHtml("<a href=\"mailto:ammar.rajab1@gmail.com\">Developed & Maintained by Ammar Rajab</a>"));
        dev.setMovementMethod(LinkMovementMethod.getInstance());

        TextView phone=(TextView)findViewById(R.id.textView3);
        phone.setPaintFlags(phone.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialer=new Intent(Intent.ACTION_DIAL);
                dialer.setData(Uri.parse("tel:041422572"));
                startActivity(dialer);
            }
        });

    }
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}

