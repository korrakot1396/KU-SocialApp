package ku.ac.th;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClickPostActivity extends AppCompatActivity {

    private ImageView PostImage;
    private TextView PostDescription;
    private Button DeletePostButton, EditPostButton;

    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;

    private String PostKey, currentUserID, databaseUserID, description, image;

    private androidx.appcompat.widget.Toolbar mToolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        PostImage = (ImageView) findViewById(R.id.click_post_image);
        PostDescription = (TextView) findViewById(R.id.click_post_description);
        EditPostButton = (Button) findViewById(R.id.edit_post_button);
        DeletePostButton = (Button) findViewById(R.id.delete_post_button);

        DeletePostButton.setVisibility(View.INVISIBLE);
        EditPostButton.setVisibility(View.INVISIBLE);


        //สร้าง Toolbar ของ แก้ไขโพสต์
        mToolbar = (Toolbar) findViewById(R.id.edit_page_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("แก้ไขโพสต์");

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postimage").getValue().toString();
                    databaseUserID = dataSnapshot.child("uid").getValue().toString();

                    PostDescription.setText(description);
                    Picasso.with(ClickPostActivity.this).load(image).into(PostImage);

                    if (currentUserID.equals(databaseUserID))
                    {
                        DeletePostButton.setVisibility(View.VISIBLE);
                        EditPostButton.setVisibility(View.VISIBLE);
                    }

                    EditPostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            EditCurrentPost(description);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeletePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DeleteCurrentPost();
            }
        });

    }

    private void EditCurrentPost(String description)
    {
        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);


       new SweetAlertDialog(ClickPostActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("แก้ไขโพสต์")
                .setConfirmText("อัพเดต")
               .setCustomView(inputField)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        ClickPostRef.child("description").setValue(inputField.getText().toString());

                        sDialog
                                .setTitleText("อัพเดตโพสต์เสร็จสิ้น")
                                .setContentText("คุณได้ทำการอัพเดตโพสต์แล้ว!")
                                .setConfirmText("ตกลง")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        DynamicToast.makeSuccess( ClickPostActivity.this, "คุณได้ทำการอัพเดตโพสต์แล้ว").show();


//                        sDialog.dismissWithAnimation();


                    }
                })
//
                .show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);.setCancelButton("ยกเลิก", new SweetAlertDialog.OnSweetClickListener() {
////                    @Override
////                    public void onClick(SweetAlertDialog sDialog) {
////                        sDialog
////                                .dismissWithAnimation();
////                    }
////                })
//        builder.setTitle("แก้ไขโพสต์: ");
//
//        final EditText inputField = new EditText(ClickPostActivity.this);
//        inputField.setText(description);
//        builder.setView(inputField);
//
//        builder.setPositiveButton("อัพเดต", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                ClickPostRef.child("description").setValue(inputField.getText().toString());
//                Toast.makeText(ClickPostActivity.this,"คุณได้อัพเดตโพสต์แล้ว",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//                dialog.cancel();
//            }
//        });
//        Dialog dialog = builder.create();
//        dialog.show();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void DeleteCurrentPost()
    {

        SweetAlertDialog  myQuittingDialogBox = AlertDialogDelete();
        myQuittingDialogBox.show();
//        showdeleteAlertDialog();

    }

    //Alert ของ Delete Post
    private SweetAlertDialog AlertDialogDelete() {

        SweetAlertDialog myQuittingDialogBox = new SweetAlertDialog(ClickPostActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("คุณแน่ใจใช่ไหม?")
                .setContentText("ว่าคุณต้องการจะลบโพสต์นี้!")
                .setConfirmText("ยืนยัน")

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        ClickPostRef.removeValue();
                        DynamicToast.makeSuccess( ClickPostActivity.this, "คุณได้ทำการลบโพสต์แล้ว").show();
                        SendUserToMainActivity();



                    }
                })
                .setCancelButton("ยกเลิก",new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                       sweetAlertDialog.dismissWithAnimation();
                    }
                });

        return myQuittingDialogBox;


    }



    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
