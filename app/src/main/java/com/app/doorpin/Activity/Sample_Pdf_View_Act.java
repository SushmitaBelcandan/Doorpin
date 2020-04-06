package com.app.doorpin.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.doorpin.R;
import com.app.doorpin.interface_pkg.View_Doc_Interface;
import com.app.doorpin.reference.SessionManager;
import com.bumptech.glide.Glide;

import java.util.List;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class Sample_Pdf_View_Act extends AppCompatActivity implements DownloadFile.Listener {

    SessionManager session1;
    Toolbar toolbar_doc_details1;
    public static String SAMPLE_FILE = "http://dev.dxminds.online/Doorpin/public/Doorpin/Patient/Reports/hindu-panchang-20191585396592.pdf"; //your file path
    public static final String SAMPLE_FILE_1 = "http://dev.dxminds.online/Doorpin/public/Doorpin/Patient/Otherdoc/IMG_20170514_2106301585396796.jpg"; //your file path
    public static final String SAMPLE_FILE_2 = "http://dev.dxminds.online/Doorpin/public/Doorpin/Patient/Otherdoc/Educational_srs (1)1585396862.docx"; //your file path
    PDFPagerAdapter adapter;
    RemotePDFViewPager remotePDFViewPager;
    ImageView ivImg;
    String strUrl, strFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_pdf_view_layout);

        session1 = new SessionManager(Sample_Pdf_View_Act.this);
        toolbar_doc_details1 = findViewById(R.id.toolbar_doc_details1);
        //--------------------------------------------------------set toolbar title-----------------------
        setSupportActionBar(toolbar_doc_details1);//manadatory for menu items
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar_doc_details1.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivImg = findViewById(R.id.ivImg);
        Intent oIntent = getIntent();//calling from patient details
        strUrl = oIntent.getExtras().getString("FILE");

        remotePDFViewPager = new RemotePDFViewPager(Sample_Pdf_View_Act.this, strUrl, this);

    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        // That's the positive case. PDF Download went fine
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        setContentView(remotePDFViewPager);
    }

    @Override
    public void onFailure(Exception e) {
        // This will be called if download fails
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        // You will get download progress here
        // Always on UI Thread so feel free to update your views here
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        adapter.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

   /* @Override
    public void onViewDoc(String url_str) {
        SAMPLE_FILE = url_str;
    }*/
}
