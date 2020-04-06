package com.app.doorpin.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.doorpin.progress_bar.CallBackFilesAfterUpload_Interface;
import com.app.doorpin.reference.SessionManager;
import com.app.doorpin.retrofit.ApiClient;
import com.app.doorpin.retrofit.ApiInterface;
import com.app.doorpin.retrofit.FileUploadModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileUploader {

    SessionManager sessionManager;
    private Context mContex;
    private FileUploaderCallback fileUploaderCallback;
    private File[] files;
    private int uploadIndex = -1;
    private long totalFileLength = 0;
    private long totalFileUploaded = 0;
    private String[] responses;
    private String str_docs_type = "NA";
    private String userType;
    private CallBackFilesAfterUpload_Interface callBackFilesAfterUpload_interface;
    private ApiInterface apiConfig;

    public interface FileUploaderCallback {
        void onError();

        void onFinish(String[] responses);

        void onProgressUpdate(int currentpercent, int totalpercent, int filenumber, int file_size);
    }

    public class PRRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        private PRRequestBody(final File file) {
            mFile = file;
        }

        @Override
        public MediaType contentType() {
            // i want to upload only images
            return MediaType.parse("*/* ");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }
    }

    private static String PROFILE_PAGE = "profile";
    private static String ADD_PAGE = "add_page";

    public void uploadFiles(Context mContext, String user_type, String page_call, String doc_type, String filekey, File[] files, FileUploaderCallback fileUploaderCallback) {
        this.mContex = mContext;
        this.fileUploaderCallback = fileUploaderCallback;
        this.callBackFilesAfterUpload_interface = (CallBackFilesAfterUpload_Interface) mContext;
        this.files = files;
        this.uploadIndex = -1;
        // this.uploadURL = url;
        totalFileUploaded = 0;
        totalFileLength = 0;
        str_docs_type = doc_type;
        userType = user_type;
        responses = new String[files.length];
        for (File file : files) {
            totalFileLength = totalFileLength + file.length();
        }
        if (page_call.equals(PROFILE_PAGE)) {
            uploadNext(PROFILE_PAGE);
        } else {
            uploadNext(ADD_PAGE);
        }
    }

    private void uploadNext(String page_call) {
        if (page_call.equals(PROFILE_PAGE)) {
            if (files.length > 0) {
                if (uploadIndex != -1)
                    totalFileUploaded = totalFileUploaded + files[uploadIndex].length();
                uploadIndex++;
                if (uploadIndex < files.length) {
                    if (Utils.CheckInternetConnection(mContex)) {
                        uploadFile(uploadIndex, str_docs_type, page_call, userType);
                    } else {
                        Toast.makeText(mContex, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    fileUploaderCallback.onFinish(responses);
                }
            } else {
                fileUploaderCallback.onFinish(responses);
            }
        } else {
            if (files.length > 0) {
                if (uploadIndex != -1)
                    totalFileUploaded = totalFileUploaded + files[uploadIndex].length();
                uploadIndex++;
                if (uploadIndex < files.length) {
                    if (Utils.CheckInternetConnection(mContex)) {
                        uploadFile(uploadIndex, str_docs_type, page_call, userType);
                    } else {
                        Toast.makeText(mContex, "Please Check Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    fileUploaderCallback.onFinish(responses);
                }
            } else {
                fileUploaderCallback.onFinish(responses);
            }
        }
    }

    private void uploadFile(final int index, final String docs_type, final String page_type, String userType) {
        apiConfig = ApiClient.getClient().create(ApiInterface.class);
        File file = new File(String.valueOf(files[index]));
        Log.d("--------media path-----", "" + files[index]);
        PRRequestBody fileBody = new PRRequestBody(file);
        MultipartBody.Part fileToUpload;
        RequestBody doc_type = RequestBody.create(MediaType.parse("text/plain"), docs_type);
        Call<FileUploadModel> call;
        if (page_type.equalsIgnoreCase(ADD_PAGE)) {
            fileToUpload = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), fileBody);
            call = apiConfig.uploadDocuments(fileToUpload, doc_type);
        } else {
            RequestBody user_type = RequestBody.create(MediaType.parse("text/plain"), userType);
            fileToUpload = MultipartBody.Part.createFormData("uploaded_file[]", file.getName(), fileBody);
            call = apiConfig.uploadProfileDocument(fileToUpload, doc_type, user_type);
        }

        call.enqueue(new Callback<FileUploadModel>() {
            @Override
            public void onResponse(@NonNull Call<FileUploadModel> call,
                                   @NonNull Response<FileUploadModel> response) {
                FileUploadModel responseModel = response.body();
                if (response.isSuccessful()) {
                    if (responseModel.getStatus().equals("success")) {
                        List<FileUploadModel.IllnessDocUpload_Datum> illnessDoc_Res = responseModel.getResult();
                        for (FileUploadModel.IllnessDocUpload_Datum illnessDocData : illnessDoc_Res) {
                            responses[index] = illnessDocData.getImage();
                            if (illnessDocData.getImage() != null) {
                                callBackFilesAfterUpload_interface.getFile(illnessDocData.getImage(), docs_type);
                            } else {
                                callBackFilesAfterUpload_interface.getFile("NA", docs_type);
                            }
                        }
                    } else {
                        responses[index] = "";
                        fileUploaderCallback.onError();
                    }
                    uploadNext(page_type);
                } else {
                    if (response.code() == 400) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("status");
                            String internalMessage = jsonObject.getString("message");
                            responses[index] = "";
                            fileUploaderCallback.onError();
                            uploadNext(page_type);
                            new android.app.AlertDialog.Builder(mContex)
                                    .setMessage(internalMessage)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<FileUploadModel> call, @NonNull Throwable t) {
                call.cancel();
                fileUploaderCallback.onError();
            }
        });
    }

    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;

        private ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            int current_percent = (int) (100 * mUploaded / mTotal);
            int total_percent = (int) (100 * (totalFileUploaded + mUploaded) / totalFileLength);
            int file_size = Integer.parseInt(String.valueOf(totalFileLength / 1024));
            fileUploaderCallback.onProgressUpdate(current_percent, total_percent, uploadIndex + 1, file_size);
        }
    }
}