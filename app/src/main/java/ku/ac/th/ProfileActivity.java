package ku.ac.th;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView userName, userProfileName, userFullname,  userStatus, userFaculty, userMajor, userGender, userRelation, userGeneration,  countShowPosts, countShowFriends;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, FriendsRef, PostsRef;
    private FirebaseAuth mAuth;
    private Button MyPosts, MyFriends;

    private String currentUserId;
    private int countFriends = 0, countPosts = 0;

//    private androidx.appcompat.widget.Toolbar mToolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_2);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        //สร้าง Toolbar ของ ตั้งค่า
//        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        mToolbar.setTitle("โปรไฟล์ของคุณ");

        userStatus = (TextView) findViewById(R.id.my_profile_status);
        userName = (TextView) findViewById(R.id.my_username);
        userFullname = (TextView) findViewById(R.id.my_fullname);
        userFaculty = (TextView) findViewById(R.id.my_faculty);
        userMajor = (TextView) findViewById(R.id.my_major);
        userGender = (TextView) findViewById(R.id.my_gender);
        userRelation =  (TextView) findViewById(R.id.my_relationship_status);
        userProfileImage= (CircleImageView) findViewById(R.id.my_profile_image);
        userGeneration = (TextView) findViewById(R.id.my_generation);

        countShowPosts = (TextView) findViewById(R.id.count_posts);
        countShowFriends = (TextView) findViewById(R.id.count_friends);


        MyFriends = (Button) findViewById(R.id.my_friends_button);
        MyPosts = (Button) findViewById(R.id.my_post_button);

        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToFriendsActivity();
            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToMyPostsActivity();
            }
        });

        FriendsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countFriends = (int) dataSnapshot.getChildrenCount();
//                    MyFriends.setText("เพื่อน" + " " + Integer.toString(countFriends)  + " คน" );
                    countShowFriends.setText(Integer.toString(countFriends));
                }
                else
                {
//                    MyFriends.setText("คุณยังไม่มีเพื่อน");
                    countShowFriends.setText("0");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
//                            MyPosts.setText("โพสต์" + " " + Integer.toString(countPosts) + " โพสต์");
                            countShowPosts.setText(Integer.toString(countPosts));
                        }
                        else
                        {
//                            MyPosts.setText("คุณยังไม่มีโพสต์");
                            countShowPosts.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String myUsername = dataSnapshot.child("username").getValue().toString();
//                    Log.i("myUsernametest",myUsername);
                    String myUserStatus = dataSnapshot.child("status").getValue().toString();
//                    Log.i("myUserStatustest",myUserStatus);
                    String myUserFullname = dataSnapshot.child("fullname").getValue().toString();
//                    Log.i("myUserFullnametest",myUserFullname);
                    String myUserFaculty = dataSnapshot.child("faculty").getValue().toString();
//                    Log.i("myUserFacultytest",myUserFaculty);
                    String myUserMajor = dataSnapshot.child("major").getValue().toString();
//                    Log.i("myUserMajortest",myUserMajor);
                    String myUserGender = dataSnapshot.child("gender").getValue().toString();
//                    Log.i("myUserGendertest",myUserGender);
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();
//                    Log.i("myUserStatusRe",myRelationStatus);
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
//                    Log.i("myUserprofiletest",myProfileImage);

                    String myGeneration = dataSnapshot.child("generation").getValue().toString();

                    Picasso.with(ProfileActivity.this).load(myProfileImage).into(userProfileImage);
                    Log.i("myImagertest",myProfileImage);

                    userName.setText(myUsername);
                    userStatus.setText("  " + myUserStatus);
                    userFullname.setText("ชื่อ-นามสกุล " + "  " + myUserFullname);
                    userFaculty.setText("คณะ " + "  " + myUserFaculty);
                    userMajor.setText("สาขาวิชา " + "  " + myUserMajor);
                    userGender.setText("เพศ " + "  " + myUserGender);
                    userRelation.setText("ความสัมพันธ์ " + "  " + myRelationStatus);
                    userGeneration.setText("รุ่น " + "  " + myGeneration);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void SendUserToFriendsActivity()
    {
        Intent friendIntent = new Intent(ProfileActivity.this, FriendsActivity.class);
        startActivity(friendIntent);
    }

    private void SendUserToMyPostsActivity()
    {
        Intent mypostsIntent = new Intent(ProfileActivity.this, MyPostsActivity.class);
        startActivity(mypostsIntent);
    }
}
