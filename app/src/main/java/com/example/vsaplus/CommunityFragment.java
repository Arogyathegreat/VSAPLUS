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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
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

    FirebaseRecyclerAdapter<PostModel,PostViewHolder> adapter1;
    FirebaseRecyclerAdapter<PostModel,PostViewHolder> adapter2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("community");
    private RecyclerView noticeview;
    private RecyclerView normalview;
    private Button write;
    private Button seemore;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community_notice,container,false);
        noticeview = view.findViewById(R.id.noticeView);
        normalview = view.findViewById(R.id.userCommunityList);
        write = view.findViewById(R.id.create_post);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(WriteCommunityFragment.newInstance());
            }
        });

        noticeview.setLayoutManager(new LinearLayoutManager(getActivity()));
        normalview.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Query query1 = myRef.child("post").orderByKey().limitToFirst(10);
            setnormalRecyclerView(query1);
            Query query2 = myRef.child("post").orderByChild("Like").limitToLast(1);
            setnoticeRecyclerView(query2);
        }
        else{
            ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
        }
        seemore = view.findViewById(R.id.post_seemore);
        seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(NormalCommunityFragment.newInstance());
            }
        });
        return view;
    }
    public void setnoticeRecyclerView(Query query){
        FirebaseRecyclerOptions<PostModel> options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query,PostModel.class)
                .build();
        adapter1 = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int position, @NonNull PostModel postModel) {
                postViewHolder.setPostname(postModel.gettitle(),postModel.getUserName(),postModel.getContent(),postModel.getUserUid(),postModel.getLike(),postModel.getReply(),postModel.getPostnum());
                Log.d("Start", "posttitle data: " +  postModel.gettitle() );
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_board, parent, false);
                return new PostViewHolder(view);
            }
        };
        noticeview.setAdapter(adapter1);
        adapter1.startListening();
    }
    public void setnormalRecyclerView(Query query) {
        FirebaseRecyclerOptions<PostModel> options = new FirebaseRecyclerOptions.Builder<PostModel>()
                .setQuery(query,PostModel.class)
                .build();

        adapter2 = new FirebaseRecyclerAdapter<PostModel, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int position, @NonNull PostModel postModel) {
                postViewHolder.setPostname(postModel.gettitle(),postModel.getUserName(),postModel.getContent(),postModel.getUserUid(),postModel.getLike(),postModel.getReply(),postModel.getPostnum());

                Log.d("Start", "posttitle data: " +  postModel.gettitle() );
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_user_board, parent, false);
                return new PostViewHolder(view);
            }
        };
        normalview.setAdapter(adapter2);
        adapter2.startListening();
    }

public class PostViewHolder extends RecyclerView.ViewHolder {

    View mView;

    PostViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    void setPostname(final String title,final String username,final String content,final String UserUid,final int like,final int reply,final int postnum) {
        TextView nameView = mView.findViewById(R.id.title);//fix
        TextView userView = mView.findViewById(R.id.user_ID);//fix
        TextView LikeView = mView.findViewById(R.id.likeCount);
        TextView ReplyView = mView.findViewById(R.id.commentCount);
        LinearLayout clickview = mView.findViewById(R.id.clickview);

        nameView.setText(title);
        userView.setText(username);
        LikeView.setText(like+"");
        ReplyView.setText(reply+"");
        clickview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PostViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title",title);
                bundle.putString("content", content);
                bundle.putInt("Like",like);
                bundle.putString("UserName",username);
                bundle.putInt("postnum",postnum);
                bundle.putString("UserUid",UserUid);
                bundle.putInt("Reply",reply);
                fragment.setArguments(bundle);
                ((MainActivity)getActivity()).loadFragment(fragment);
            }
        });

    }

}}