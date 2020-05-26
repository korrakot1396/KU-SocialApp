package ku.ac.th;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity
{

    private TextView userName, userProfileName, userFullname,  userStatus, userFaculty, userMajor, userGender, userRelation, userGeneration;
    private CircleImageView userProfileImage;
    private Button SendFriendReqButton, DeclineFriendRegButton;

    private DatabaseReference FriendRequestRef, UsersRef, FriendsRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE,  saveCurrentDate;

    private int countFriendsPerson = 0, countPostsPerson = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");


        IntializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String myUsername = dataSnapshot.child("username").getValue().toString();
                    String myUserStatus = dataSnapshot.child("status").getValue().toString();
                    String myUserFullname = dataSnapshot.child("fullname").getValue().toString();
                    String myUserFaculty = dataSnapshot.child("faculty").getValue().toString();
                    String myUserMajor = dataSnapshot.child("major").getValue().toString();
                    String myUserGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myGeneration = dataSnapshot.child("generation").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).into(userProfileImage);
                    Log.i("myImagertest",myProfileImage);

                    userName.setText(myUsername);
                    userStatus.setText("  " + myUserStatus);
                    userFullname.setText("ชื่อจริง " + "  " + myUserFullname);
                    userFaculty.setText("คณะ " + "  " + myUserFaculty);
                    userMajor.setText("สาขาวิชา " + "  " + myUserMajor);
                    userGender.setText("เพศ " + "  " + myUserGender);
                    userRelation.setText("สถานะ " + "  " + myRelationStatus);
                    userGeneration.setText("รุ่น " + "  " + myGeneration);

                    MaintanaceOfButtons();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DeclineFriendRegButton.setVisibility(View.INVISIBLE);
        DeclineFriendRegButton.setEnabled(false);

        if (!senderUserId.equals(receiverUserId))
        {
            SendFriendReqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    SendFriendReqButton.setEnabled(false);

                    if (CURRENT_STATE.equals("not_friends"))
                    {
                        SendFriendRequestToPerson();
                    }

                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAnExistingFriend();
                    }

                }
            });
        }
        else
        {
            DeclineFriendRegButton.setVisibility(View.INVISIBLE);
            SendFriendReqButton.setVisibility(View.INVISIBLE);
        }

    }

    private void UnFriendAnExistingFriend()
    {
        new SweetAlertDialog(PersonProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("คุณแน่ใจแล้วใช่ไหม?")
                .setContentText("ว่าคุณต้องการลบเพื่อนคนนี้")
                .setConfirmText("ยืนยัน")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        FriendsRef.child(senderUserId).child(receiverUserId)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            FriendsRef.child(receiverUserId).child(senderUserId)
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {
                                                                SendFriendReqButton.setEnabled(true);
                                                                CURRENT_STATE = "not_friend";
                                                                SendFriendReqButton.setText("เพิ่มเป็นเพื่อน");
                                                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add_friend, 0, 0, 0);
                                                                //icon เอามาใส่ตรงนี้
                                                                SendFriendReqButton.setBackgroundResource(R.color.color_1);


                                                                DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                                                DeclineFriendRegButton.setEnabled(false);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

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

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                FriendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    FriendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        SendFriendReqButton.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        SendFriendReqButton.setText("ลบเพื่อน");
                                                                                        //icon เอามาใส่ตรงนี้
                                                                                        SendFriendReqButton.setBackgroundResource(R.color.colorDelete);
                                                                                        SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.delete_friends,0, 0,0);


                                                                                        //toast ข้อความ
                                                                                        new SweetAlertDialog(PersonProfileActivity.this)
                                                                                                .setTitleText("คุณได้ยืนยันคำขอเป็นเพื่อนแล้ว")
                                                                                                .show();

                                                                                        DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                                                                        DeclineFriendRegButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });


    }

    private void CancelFriendRequest()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendFriendReqButton.setEnabled(true);
                                                CURRENT_STATE = "not_friend";
                                                SendFriendReqButton.setText("เพิ่มเป็นเพื่อน");
                                                //icon เอามาใส่ตรงนี้
                                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.add_friend,0, 0,0);
                                                DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRegButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintanaceOfButtons()
    {
        FriendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                     {
                        if (dataSnapshot.hasChild(receiverUserId))
                        {
                            String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                CURRENT_STATE = "request_sent";
                                SendFriendReqButton.setText("ยกเลิกคำขอเป็นเพื่อน");
                                //icon เอามาใส่ตรงนี้
                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.request_friend,0, 0,0);

                                DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                DeclineFriendRegButton.setEnabled(false);
                            }
                            else if(request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                SendFriendReqButton.setText("ยืนยัน");

                                //icon ใส่ตรงนี้
                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.confirm_friend,0, 0,0);
                                SendFriendReqButton.setBackgroundResource(R.color.success_btn);




                                DeclineFriendRegButton.setVisibility(View.VISIBLE);
                                DeclineFriendRegButton.setEnabled(true);



                                DeclineFriendRegButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {

                                        CancelFriendRequest();

                                    }
                                });
                            }
                        }
                        else
                        {
                            FriendsRef.child(senderUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(receiverUserId))
                                            {
                                                CURRENT_STATE = "friends";
                                                SendFriendReqButton.setText("ลบเพื่อน");

                                                //icon ใส่ตรงนี้
                                                SendFriendReqButton.setBackgroundResource(R.color.colorDelete);
                                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.delete_friends,0, 0,0);

                                                DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRegButton.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
    }

    private void SendFriendRequestToPerson()
    {
        FriendRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                SendFriendReqButton.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                SendFriendReqButton.setText("ยกเลิกคำขอเป็นเพื่อน");

                                                SendFriendReqButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.request_friend,0, 0,0);
                                                //icon เอามาใส่ตรงนี้

                                                DeclineFriendRegButton.setVisibility(View.INVISIBLE);
                                                DeclineFriendRegButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void IntializeFields()
    {
        userStatus = (TextView) findViewById(R.id.person_profile_status);
        userName = (TextView) findViewById(R.id.person_username);
        userFullname = (TextView) findViewById(R.id.person_fullname);
        userFaculty = (TextView) findViewById(R.id.person_faculty);
        userMajor = (TextView) findViewById(R.id.person_major);
        userGender = (TextView) findViewById(R.id.person_gender);
        userRelation =  (TextView) findViewById(R.id.person_relationship_status);
        userProfileImage= (CircleImageView) findViewById(R.id.person_profile_image);
        userGeneration = (TextView) findViewById(R.id.person_generation);

        SendFriendReqButton = (Button) findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRegButton = (Button) findViewById(R.id.person_decline_friend_request);

        CURRENT_STATE = "not_friends";
    }
}
