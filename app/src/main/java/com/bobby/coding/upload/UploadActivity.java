package com.bobby.coding.upload;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobby.coding.R;
import com.bobby.coding.main.MainActivity;
import com.bobby.coding.model.Upload;
import com.bobby.coding.utils.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lyft.android.scissors.CropView;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Bitmap.CompressFormat.PNG;


/**
 * Created by amitgupta on 9/13/17.
 */

public class UploadActivity extends AppCompatActivity implements View.OnClickListener{
    Activity mActivity;
    Uri mCapturedImageURI;
    ImageView img_placeholder;
    CropView img_to_be_uploaded;
    CardView card_upload_image,card_capture_image;
    ImageView img_edit;
    String FINAL_PATH="";
    TextView tv_msg1,tv_msg2;
    String options[] ;
    private static final int CAMERA_PERMISSION=10;
    private static final int GALLERY_PERMISSION=11;
    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        options = new String[]{"Take Photo", "Upload from Gallery", "Cancel"};
        mActivity = this;

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        Toolbar toolbar   = (Toolbar)findViewById(R.id.app_bar);
        toolbar.setTitle("Upload Image");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img_to_be_uploaded = findViewById(R.id.img_main);
        img_placeholder    = findViewById(R.id.img_placeholder);
        card_capture_image = findViewById(R.id.upload_card);
        card_upload_image  = findViewById(R.id.card_upload_done);
        img_edit           =  findViewById(R.id.img_edit);
        tv_msg1            = (TextView)findViewById(R.id.textView72);
        tv_msg2            = (TextView)findViewById(R.id.textView73) ;

