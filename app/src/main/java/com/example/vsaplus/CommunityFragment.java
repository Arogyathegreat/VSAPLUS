package com.example.vsaplus;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CommunityFragment extends Fragment {

    public static CommunityFragment newInstance(){
        return new CommunityFragment();
    }

    FirebaseRecyclerAdapter<PostModel,PostViewHolder> adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("community");
    private RecyclerView noticeview;
    private Button write;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_notice,container,false);
        noticeview = view.findViewById(R.id.userCommunityList);
        write = view.findViewById(R.id.create_post);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(WriteCommunityFragment.newInstance());
            }
        });
        noticeview.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Query query = myRef.child("post").orderByValue();
            setRecyclerView(query);
        }
        else{
            ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
        }
        return view;
    }
    public void setRecyclerView(Query query) {
        FirebaseRecyclerOptions<PostModel> options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query,PostModel.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int position, @NonNull PostModel postModel) {
                postViewHolder.setPostname(postModel.gettitle(), postModel.getUserUID(),getActivity());

                Log.d("Start", "posttitle data: " +  postModel.gettitle() );
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_board, parent, false);
                return new PostViewHolder(view);
            }
        };
        noticeview.setAdapter(adapter);
        adapter.startListening();
    }

public class PostViewHolder extends RecyclerView.ViewHolder {

    View mView;

    PostViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    void setPostname(final String PostTitle, final String UserUid, final Activity activity) {
        TextView nameView = mView.findViewById(R.id.title);//fix
        TextView userView = mView.findViewById(R.id.user_ID);//fix
        ImageButton removal = mView.findViewById(R.id.delete_bookmark);
        nameView.setText(PostTitle);

        userView.setText(UserUid);
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //frament->community
            }
        });

    }
}}