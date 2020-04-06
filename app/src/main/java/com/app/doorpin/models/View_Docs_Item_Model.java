package com.app.doorpin.models;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.doorpin.Activity.Document_Details_Act;
import com.app.doorpin.Activity.Sample_Pdf_View_Act;
import com.app.doorpin.Activity.View_Docs_Surg_Patient_Act;
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

@Layout(R.layout.view_docs_item_model)
public class View_Docs_Item_Model {

    @View(R.id.ivDocs)
    public ImageView ivDocs;

    SessionManager sessionManager;
    Context mContext;
    String str_docs_link;
    public ArrayList<String> arrList = new ArrayList<>();
    public ArrayList<String> arr_list_docs;

    public View_Docs_Item_Model(Context context, String docs_link, ArrayList<String> arrList_Docs) {
        this.mContext = context;
        this.str_docs_link = docs_link;
        this.arr_list_docs = arrList_Docs;
    }

    @Resolve
    public void onResolved() {
        sessionManager = new SessionManager(mContext);
        if (!str_docs_link.equals("NA") && !str_docs_link.equals("null") && !str_docs_link.equals(null) && !str_docs_link.isEmpty()) {
            String extension = str_docs_link.substring(0, str_docs_link.lastIndexOf(".") + 1);
            String file_extension = str_docs_link.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                // arrList.add(str_docs_link);
                Glide.with(mContext).load(str_docs_link).error(R.drawable.download_sample).into(ivDocs);

            } else if (file_extension.toLowerCase().equals("pdf")) {
                Glide.with(mContext).load(R.drawable.pdf_file_icon).into(ivDocs);
            } else {
                Glide.with(mContext).load(R.drawable.doc_file_icon).into(ivDocs);
            }
        } else {
            Glide.with(mContext).load(R.drawable.download_sample).into(ivDocs);
        }

    }

    @Click(R.id.ivDocs)
    public void onClickImageOrDoc() {
        arrList.clear();
        for (int i = 0; i < arr_list_docs.size(); i++) {
            String extension = arr_list_docs.get(i).substring(0, arr_list_docs.get(i).lastIndexOf(".") + 1);
            String file_extension = arr_list_docs.get(i).replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                arrList.add(arr_list_docs.get(i));
            }
        }
        if (!str_docs_link.equals("NA") && !str_docs_link.equals("null") && !str_docs_link.equals(null) && !str_docs_link.isEmpty()) {

            String extension = str_docs_link.substring(0, str_docs_link.lastIndexOf(".") + 1);
            String file_extension = str_docs_link.replace(extension, "");
            if (file_extension.toLowerCase().equals("jpg") || file_extension.toLowerCase().equals("png")
                    || file_extension.toLowerCase().equals("bmp") || file_extension.toLowerCase().equals("svg")
                    || file_extension.toLowerCase().equals("jpeg")) {
                Intent intentDocDetails = new Intent(mContext, Document_Details_Act.class);
                sessionManager.saveDocType("Surgery Documents");
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(arrList);
                editor.putString("DOC", json);
                editor.commit();
                intentDocDetails.putExtra("FLAG_FILE", "SURGERY");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);
                // arrList.clear();
            } else if (file_extension.toLowerCase().equals("pdf")) {
                Intent intentDocDetails = new Intent(mContext, Sample_Pdf_View_Act.class);
                intentDocDetails.putExtra("FILE", str_docs_link);
                intentDocDetails.putExtra("FLAG", "SURGERY");
                intentDocDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intentDocDetails);

            } else if (file_extension.toLowerCase().equals("doc") || file_extension.toLowerCase().equals("docx")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(str_docs_link);
                intent.setDataAndType(uri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(fileExt(str_docs_link));
                File file = new File(str_docs_link);
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
