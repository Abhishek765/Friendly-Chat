package com.rup.registeractivity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FriendsList extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView textView;
    private DatabaseReference FriendsRef,PostRef;
    private  Query query;
    private RecyclerView postList;

    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);


        //mAuth = FirebaseAuth.getInstance();
        //online_user_id=mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        // PostRef= FirebaseDatabase.getInstance().getReference().child("posts");
        textView=(TextView)findViewById(R.id.text1);
        postList=(RecyclerView)findViewById(R.id.friendRecycleList);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        postList.setLayoutManager(linearLayoutManager);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(FriendsList.this, MainActivity.class));
                Toast.makeText(FriendsList.this, "Log out successfully!!", Toast.LENGTH_SHORT).show();

            }
        });

        DisplayPosts();

    }

    private void DisplayPosts()
    {
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder>firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                (
                        Friends.class,
                        R.layout.recycle_list_single_user,
                        FriendsViewHolder.class,
                        FriendsRef

                )
        {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position)
            {
                final String usersIDs = getRef(position).getKey();

                FriendsRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName= dataSnapshot.child("name").getValue().toString();

                        viewHolder.setName(userName);


                        final String finalUserName = userName;

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent= new Intent(FriendsList.this,ChatActivity.class);
                                intent.putExtra("user_id",usersIDs);
                                intent.putExtra("user_name", finalUserName);

                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setName(String name)
        {
            TextView titlename=(TextView)mView.findViewById(R.id.textViewSingleListName);
            titlename.setText(name);
        }

    }
}