package ku.ac.th;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class PostActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private ImageView SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;


    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private String Description;

    private StorageReference PostsImagesReference;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;


    private String saveCurrentDate,  saveCurrentTime, postRandomName, downloadUrl, current_user_id;
    private long countPosts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        PostsImagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

       SelectPostImage = (ImageView) findViewById(R.id.select_post_image);
       UpdatePostButton = (Button) findViewById(R.id.edit_post_button);
       PostDescription = (EditText) findViewById(R.id.post_description) ;

       loadingBar = new ProgressDialog(this);


        //สร้าง Toolbar
//        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("อัพเดตโพสต์");

        //สร้าง Toolbar ของ สร้างโพสต์ใหม่
        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("สร้างโพสต์ใหม่");

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });

    }

    private void ValidatePostInfo()
    {
        Description = PostDescription.getText().toString();

        if (ImageUri == null)
        {
            DynamicToast.makeError( PostActivity.this, "กรุณาเลือกรูปภาพที่ต้องการโพสต์").show();
//            Toast.makeText(PostActivity.this,"กรุณาเลือกรูปภาพที่ต้องการโพสต์",Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(Description)) {
            DynamicToast.makeError( PostActivity.this, "กรุณาเระบุข้อความที่ต้องการโพสต์").show();
//            Toast.makeText(PostActivity.this, "กรุณาเระบุข้อความที่ต้องการโพสต์", Toast.LENGTH_SHORT).show();

        } else{

            loadingBar.setTitle("กำลังทำการเพิ่มโพสต์ใหม่");
            loadingBar.setMessage("กรุณารอสักครู่ , โพสต์ของคุณกำลังเพิ่ม...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);


            StoringImageToFirebaseStorage();
        }

    }

    private void StoringImageToFirebaseStorage()
    {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostsImagesReference.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    downloadUrl = task.getResult().getDownloadUrl().toString();



                    //Dialog Popup ของ โพสต์เสร็จสิ้น
//                    new SweetAlertDialog(PostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText("โพสต์เสร็จสิ้น")
//                            .setContentText("คุณได้ทำการโพสต์เสร็จสิ้นแล้ว")
//                            .setConfirmText("ตกลง")
//                            .show();



//                    Toast.makeText(PostActivity.this,"โพสต์เสร็จสิ้น",Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();
                }
                else {
                    String message = task.getException().getMessage();
                    DynamicToast.makeError( PostActivity.this, "เกิดข้อผิดพลาด: " + message).show();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase()
    {
        PostsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts  = dataSnapshot.getChildrenCount();
                }
                else
                {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String userFullName =  dataSnapshot.child("username").getValue().toString();
                    final String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                        postsMap.put("uid", current_user_id);
                        postsMap.put("date",saveCurrentDate);
                        postsMap.put("time", saveCurrentTime);
                        postsMap.put("description", Description);
                        postsMap.put("postimage", downloadUrl);
                        postsMap.put("profileimage", userProfileImage);
                        postsMap.put("username",userFullName);
                        postsMap.put("counter", countPosts);

                    PostsRef.child(current_user_id + postRandomName ).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful()){


                                        new SweetAlertDialog(PostActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("โพสต์เสร็จสิ้น")
                                                .setContentText("คุณได้ทำการโพสต์เสร็จสิ้นแล้ว")
                                                .setConfirmText("ตกลง")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        SendUserToMainActivity();

                                                    }
                                                })
                                                .show();
                                        DynamicToast.makeSuccess( PostActivity.this, "คุณได้ทำการโพสต์เสร็จสิ้นแล้ว").show();


//                                        Toast.makeText(PostActivity.this,"คุณได้ทำการอัพเดตโพสต์",Toast.LENGTH_LONG).show();
//                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
//                                        Toast.makeText(PostActivity.this, "เกิดความผิดพลาด: สำหรับการอัพเดตโพสต์ ",Toast.LENGTH_SHORT).show();
                                        DynamicToast.makeError( PostActivity.this, "เกิดความผิดพลาด: สำหรับการอัพเดตโพสต์ ").show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void OpenGallery() {
        Intent galleryIntent =  new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
