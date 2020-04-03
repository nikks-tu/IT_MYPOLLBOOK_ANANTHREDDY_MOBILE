package com.techuva.new_changes_corporate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.techuva.councellorapp.R;
import com.techuva.councellorapp.contus_Corporate.MApplication;
import com.techuva.councellorapp.contus_Corporate.activity.CustomDialogAdapter;
import com.techuva.councellorapp.contus_Corporate.app.Constants;
import com.techuva.councellorapp.contus_Corporate.myprofile.EditProfile;
import com.techuva.councellorapp.contusfly_corporate.utils.ImageUploadS3;
import com.techuva.councellorapp.contusfly_corporate.utils.Utils;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddComplaint extends AppCompatActivity {

    Context context;
    ImageView iv_back;
    ImageView imgCancel;
    ImageView iv_image;
    ImageView iv_add_image;
    TextView tv_heading;
    EditText edt_title;
    EditText edt_complaint;
    TextView tv_letter_count;
    TextView tv_upload;
    TextView tv_submit;
    LinearLayout ll_submit;
    FrameLayout ll_image;
    LinearLayout ll_image_upload;
    String profileImage;
    //uri image capture
    Uri mImageCaptureUri;
    Activity activity;
    private Uri imageFileUri;
    //file path
    private File filepath;
    private ImageUploadS3 imageS3Bucket;
    //profile image
    private String updateProfileImage = "";
    static String result;
    private Dialog editProfileListDialog;
    String updatedPhoto;

    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    final int PERMISSION_ALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complaint);
        init();


        ll_image_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!hasPermissions(AddComplaint.this, PERMISSIONS)){
                        ActivityCompat.requestPermissions(AddComplaint.this, PERMISSIONS, PERMISSION_ALL);
                        // Toast.makeText(getApplicationContext(), "No permission", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        editProfileChoosePic(activity);
                    }
                }
                else

                {
                    editProfileChoosePic(activity);
                }
            }
        });

    }


    private void editProfileChoosePic(final Activity activity) {
        //A dialog is a small window that prompts the user to make a decision or enter additional information.
        editProfileListDialog = new Dialog(activity);
        //Enable extended window features.
        editProfileListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Change the background of this window to a custom Drawable.
        editProfileListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editProfileListDialog.setContentView(R.layout.custom_dialog_adapter);
        ListView list =  editProfileListDialog.findViewById(R.id.component_list);
        //splits this string using the supplied regularExpression
        String[] cameraOptions = new String[]{getResources().getString(R.string.take_photo), getResources().getString(R.string.choose_pic), getResources().getString(R.string.cancel_gallery)};
        //dialog adapter
        CustomDialogAdapter adapter = new CustomDialogAdapter(this, cameraOptions);
        list.setAdapter(adapter);
        //show
        editProfileListDialog.show();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        updatePictureIntent();
                        break;
                    case 1:
                        //An intent is an abstract description of an operation to be performed.
                        // It can be used with startActivity to launch an Activity
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.action_complete_using)), 10);
                        editProfileListDialog.cancel();
                        break;
                    case 2:
                        //cancel the dialog
                        editProfileListDialog.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                else
                    return true;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 15:
                if (resultCode == RESULT_OK) {
                    //file uri
                    mImageCaptureUri = data.getData();
                    //file path
                    filepath = new File(getUpdatePicturePath(activity, mImageCaptureUri));
                    //set boolean
                    MApplication.setBoolean(activity, Constants.PROFILE_IMAGE_TRUE, true);
                    //set image urinew Handler().postDelayed()
                    Utils.loadImageWithGlide(AddComplaint.this, filepath, iv_image, R.drawable.img_ic_user);
                    imgCancel.setVisibility(View.VISIBLE);//visible
                    /*Calling the material design custom dialog **/
                    MApplication.materialdesignDialogStart(activity);
                    //s3 bucket
                    imageS3Bucket.executeUpload(filepath, "image", "","PROFILES/");
                }
                break;
            case 10:
                if (resultCode == RESULT_OK) {
                    try {
                        //file uri
                        mImageCaptureUri = data.getData();
                        String url = Objects.requireNonNull(data.getData()).toString();
                        //file path
                       /* if(url.startsWith("content://com.google.android.apps.photos.content")) {
                        filepath=new File(MApplication.getPath(mImageCaptureUri,mProfileEdit));
                        }
                        else {*/
                        filepath = new File(MApplication.getPath(activity, mImageCaptureUri));
                        /* }*/
                    } catch (URISyntaxException e) {
                        Log.e("", "", e);
                    }
                    //An intent is an abstract description of an operation to be performed. It can be used with startActivity to launch an Activity, broadcastIntent
                    // to send it to any interested BroadcastReceiver components, and startService(Intent)
                    try {
                        //set boolean
                        MApplication.setBoolean(activity, Constants.PROFILE_IMAGE_TRUE, true);
                        //set image uri
                        Utils.loadImageWithGlide(AddComplaint.this, filepath, iv_image, R.drawable.img_ic_user);
                        imgCancel.setVisibility(View.VISIBLE);//visible
                        /*Calling the material design custom dialog **/
                        MApplication.materialdesignDialogStart(activity);
                        //s3 bucket
                        imageS3Bucket.executeUpload(filepath, "image", "","PROFILES/");
                    }    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case 11:
                if (resultCode == Activity.RESULT_OK) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    //file path
                    filepath = new File(getUpdatePicturePath(activity, imageFileUri));
                    //set boolean
                    MApplication.setBoolean(activity, Constants.PROFILE_IMAGE_TRUE, true);


                    //set image uri
                    Utils.loadImageWithGlide(AddComplaint.this, filepath, iv_image, R.drawable.img_ic_user);
                    imgCancel.setVisibility(View.VISIBLE);//visible
                    /* Calling the material design custom dialog **/
                    MApplication.materialdesignDialogStart(activity);
                    //s3 bucket
                    imageS3Bucket.executeUpload(filepath, "image", "","PROFILES/");
                }
                break;
            default:
                break;
        }
    }

    private static String getUpdatePicturePath(Activity activity, Uri uri) {
        //Cursor implementations are not required to be synchronized so code using a Cursor from multiple threads should
        // perform its own synchronization when using the Cursor.
        Cursor editPicCursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (editPicCursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            //move to the first
            editPicCursor.moveToFirst();
            //Get the coulm index
            int idx = editPicCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            //get the value from the index
            result = editPicCursor.getString(idx);
            //close the cursor
            editPicCursor.close();
        }
        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("", "Permission callback called-------");
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("", "Storage services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        // Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                        editProfileChoosePic(AddComplaint.this);

                    } else {
                        Log.d("", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    hasPermissions(AddComplaint.this, PERMISSIONS);
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Please enable permissions from settings", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }
            }
        }

    }

    public void updatePictureIntent() {
        try {
            Intent mIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //Returns the current state of the primary shared/external storage media.
            File mFolder = new File(Environment.getExternalStorageDirectory() + "/"
                    + getResources().getString(R.string.app_name));
            //If folder does not exist
            if (!mFolder.exists()) {
                //create the folder
                mFolder.mkdir();
            }
            //Calendar is an abstract base class for converting between a Date object and a set of integer
            // fields such as YEAR, MONTH, DAY, HOUR, and so on. (A Date object represents a specific instant in time with millisecond precision.
            final Calendar c = Calendar.getInstance();
            //Storing the calender date to the string
            String mDate = c.get(Calendar.DAY_OF_MONTH) + "-" + ((c.get(Calendar.MONTH)) + 1) + "-" + c.get(Calendar.YEAR) + " " + c.get(Calendar.HOUR) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
            //Image name
            updatedPhoto = String.format(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name) + "/%s.png", "profilepic(" + mDate + ")");
            //Converting the bitmap into the fiel
            File photo = new File(updatedPhoto);
            // f set, the activity will not be launched if it is already running at the top of the history stack.
            mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //The name of the Intent-extra used to control the orientation of a ViewImage or a MovieView.
            mIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //file uri
            imageFileUri = Uri.fromFile(photo);

            //image file uri
            mIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
            startActivityForResult(mIntent, 11);
            //list dialog cancel
            editProfileListDialog.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(AddComplaint.this).create();
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= 21)
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Information");
        builder.setMessage("Please provide required permissions!");
        builder.setPositiveButton("OK", okListener);
        builder.setNegativeButton("Cancel", okListener);
        builder.create().show();
    }


    private void init() {
        context = AddComplaint.this;
        activity= this;
        iv_back = findViewById(R.id.iv_back);
        iv_image = findViewById(R.id.iv_image);
        iv_add_image = findViewById(R.id.iv_add_image);
        tv_heading = findViewById(R.id.tv_heading);
        edt_title = findViewById(R.id.edt_title);
        edt_complaint = findViewById(R.id.edt_complaint);
        tv_letter_count = findViewById(R.id.tv_letter_count);
        tv_upload = findViewById(R.id.tv_upload);
        tv_submit = findViewById(R.id.tv_submit);
        ll_submit = findViewById(R.id.ll_submit);
        ll_image = findViewById(R.id.ll_image);
        ll_image_upload = findViewById(R.id.ll_image_upload);
        imgCancel = findViewById(R.id.imgCancel);
    }
}
