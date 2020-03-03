package com.app.doorpin.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.app.doorpin.Adapters.Docs_Details_Slider_Adapter;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Document_Details_Act extends AppCompatActivity {

    SessionManager session;
    Toolbar toolbar_doc_details;
    TabLayout indicator_doc_details;
    ViewPager pager_doc_details;

    private static String[] XMEN = {"https://www.gstatic.com/webp/gallery3/2.png", "https://www.gstatic.com/webp/gallery3/1.png", "https://www.gstatic.com/webp/gallery3/3.png"};
    private ArrayList<String> XMENArray = new ArrayList<String>();
    public static int currentPage = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_details_page);

        session = new SessionManager(Document_Details_Act.this);
        toolbar_doc_details = findViewById(R.id.toolbar_doc_details);
        indicator_doc_details = findViewById(R.id.indicator_doc_details);
        pager_doc_details = findViewById(R.id.pager_doc_details);

        //--------------------------------------------------------set toolbar title-----------------------
        setSupportActionBar(toolbar_doc_details);//manadatory for menu items
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_doc_details.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (!session.getDocType().equals("NA")) {
            getSupportActionBar().setTitle(session.getDocType());
        } else {
            toolbar_doc_details.setTitle("");
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        //--------------------------------------add docs into view pager-----------------------------------
        for (int i = 0; i < XMEN.length; i++) {
            XMENArray.add(XMEN[i]);
            Log.e("---images----", String.valueOf(XMEN[i]));
        }
        pager_doc_details.setAdapter(new Docs_Details_Slider_Adapter(Document_Details_Act.this, XMENArray));
        indicator_doc_details.setupWithViewPager(pager_doc_details);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                pager_doc_details.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
