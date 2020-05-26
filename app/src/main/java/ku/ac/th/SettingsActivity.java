package ku.ac.th;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{

    private Toolbar mToolbar;

    private EditText userStatus,userName;
    private Button UpdateAccoutSettingButton;
    private CircleImageView userProfileImage;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    final static int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;
    private DatabaseReference UsersRef;
    String currentUserID;

    private  Spinner userGender, userRelationship, userGeneration;




    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);


        //สร้าง Toolbar ของ ตั้งค่า
        mToolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("ตั้งค่าข้อมูลของคุณ");

        userStatus = (EditText) findViewById(R.id.settings_status);
        userName = (EditText) findViewById(R.id.settings_username);
//        userGender = (EditText) findViewById(R.id.settings_profile_gender);
        userProfileImage= (CircleImageView) findViewById(R.id.setting_edit_image);
        UpdateAccoutSettingButton = (Button) findViewById(R.id.setting_update_account_button);

        loadingBar = new ProgressDialog(this);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Image");
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);;

       userGender = (Spinner)  findViewById(R.id.settings_profile_gender);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SettingsActivity.this,
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.gender));

        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        userGender.setAdapter(myAdapter);




        userRelationship = (Spinner) findViewById(R.id.settings_profile_reletionship);

        final ArrayAdapter<String> myAdapterRelation = new ArrayAdapter<>(SettingsActivity.this,
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.relationship));

        myAdapterRelation.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        userRelationship.setAdapter(myAdapterRelation);

        userGeneration = (Spinner) findViewById(R.id.settings_profile_generation);

        final ArrayAdapter<String> myAdapterGeneration = new ArrayAdapter<>(SettingsActivity.this,
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.generation));

        myAdapterGeneration.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        userGeneration.setAdapter(myAdapterGeneration);


        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myUserStatus = dataSnapshot.child("status").getValue().toString();
                    String myUserGender = dataSnapshot.child("gender").getValue().toString();
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myRelationship = dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myGeneration = dataSnapshot.child("generation").getValue().toString();


                    //load รูปภาพ
                    Picasso.with(SettingsActivity.this).load(myProfileImage).into(userProfileImage);


                    if (myUserStatus.equalsIgnoreCase(" "))
                    {
                        userStatus.setHint("ยังไม่ได้ตั้งค่า");
                    }
                    else
                    {
                        userStatus.setText(myUserStatus);
                    }

                    userName.setText(myUsername);


//                    userGender.setSelected(Boolean.parseBoolean(myUserGender));
                    int position = myAdapter.getPosition(myUserGender);
                    userGender.setSelection(position);

                    int position_relation = myAdapterRelation.getPosition(myRelationship);
                    userRelationship.setSelection(position_relation);

                    int position_generation = myAdapterGeneration.getPosition(myGeneration);
                    userGeneration.setSelection(position_generation);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UpdateAccoutSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateAccountInfo();
            }
        });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
//                loadingBar.setTitle("กำลังโหลดรูปภาพของคุณ");
//                loadingBar.setMessage("กรุณารอสักครู่ , รูปภาพของคุณกำลังถูกบันทึก...");
//                loadingBar.setCanceledOnTouchOutside(true);
//                loadingBar.show();

                //loading
                final SweetAlertDialog pDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("รูปภาพของคุณกำลังถูกบันทึก");
                pDialog.setCancelable(true);
                pDialog.show();



                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SettingsActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UsersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                DynamicToast.makeSuccess( SettingsActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น").show();
//                                                Toast.makeText(SettingsActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                DynamicToast.makeError( SettingsActivity.this, "เกิดข้อผิดพลาด: " + message).show();
//                                                Toast.makeText(SettingsActivity.this, "เกิดข้อผิดพลาด: " + message, Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                DynamicToast.makeError( SettingsActivity.this, "เกิดข้อผิดพลาด: Image can not be cropped. Try Again.").show();
//                Toast.makeText(this, "เกิดข้อผิดพลาด: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }



    private void ValidateAccountInfo()
    {
        final String username = userName.getText().toString();
        final String status = userStatus.getText().toString();
        final String gender = userGender.getSelectedItem().toString();
        final String relationshipstatus = userRelationship.getSelectedItem().toString();
        final String generation = userGeneration.getSelectedItem().toString();

        if (TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"กรุณากรอกชื่อบัญชีของคุณ",Toast.LENGTH_SHORT).show();
        }
//        else if (TextUtils.isEmpty(status))
//        {
//            Toast.makeText(this,"กรุณากรอกสถานะของคุณ",Toast.LENGTH_SHORT).show();
//        }
//        else if (TextUtils.isEmpty(gender))
//        {
//            Toast.makeText(this,"กรุณากรอกเพศชองคุณ",Toast.LENGTH_SHORT).show();
//        }
        else {

//            loadingBar.setTitle("กำลังโหลดรูปภาพของคุณ");
//            loadingBar.setMessage("กรุณารอสักครู่ , รูปภาพของคุณกำลังถูกบันทึก...");
//            loadingBar.setCanceledOnTouchOutside(true);
//            loadingBar.show();

            new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("ยืนยันการอัพเดต")
                    .setContentText("คุณต้องการอัพเดตข้อมูลส่วนตัวใช่ไหม!")
                    .setConfirmText("ยืนยัน!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {


                            SweetAlertDialog pDialog = new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                            pDialog.setTitleText("กำลังบันทึก ...");
                            pDialog.setCancelable(true);
                            pDialog.show();

                            UpdateAccoutSettingButton(username,status,gender, relationshipstatus, generation);

                            sDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelButton("ยกเลิก", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();


        }

    }

    private void UpdateAccoutSettingButton(String username, String status, String gender, String relationshipstatus, String generation) {

        HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("status", status);
            userMap.put("gender", gender);
            userMap.put("relationshipstatus", relationshipstatus);
            userMap.put("generation", generation);


            SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete( Task task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        DynamicToast.makeSuccess( SettingsActivity.this, "อัพเดตข้อมูลส่วนตัวสร็จสิ้น").show();
//                        Toast.makeText(SettingsActivity.this,"อัพเดตการตั้งค่าบัญชีเสร็จสิ้น",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        DynamicToast.makeError( SettingsActivity.this, "เกิดข้อผิดพลาด: อัพเดตข้อมูลล้มเหลว").show();
//                        Toast.makeText(SettingsActivity.this,"เกิดข้อผิดพลาด: ",Toast.LENGTH_SHORT).show();
                    }
                }

            });
    }

    private void   SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
