package ku.ac.th;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private RecyclerView postlist;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;

    //Authentication
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,PostsRef, LikesRef;

    String currentUserID;
    Boolean LikeChecker = false;





    public MainActivity() {
    }


    @SuppressLint({"CutPasteId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = (FirebaseDatabase.getInstance().getReference().child("Users"));
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");



        mToolbar = findViewById(R.id.main_page_toolbar);
        mToolbar.setTitle("หน้าหลัก");

        AddNewPostButton = (ImageButton) findViewById(R.id.add_new_post_button);


        drawerLayout = findViewById(R.id.drawable_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);

        postlist = (RecyclerView) findViewById(R.id.all_user_post_list);
        postlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postlist.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        NavProfileImage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName = (TextView) navView.findViewById(R.id.nav_user_full_name);



        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("username")){
                        String username = dataSnapshot.child("username").getValue().toString();
//                        Log.i("username",username);
                        NavProfileUserName.setText(username);
                    }
                    if (dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else {

                        Toast.makeText(MainActivity.this,"Profile name do not exists ...",Toast.LENGTH_SHORT).show();
                        SendUserToSetupActivity();
                    }




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelector(item);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPostActivity();
            }
        });

        DisplayAllUsersPosts();

    }

    public void updateUserStatus(String state)
    {
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        UserRef.child(currentUserID).child("userState")
                .updateChildren(currentStateMap);
    }

    private void DisplayAllUsersPosts()
    {
        Query SortPostsInDecendingOrder = PostsRef.orderByChild("counter");

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_posts_layout,
                                PostsViewHolder.class,
                                SortPostsInDecendingOrder

                        )

                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {

                        final String PostKey = getRef(position).getKey();

                        viewHolder.setUsername(model.getUsername());
                        viewHolder.setUsernamepost(model.getUsername()); //ดึงมาจาก Username ให้แสดงใต้รูปภ่พ
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());
                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());

                        viewHolder.setLikeButtonStatus(PostKey);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent  clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);
                                clickPostIntent.putExtra("PostKey",PostKey);
                                startActivity(clickPostIntent);
                            }
                        });

                        viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent  commentsIntent = new Intent(MainActivity.this,CommentsActivity.class);
                                commentsIntent.putExtra("PostKey",PostKey);
                                startActivity(commentsIntent);
                            }
                        });

                        viewHolder.LikePostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                LikeChecker = true;

                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        if (LikeChecker.equals(true))
                                        {
                                            if (dataSnapshot.child(PostKey).hasChild(currentUserID))
                                            {
                                                LikesRef.child(PostKey).child(currentUserID).removeValue();
                                                LikeChecker = false;
                                            }
                                            else
                                            {
                                                LikesRef.child(PostKey).child(currentUserID).setValue(true);
                                                LikeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }
                        });

                    }
                };
        postlist.setAdapter(firebaseRecyclerAdapter);

        updateUserStatus("online");
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        ImageButton LikePostButton, CommentPostButton;
        TextView DisplayNoOfLikes;
        int countLikes; //นับยอดถูกใจ
        String currentUserId;
        DatabaseReference LikeRef;


        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            LikePostButton = (ImageButton) mView.findViewById(R.id.like_button);
            CommentPostButton = (ImageButton) mView.findViewById(R.id.comment_button);
            DisplayNoOfLikes = (TextView) mView.findViewById(R.id.display_no_of_likes);

            LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setLikeButtonStatus(final String PostKey)
        {
            LikeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.like);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes) + " " + ("ถูกใจ"));
                    }
                    else
                    {
                        countLikes = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        LikePostButton.setImageResource(R.drawable.dislike);
                        DisplayNoOfLikes.setText(Integer.toString(countLikes) + " " + ("ถูกใจ"));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        public void setUsername(String username)
        {
            TextView nameuser = (TextView) mView.findViewById(R.id.post_user_name);
            nameuser.setText(username);

        }
        public void setProfileimage(Context cxt, String profileimage)
        {
           CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
           Picasso.with(cxt).load(profileimage).into(image);
        }
        public void setTime(String time)
        {
            TextView PostTime = (TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("   " + time);
        }
        public void setDate(String date)
        {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("   " + date);
        }
        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);

        }
        public void setPostimage(Context ctx, String postimage)
        {
            ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(PostImage);
        }
        public void setUsernamepost(String usernamepost)
        {
            TextView nameuserpost = (TextView) mView.findViewById(R.id.text_username);
            nameuserpost.setText(usernamepost);
        }

    }


    private void SendUserToPostActivity()
    {
        Intent addNewPostIntent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);
    }

    @Override
    protected void onStart() {
            super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
        SendUserToLoginActivity();
    }else {
        CheckUserExistence();
    }

}

        private void CheckUserExistence() {
            final String current_user_id = mAuth.getCurrentUser().getUid();
            UserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.d("current",current_user_id);
//                    Log.i("current",current_user_id);
//                    Log.i("datasnap", String.valueOf(dataSnapshot));
                    if (!dataSnapshot.hasChild(current_user_id))
                    {
//                        Log.i("child", String.valueOf(dataSnapshot.hasChild(current_user_id)));
                        SendUserToSetupActivity();

                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_home:
                Toast.makeText(this,"หน้าหลัก", Toast.LENGTH_LONG).show();
                break;


            case R.id.nav_profile:
//                Toast.makeText(this,"โปรไฟล์ของคุณ", Toast.LENGTH_LONG).show();
                SendUserToProfileActivity();
                break;

            case R.id.nav_post:
//                Toast.makeText(this,"สร้างโพสต์", Toast.LENGTH_LONG).show();
                SendUserToPostActivity();
                break;

            case R.id.nav_friends:
//                Toast.makeText(this,"เพื่อน", Toast.LENGTH_LONG).show();
                SendUserToFriendsActivity();
                break;

            case R.id.nav_find_friends:
//                Toast.makeText(this,"ค้นหาเพื่อน", Toast.LENGTH_LONG).show();
                SendUserToFindFriendsActivity();
                break;

            case R.id.nav_message:
                Toast.makeText(this,"ข้อความ", Toast.LENGTH_LONG).show();
                break;

            case R.id.nav_setting:
//                Toast.makeText(this,"ตั้งค่า", Toast.LENGTH_LONG).show();
                SendUserToSettingActivity();

                break;

            case R.id.nav_logout:
//                Toast.makeText(this,"ออกจากระบบ", Toast.LENGTH_LONG).show();
                updateUserStatus("offline");
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

//
        }
    }

    private void SendUserToSettingActivity()
    {
        Intent settingIntent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingIntent);

    }
    private void SendUserToProfileActivity()
    {
        Intent profileIntent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(profileIntent);

    }
    private void SendUserToFindFriendsActivity()
    {
        Intent findIntent = new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findIntent);

    }
    private void SendUserToFriendsActivity()
    {
        Intent friendIntent = new Intent(MainActivity.this, FriendsActivity.class);
        startActivity(friendIntent);
    }

}
