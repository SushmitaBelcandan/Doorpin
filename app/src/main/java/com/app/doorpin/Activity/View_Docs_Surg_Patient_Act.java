package com.app.doorpin.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import com.app.doorpin.R;
import com.app.doorpin.models.View_Docs_Item_Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindorks.placeholderview.PlaceHolderView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class View_Docs_Surg_Patient_Act extends AppCompatActivity {

    PlaceHolderView phvViewDocs;
    Toolbar toolbar_view_docs;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    LinearLayout llNoData;


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
        llNoData = findViewById(R.id.llNoData);
        phvViewDocs.getBuilder()
                .setHasFixedSize(false)
                .setItemViewCacheSize(10)
                .setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        //get saved docs
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(View_Docs_Surg_Patient_Act.this);
        editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json_docs = sharedPrefs.getString("doc_id", "default_key");

        Type type1 = (Type) new TypeToken<ArrayList<String>>() {
        }.getType();

        ArrayList<String> inpList_Docs = new Gson().fromJson(json_docs, (java.lang.reflect.Type) type1);
        //add items in view from json array list
        if (inpList_Docs.size() > 0) {
            llNoData.setVisibility(View.GONE);
            phvViewDocs.setVisibility(View.VISIBLE);
            for (int i = 0; i < inpList_Docs.size(); i++) {
                if (!inpList_Docs.get(i).equals("NA") && !inpList_Docs.get(i).equals("null") && !inpList_Docs.get(i).equals(null) && !inpList_Docs.isEmpty()) {
                    phvViewDocs.addView(new View_Docs_Item_Model(getApplicationContext(), inpList_Docs.get(i),inpList_Docs));

                }
                //else skip the list item
            }
        } else {
            llNoData.setVisibility(View.VISIBLE);
            phvViewDocs.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
