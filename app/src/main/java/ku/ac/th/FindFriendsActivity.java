package ku.ac.th;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity
{

    private androidx.appcompat.widget.Toolbar mToolbar;
    private ImageButton SearchButton;
    private EditText SearchInputText;

    private RecyclerView SearchResultList;

    private DatabaseReference allUsersDatabaseRef;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //สร้าง Toolbar ของ สร้างโพสต์ใหม่
        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.find_friends_appbar_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle("ค้นหาเพื่อน");

        SearchResultList = (RecyclerView) findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

        SearchButton = (ImageButton) findViewById(R.id.search_people_friends_button);
        SearchInputText = (EditText) findViewById(R.id.search_box_input);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String searchBoxInput = SearchInputText.getText().toString();

                SearchPeopleAndFriends(searchBoxInput);
            }
        });
    }

    private void SearchPeopleAndFriends(String searchBoxInput)
    {
        Toast.makeText(FindFriendsActivity.this,"กำลังค้นหา...",Toast.LENGTH_SHORT).show();

        Query searchPeopleFriendsQuery = allUsersDatabaseRef.orderByChild("username")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

      FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder> firebaseRecyclerAdapter
              = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>
              (
                      FindFriends.class,
                      R.layout.all_users_display_find_friend_layout,
                      FindFriendsViewHolder.class,
                      searchPeopleFriendsQuery


              )
      {
          @Override
          protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, final int position)
          {
              viewHolder.setUsername(model.getUsername());
              viewHolder.setFaculty(model.getFaculty());
              viewHolder.setProfileimage(getApplicationContext(),model.getProfileimage());

              viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v)
                  {
                      String visit_user_id = getRef(position).getKey();

                      Intent profileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                      profileIntent.putExtra("visit_user_id", visit_user_id);
                      startActivity(profileIntent);
                  }
              });

          }
      };
      SearchResultList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public FindFriendsViewHolder( View itemView) {
            super(itemView);
            this.mview = itemView;
        }

        public void setProfileimage(Context cxt, String profileimage)
        {
            CircleImageView myImagesearch = (CircleImageView) mview.findViewById(R.id.all_user_profile_pic);
//            Picasso.with(cxt).load(profileimage).placeholder(R.drawable.profile).into(myImagesearch);
            Picasso.with(cxt).load(profileimage).into(myImagesearch);
        }

        public void setUsername(String username)
        {

            TextView myUsername = (TextView) mview.findViewById(R.id.all_users_profile_username);
            myUsername.setText(username);

        }
        public void setFaculty(String faculty)
        {
            TextView myFaculty = (TextView) mview.findViewById(R.id.all_users_profile_faculty);
            myFaculty.setText(faculty);
        }

    }

}
