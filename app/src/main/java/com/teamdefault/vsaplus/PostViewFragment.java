package com.teamdefault.vsaplus;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class PostViewFragment extends Fragment {
    FirebaseRecyclerAdapter<ReplyModel,ReplyViewHolder> adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("community");
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database1.getReference("users");
    int postnum = 0;
    boolean liked = false;
    ImageButton delete;
    String title = null;
    String contents = null;
    String userUid=null;
    int Like = 0;
    String Username = null;
    int replynum = 0;
    private RecyclerView replyView;
    private EditText writecomment;
    public static PostViewFragment newInstance() {
        return new PostViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_postview,container,false);
        if(user == null){
            Toast.makeText(getContext(),"login first!",Toast.LENGTH_SHORT).show();
            ((MainActivity)getActivity()).loadFragment(SignupFragment.newInstance());
        }
             title = getArguments().getString("title");
             contents = getArguments().getString("content");
             Like = getArguments().getInt("Like");
             Username = getArguments().getString("UserName");
             postnum = getArguments().getInt("postnum");
             userUid = getArguments().getString("UserUid");
            replynum = getArguments().getInt("Reply");
        delete = v.findViewById(R.id.delete_post);
        TextView username = v.findViewById(R.id.user_ID);
        TextView titlepost = v.findViewById(R.id.post_title);
        TextView actualpost = v.findViewById(R.id.actual_post);
        TextView Likecount = v.findViewById(R.id.likeCount);
        TextView commentcount = v.findViewById(R.id.commentCount);
        ImageView profileimage = v.findViewById(R.id.firebase_profileImage);
        ImageButton commentbutton = v.findViewById(R.id.comment_post);
         writecomment = v.findViewById(R.id.write_comment);
        ImageButton replybutton = v.findViewById(R.id.post_comment);
        ImageButton likebutton = v.findViewById(R.id.like_post);
        ImageButton exit = v.findViewById(R.id.exit_post);
        TextView upload_date = v.findViewById(R.id.post_date);
        String date = new SimpleDateFormat("MM/dd hh:mm", Locale.getDefault()).format(new Date());
        upload_date.setText(date);
        RelativeLayout comment = v.findViewById(R.id.comment);
        comment.setVisibility(View.GONE);
        replyView = v.findViewById(R.id.comment_view);
        replyView.setLayoutManager(new LinearLayoutManager(getActivity()));
        commentcount.setText(replynum+"");
        username.setText(Username);
        titlepost.setText(title);
        actualpost.setText(contents);
        Likecount.setText(Like+"");
        if(userUid.equals(user.getUid())){
            delete.setVisibility(View.VISIBLE);
        }
        else{
            delete.setVisibility(View.GONE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("post").child(postnum-2*postnum+"").removeValue();
                ((MainActivity)getActivity()).loadFragment(CommunityFragment.newInstance());
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).loadFragment(CommunityFragment.newInstance());
            }
        });
        likebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child("post").child(postnum-2*postnum+"").runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        PostModel postModel = mutableData.getValue(PostModel.class);
                        if(postModel==null){
                            return Transaction.success(mutableData);
                        }
                        if(postModel.getLikepeople() == null||!(postModel.getLikepeople().containsKey(user.getUid()))){
                            postModel.setLike(++Like);
                            HashMap<String,Boolean> liker = new HashMap<>();
                            liker.put(user.getUid(),true);
                            postModel.setLikepeople(liker);

                        }
                        else{
                            postModel.setLike(--Like);
                            postModel.getLikepeople().remove(user.getUid());
                        }
                        mutableData.setValue(postModel);
                        return Transaction.success(mutableData);
                    }
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        Likecount.setText(Like+"");
                    }
                });

            }
        });
        commentbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment.setVisibility(View.VISIBLE);
            }
        });
        replybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(writecomment.getText() == null || writecomment.getText().equals("")){
                    Toast.makeText(getContext(),"put your comment!",Toast.LENGTH_SHORT).show();
                    return;
                }
                writecomment.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(writecomment.getWindowToken(), 0);
                String reply = writecomment.getText().toString();
                HashMap<String,Object> repl = new HashMap<>();
                repl.put("userName",user.getDisplayName());
                repl.put("comment",reply);
                repl.put("picture",String.valueOf(user.getPhotoUrl()));
                HashMap<String,Object> replymodel = new HashMap<>();
                replymodel.put(replynum+1+"num",repl);
                myRef.child("post").child(postnum-2*postnum+"").child("comments")
                        .updateChildren(replymodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            writecomment.setText("");
                            comment.setVisibility(View.GONE);
                            replynum = replynum+1;
                            myRef.child("post").child(postnum-2*postnum+"").child("reply").setValue(replynum);
                            Query query = myRef.child("post").child(postnum-2*postnum+"").child("comments").orderByValue();
                            setRecyclerView(query);
                            commentcount.setText(replynum+"");
                        }
                    }
                });
            }
        });
//        userRef.child(userUid).child("photo").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String photo = dataSnapshot.getValue().toString();
//                if(photo == null || photo.equals("")||photo.equals("null")) return;
//                Picasso.get()
//                        .load(photo)
//                        .fit()
//                        .centerInside()
//                        .into(profileimage);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        if(replynum > 0){
            Query query = myRef.child("post").child(postnum-2*postnum+"").child("comments").orderByValue();
            setRecyclerView(query);
        }
        return v;
    }
    public void setRecyclerView(Query query){
        FirebaseRecyclerOptions<ReplyModel> options = new FirebaseRecyclerOptions.Builder<ReplyModel>()
                .setQuery(query,ReplyModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ReplyModel, ReplyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReplyViewHolder replyViewHolder, int i, @NonNull ReplyModel replyModel) {
                replyViewHolder.setReply(replyModel.getUserName(),replyModel.getComment(),replyModel.getPicture());
            }

            @NonNull
            @Override
            public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_usercomment,parent,false);
                return new ReplyViewHolder(view);
            }
        };
        replyView.setAdapter(adapter);
        adapter.startListening();
    }

public class ReplyViewHolder extends RecyclerView.ViewHolder{
        View mView;
    ReplyViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }
    void setReply(String userName,String comment,String picture){
        TextView contents = mView.findViewById(R.id.title);
        TextView userID = mView.findViewById(R.id.user_ID);
        contents.setText(comment);
        userID.setText(userName);

        ImageView pic = mView.findViewById(R.id.firebase_profileImage);
        Picasso.get()
                .load(picture)
                .fit()
                .centerInside()
                .into(pic);
    }
}
}