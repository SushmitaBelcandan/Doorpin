package com.app.doorpin.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.doorpin.R;
import com.app.doorpin.models.View_Docs_Item_Model;
import com.mindorks.placeholderview.PlaceHolderView;

public class View_Docs_Surg_Patient_Act extends AppCompatActivity {

    PlaceHolderView phvViewDocs;
    Toolbar toolbar_view_docs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_docs_patient_surg_main);

        toolbar_view_docs = findViewById(R.id.toolbar_view_docs);
        setSupportActionBar(toolbar_view_docs);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar_view_docs.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        phvViewDocs = findViewById(R.id.phvViewDocs);
        phvViewDocs.getBuilder()
                .setHasFixedSize(false)
                .setItemViewCacheSize(10)
                .setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        phvViewDocs.addView(new View_Docs_Item_Model(getApplicationContext()));
        phvViewDocs.addView(new View_Docs_Item_Model(getApplicationContext()));
        phvViewDocs.addView(new View_Docs_Item_Model(getApplicationContext()));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