        card_capture_image.setOnClickListener(this);
        img_edit.setOnClickListener(this);
        card_upload_image.setOnClickListener(this);

    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    //On click listeners
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.upload_card){
            //  dialogOptionsUpload();
            selectFromAlertDialog();

        }
        else if(v.getId()== R.id.card_upload_done){
            File file = new File(FINAL_PATH);
            img_to_be_uploaded.extensions()
                    .crop()
                    .quality(100)
                    .format(PNG)
                    .into(file);
            Uri uri=Uri.fromFile(file);

            if (uri != null) {
                //displaying progress dialog while image is uploading
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.show();

                //getting the storage reference
                final StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + uri.toString().substring(uri.toString().lastIndexOf(".") + 1));

                //adding the file to reference
                Task uploadTask=sRef.putFile(uri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return sRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            progressDialog.dismiss();

                            //displaying success toast
                            Toasty.success(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG,true).show();
                            //creating the upload object to store uploaded image details
                            Upload upload = new Upload(""+(new Random().nextInt(198989)+111), downloadUri.toString());

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);
                            Intent intent =new Intent(UploadActivity.this,MainActivity.class);
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toasty.error(getApplicationContext(),"Someting went wrong", Toast.LENGTH_LONG,true).show();
                        }
                    }
                });

            } else {
                //display an error if no file is selected
                Toasty.error(getApplicationContext(), "No file selected", Toast.LENGTH_LONG,true).show();

            }
        }
        else if(v.getId()==R.id.img_edit){
            displayOptionsEdit();
        }
    }

    void displayOptionsEdit(){
        final String options[] = {"Remove image", "Edit Image"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).
                setItems(options,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Remove image")) {
                                    img_to_be_uploaded.setImageDrawable(null);
                                    img_edit.setVisibility(View.GONE);
                                    img_placeholder.setVisibility(View.VISIBLE);
                                    card_upload_image.setVisibility(View.INVISIBLE);
                                    card_capture_image.setVisibility(View.VISIBLE);
                                }
                                else  {
                                    selectFromAlertDialog();
                                }
                                dialog.dismiss();
                            }
                        }
                ).setTitle("Edit image");
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            FINAL_PATH= getRealPathFromURIlatest(mCapturedImageURI);;
            // type="image";
            try {
                if (data == null) {
                    Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(FINAL_PATH),
                            3000,3000);
                    img_to_be_uploaded.setImageBitmap(ThumbImage);
                } else {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    img_to_be_uploaded.setImageBitmap(photo);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            if (img_to_be_uploaded.getImageBitmap() != null) {
                img_placeholder.setVisibility(View.INVISIBLE);
                img_edit.setVisibility(View.VISIBLE);
                card_upload_image.setVisibility(View.VISIBLE);
                card_capture_image.setVisibility(View.INVISIBLE);
            }
        }


        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri selectedimg = data.getData();
            String column_[] = {MediaStore.Images.Media.DATA};
            Cursor cursor = getApplicationContext().getContentResolver().query(selectedimg, column_, null, null, null);
            cursor.moveToFirst();
            int columnindex = cursor.getColumnIndex(column_[0]);
            String picturepath = cursor.getString(columnindex);
            //path_gallery = picturepath;
            FINAL_PATH=picturepath;
            //type="image";
            cursor.close();
            Bitmap bitmap1 = BitmapFactory.decodeFile(picturepath);
            int nh = (int) ( bitmap1.getHeight() * (512.0 / bitmap1.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap1, 512, nh, true);

            if (isSizeLessThan(10 * 1024 * 1024, scaled)) {
                img_to_be_uploaded.setImageBitmap(scaled);
                img_to_be_uploaded.setImageBitmap(scaled);
                if (img_to_be_uploaded.getImageBitmap() != null) {
                    img_placeholder.setVisibility(View.INVISIBLE);
                    img_edit.setVisibility(View.VISIBLE);
                    card_upload_image.setVisibility(View.VISIBLE);
                    card_capture_image.setVisibility(View.INVISIBLE);
                }
            } else {
                Toast.makeText(mActivity, "Size greater than 10 Mb, upload a smaller file", Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isSizeLessThan(int size, Bitmap photo) {
        int bitmapByteCount = BitmapCompat.getAllocationByteCount(photo);
        return bitmapByteCount < size;
    }


    public String getRealPathFromURIlatest(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return getOriginalImagePath();
        }
    }

    private String getOriginalImagePath(){
        String projection[]= {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,null,null,null);
        int column_index= cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();
        return cursor.getString(column_index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            Intent intent=new Intent(UploadActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;
    }

    public void checkPermissionsForMarshMallow(String[] permissionsList,int requestCode){
        String permissions[] = permissionsList;
        int result = 0;


        ArrayList<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(mActivity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);

        }
    }


    private void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values = new ContentValues();
        Random random = new Random();
        values.put(MediaStore.Images.Media.TITLE, "Image File name"+random.nextInt(198989)+111);
        mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        startActivityForResult(intent, 1);
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if(requestCode==CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(grantResults.length>1) {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else {
                        boolean showRationale = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            showRationale = shouldShowRequestPermissionRationale(permissions[1]);
                        }

                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting

                            showRequestDialog("Camera Permission Required", "We require permissions to use camera.Please grant camera permissions from app settings", requestCode);

                        }
                    }
                }
                else
                {
                    openCamera();
                }
            } else {
                // Permission Denied
                boolean showRationale = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                }

                if (!showRationale) {
                    showRequestDialog("Camera & Storage Permission Required", "We require permission to store pictures taken from your camera. Please grant  camera and external storage permissions from app settings ", requestCode);
                }
            }
        }
        else if(requestCode==GALLERY_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
            else
            {
                boolean showRationale = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                }

                if (!showRationale) {

                    showRequestDialog("Storage Permission Required", "We require permission to access external storage. Please grant external storage permissions from app settings ", requestCode);
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRequestDialog(String title, String message, final int requestCode) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);
                    }
                })
                .show();
    }

    private void selectFromAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).
                setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                if (options[item].equals("Take Photo")) {
                                    if(Build.VERSION.SDK_INT>=23) {
                                        int hasPermExternalCamera = getPackageManager().checkPermission(Manifest.permission.CAMERA, mActivity.getPackageName());
                                        int hasPermWriteExt =getPackageManager().checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,mActivity.getPackageName());

                                        if (hasPermExternalCamera != PackageManager.PERMISSION_GRANTED || hasPermWriteExt!=PackageManager.PERMISSION_GRANTED) {
                                            checkPermissionsForMarshMallow(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_PERMISSION);
                                        }

                                        if(hasPermExternalCamera == PackageManager.PERMISSION_GRANTED && hasPermWriteExt==PackageManager.PERMISSION_GRANTED){
                                            openCamera();
                                        }
                                    }

                                    else
                                    {
                                        openCamera();
                                    }

                                } else if (options[item].equals("Upload from Gallery")) {
                                    if (Build.VERSION.SDK_INT >= 23) {
                                        int hasPermExternalRead = getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, mActivity.getPackageName());

                                        if (hasPermExternalRead != PackageManager.PERMISSION_GRANTED) {
                                            checkPermissionsForMarshMallow(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, GALLERY_PERMISSION);
                                        } else {
                                            openGallery();
                                        }
                                    } else {
                                        openGallery();
                                    }
                                }
                                dialog.dismiss();
                            }
                        }

                ).setTitle("Upload Image");
        builder.create().show();
    }
}
