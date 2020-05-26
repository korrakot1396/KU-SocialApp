package ku.ac.th;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class SetupActivity extends AppCompatActivity {

    private EditText Username, Fullname;
    private Button SaveInformationbuttion;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;

    String currentUserID;

    final static int Gallery_Pick = 1;

    private Spinner Faculty, Major;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        Log.i("userid",currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);;
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile Image");

        Username = (EditText) findViewById(R.id.setup_username);
        Fullname = (EditText) findViewById(R.id.setup_full_name);
//        Faculty = (EditText) findViewById(R.id.setup_faculty_name);
//        Major = (EditText) findViewById(R.id.setup_major_name);
        SaveInformationbuttion = (Button) findViewById(R.id.setup_information_button);
        ProfileImage = (CircleImageView) findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);


        Faculty = (Spinner)  findViewById(R.id.setup_information_faculty);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty));

        myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        Faculty.setAdapter(myAdapter);



        Faculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                if (position == 0)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.major_select));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 1)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_angriculture));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 2)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_business));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 3)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_fisheries));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 4)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_human));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 5)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_forest));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 6)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_science));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 7)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_engineer));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 8)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_education));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 9)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_economic));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 10)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_architecture));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 11)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_social));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 12)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_veterinary));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 13)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_agro_industry));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 14)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_vettech));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
                else if (position == 15)
                {
                    Major = (Spinner)  findViewById(R.id.setup_information_major);

                    final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SetupActivity.this,
                            R.layout.support_simple_spinner_dropdown_item, getResources().getStringArray(R.array.faculty_environment));

                    myAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    Major.setAdapter(myAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });

        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent =  new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profileimage")){

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(ProfileImage);
                    }
                    else {
                        DynamicToast.makeError( SetupActivity.this, "กรุณาเลือกรูปภาพโปรไฟล์ของคุณ").show();
//                        Toast.makeText(SetupActivity.this,"กรุณาเลือกรูปภาพโปรไฟล์ของคุณ.",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    DynamicToast.makeError( SetupActivity.this, "กรุณาเลือกรูปภาพโปรไฟล์ของคุณ").show();
//                    Toast.makeText(SetupActivity.this, "กรุณาเลือกรูปภาพโปรไฟล์ของคุณ.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
//                loadingBar.show();
//                loadingBar.setCanceledOnTouchOutside(true);

                final SweetAlertDialog pDialog = new SweetAlertDialog(SetupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("รูปภาพของคุณกำลังถูกบันทึก...");
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
//                            Toast.makeText(SetupActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UsersRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                                startActivity(selfIntent);

                                                DynamicToast.makeSuccess(SetupActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น" ).show();
//                                                Toast.makeText(SetupActivity.this, "รูปภาพอัพโหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                DynamicToast.makeError( SetupActivity.this, "เกิดข้อผิดพลาด: " + message).show();
//                                                Toast.makeText(SetupActivity.this, "เกิดข้อผิดพลาด: " + message, Toast.LENGTH_SHORT).show();
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
                DynamicToast.makeError( SetupActivity.this, "เกิดข้อผิดพลาด: Image can not be cropped. Try Again").show();
//                Toast.makeText(this, "เกิดข้อผิดพลาด: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
//                loadingBar.dismiss();
            }
        }
    }





    private void SaveAccountSetupInformation() {
        String username = Username.getText().toString();
        String fullname = Fullname.getText().toString();
        String faculty = Faculty.getSelectedItem().toString();
        String major = Major.getSelectedItem().toString();

        if (TextUtils.isEmpty(username)){
            DynamicToast.makeError( SetupActivity.this, "กรุณากรอกชื่อบัญชี").show();
//            Toast.makeText(this,"กรุณากรอกชื่อบัญชี",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(fullname)){
            DynamicToast.makeError( SetupActivity.this, "กรุณากรอกชื่อ-นามสกุล").show();
//            Toast.makeText(this,"กรุณากรอกชื่อ-นามสกุล",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(faculty)){
            DynamicToast.makeError( SetupActivity.this, "กรุณากรอกชื่อคณะ").show();
//            Toast.makeText(this,"กรุณากรอกชื่อคณะ",Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(major)){
            DynamicToast.makeError( SetupActivity.this, "กรุณากรอกชื่อสาขาวิชา").show();
//            Toast.makeText(this,"กรุณากรอกชื่อสาขาวิชา",Toast.LENGTH_LONG).show();
        }
        else {

//            loadingBar.setTitle("กำลังบันทึกข้อมูลส่วนตัว");
//            loadingBar.setMessage("กรุณารอสักครู่ กำลังทำการบันทึกข้อมูล...");
//            loadingBar.show();
//            loadingBar.setCanceledOnTouchOutside(true);


            final SweetAlertDialog pDialog = new SweetAlertDialog(SetupActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("กำลังทำการบันทึกข้อมูล...");
            pDialog.setCancelable(true);
            pDialog.show();


            HashMap userMap = new HashMap();
            userMap.put("username",username);
            userMap.put("fullname",fullname);
            userMap.put("faculty",faculty);
            userMap.put("major",major);
            userMap.put("status"," ");
            userMap.put("gender","-");
            userMap.put("role","user");
            userMap.put("relationshipstatus","-");
            userMap.put("generation", "-");

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()){
                        SendUserToMainActivity();
                        DynamicToast.makeSuccess( SetupActivity.this, "บันทึกข้อมูลเสร็จสิ้น").show();
//                        Toast.makeText(SetupActivity.this,"บันทึกข้อมูลเสร็จสิ้น",Toast.LENGTH_LONG).show();
                        pDialog.dismiss();


                    }else {
                        String message = task.getException().getMessage();
                        DynamicToast.makeError( SetupActivity.this, "เกิดข้อผิดพลาด: "+message).show();
//                        Toast.makeText(SetupActivity.this,"เกิดข้อผิดพลาด: "+message,Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity()
    {

        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
//        Toast.makeText(SetupActivity.this,"เรียก SendUserToMainActivity()",Toast.LENGTH_LONG).show();
    }
}
