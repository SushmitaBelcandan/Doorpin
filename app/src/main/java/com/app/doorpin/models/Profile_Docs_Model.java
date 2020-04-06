package com.app.doorpin.models;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.app.doorpin.Activity.Document_Details_Act;
import com.app.doorpin.Activity.Sample_Pdf_View_Act;
import com.app.doorpin.R;
import com.app.doorpin.reference.SessionManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

import java.io.File;
import java.util.ArrayList;


@Layout(R.layout.profile_docs_model)
public class Profile_Docs_Model {

    @View(R.id.cv_exp_gen)
    public CardView cv_exp_gen;

    @View(R.id.iv_exp_gen)
    public ImageView iv_exp_gen;

    public Context mContext;
    SessionManager sessionManager;
    public String str_img, strDocType;
    public ArrayList<String> arrList;
    public static ArrayList<String> arrImg_gen = new ArrayList<>();
    public static ArrayList<String> arrImg_exp = new ArrayList<>();

    public Profile_Docs_Model(Context context, String doc_type, String file_name) {
        this.mContext = context;
        this.strDocType = doc_type;
        this.str_img = file_name;
    }

    @Resolve
    public void onResolved() {
        sessionManager = new SessionManager(mContext);
        if (!str_img.equals("null") && !str_img.equals("NA") && !str_img.equals(null) && !str_img.isEmpty()) {
            String extension = str_img.substring(0, str_img.lastIndexOf(".") + 1);
            String file_extension = str_img.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {

                Glide.with(mContext).load(str_img).error(R.drawable.document_grey).into(iv_exp_gen);
                if (strDocType.equals("4")) {
                    arrImg_gen.add(str_img);
                } else {
                    arrImg_exp.add(str_img);
                }

            } else if (file_extension.toLowerCase().equals("pdf")) {
                Glide.with(mContext).load(R.drawable.pdf_file_icon).into(iv_exp_gen);
            } else {
                Glide.with(mContext).load(R.drawable.doc_file_icon).into(iv_exp_gen);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).error(R.drawable.document_grey).into(iv_exp_gen);
        }

    }

    @Click(R.id.cv_exp_gen)
    public void onClick() {
        if (!str_img.isEmpty() && !str_img.equals(null) && !str_img.equals("NA") && !str_img.equals("null")) {

            String extension = str_img.substring(0, str_img.lastIndexOf(".") + 1);
            String file_extension = str_img.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                Intent intentDocDetails = new Intent(mContext, Document_Details_Act.class);

                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                if (strDocType.equals("4")) {
                    Gson gson = new Gson();
                    String json = gson.toJson(arrImg_gen);
                    editor.putString("DOC", json);
                    editor.commit();
                    sessionManager.saveDocType("Genunity Documents");
                } else {
                    Gson gson = new Gson();
                    String json = gson.toJson(arrImg_exp);
                    editor.putString("DOC", json);
                    editor.commit();
                    sessionManager.saveDocType("Experience Documents");
                }

                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);
                // arrList.clear();
            } else if (file_extension.toLowerCase().equals("pdf")) {
                Intent intentDocDetails = new Intent(mContext, Sample_Pdf_View_Act.class);
                intentDocDetails.putExtra("FILE", str_img);
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);
            } else if (file_extension.toLowerCase().equals("doc") || file_extension.toLowerCase().equals("docx")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(str_img);
                intent.setDataAndType(uri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(str_img));
                File file = new File(str_img);
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