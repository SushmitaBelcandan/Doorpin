package com.app.doorpin.models;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.app.doorpin.Activity.Document_Details_Act;
import com.app.doorpin.Activity.PatientDetails;
import com.app.doorpin.Activity.Sample_Pdf_View_Act;
import com.app.doorpin.R;
import com.app.doorpin.interface_pkg.View_Doc_Interface;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

@Layout(R.layout.patient_doc_list_model)
public class Patient_Doc_List_Model {

    @View(R.id.cv_container)
    public CardView cv_container;

    @View(R.id.ivDocs)
    public ImageView ivDocs;

    public Context mContext;
    public String strId, strName;
    public ArrayList<String> arrList;
    public ArrayList<String> arrImg = new ArrayList<>();

    public Patient_Doc_List_Model(Context contxt, String str_id, String str_name, ArrayList<String> arr_name) {
        this.mContext = contxt;
        this.strId = str_id;
        this.strName = str_name;
        this.arrList = arr_name;
    }

    @Resolve
    public void onResolved() {
        if (!strId.isEmpty() && !strId.equals(null) && !strId.equals("NA") && !strId.equals("null")) {
            if (!strName.isEmpty() && !strName.equals(null) && !strName.equals("NA") && !strName.equals("null")) {
                //store image document in seperate list
                String extension = strName.substring(0, strName.lastIndexOf(".") + 1);
                String file_extension = strName.replace(extension, "");
                if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                        || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                        || file_extension.toLowerCase().equals("jpeg")) {
                    Glide.with(mContext).load(strName).into(ivDocs);
                } else if (file_extension.toLowerCase().equals("pdf")) {
                    Glide.with(mContext).load(R.drawable.pdf_file_icon).into(ivDocs);
                } else {
                    Glide.with(mContext).load(R.drawable.doc_file_icon).into(ivDocs);
                }
            } else {
                Glide.with(mContext).load(R.drawable.download_sample).into(ivDocs);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(ivDocs);
        }

    }

    @Click(R.id.cv_container)
    public void viewDocDetails() {
        arrImg.clear();
        //navigate to doc container slider
        for (int i = 0; i < arrList.size(); i++) {
            String extension = arrList.get(i).substring(0, arrList.get(i).lastIndexOf(".") + 1);
            String file_extension = arrList.get(i).replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                arrImg.add(arrList.get(i));
            }

        }
        if (!strName.isEmpty() && !strName.equals(null) && !strName.equals("NA") && !strName.equals("null")) {

            String extension = strName.substring(0, strName.lastIndexOf(".") + 1);
            String file_extension = strName.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                Intent intentDocDetails = new Intent(mContext, Document_Details_Act.class);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(arrImg);
                editor.putString("DOC", json);
                editor.commit();

                intentDocDetails.putExtra("FLAG_FILE", "ILLNESS");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);
                // arrList.clear();
            } else if (file_extension.toLowerCase().equals("pdf")) {
                Intent intentDocDetails = new Intent(mContext, Sample_Pdf_View_Act.class);
                intentDocDetails.putExtra("FILE", strName);
                intentDocDetails.putExtra("FLAG", "ILLNESS");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);
            } else if (file_extension.toLowerCase().equals("doc") || file_extension.toLowerCase().equals("docx")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(strName);
                intent.setDataAndType(uri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(strName));
                File file = new File(strName);
                newIntent.setDataAndType(Uri.fromFile(file), mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(newIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(mContext, "No handler for this type of file.", Toast.LENGTH_LONG).show();
        }
    }

    private String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }
}
