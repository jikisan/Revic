package Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.homepage;
import com.example.revic_capstone.profile_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Models.Posts;
import Models.Users;
import Objects.TextModifier;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterPostsItem extends RecyclerView.Adapter<AdapterPostsItem.ItemViewHolder> {

    private List<String> arrPostId;
    private List<Posts> arrPosts;
    private List<Users> arrUsers;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private FirebaseUser user;
    private SweetAlertDialog sDialog;

    private DatabaseReference postDatabase = FirebaseDatabase.getInstance().getReference("Posts");;

    public AdapterPostsItem() {
    }

    public AdapterPostsItem(List<Posts> arrPosts, List<Users> arrUsers, Context context, List<String> arrPostId) {
        this.arrPostId = arrPostId;
        this.arrPosts = arrPosts;
        this.arrUsers = arrUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPostsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posts,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPostsItem.ItemViewHolder holder, int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        final EditText edittext = new EditText(context);
        int currentPosition = position;



        String myUserID = user.getUid();

        Posts posts = arrPosts.get(currentPosition);
        String postId = arrPostId.get(currentPosition);

        String dateCreated = posts.getDateCreated();
        String timeCreated = posts.getTimeCreated();
        String postMessage = posts.getPostMessage();
        String fileUrl = posts.getFileUrl();
        String fileType = posts.getFileType();
        String postUserId = posts.getUserId();

        holder.tv_postDate.setText(dateCreated + " " + timeCreated);
        holder.tv_postMessage.setText(postMessage);

        generateUsersData(postUserId, holder);

        if(fileType.equals("photo"))
        {
            holder.video_postVideo.setVisibility(View.INVISIBLE);
            holder.tv_play.setVisibility(View.INVISIBLE);
            holder.tv_pause.setVisibility(View.INVISIBLE);

            Picasso.get().load(fileUrl)
                    .resize(2560  , 1440 )
                    .onlyScaleDown()
                    .centerCrop()
                    .into(holder.iv_postPhoto);

        }else if(fileType.equals("video"))
        {
            holder.iv_postPhoto.setVisibility(View.INVISIBLE);
            holder.tv_pause.setVisibility(View.INVISIBLE);
            holder.tv_play.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse(fileUrl);
            holder.video_postVideo.setVideoURI(uri);

//            MediaController mediaController = new MediaController(context);
//            mediaController.setAnchorView(holder.video_postVideo);
//            mediaController.setMediaPlayer(holder.video_postVideo);
//            holder.video_postVideo.setMediaController(mediaController);

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.video_postVideo.start();
//                }
//            });

            holder.tv_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.video_postVideo.pause();

                    holder.tv_pause.setVisibility(View.INVISIBLE);
                    holder.tv_play.setVisibility(View.VISIBLE);
                }
            });

            holder.tv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.video_postVideo.start();

                    holder.tv_pause.setVisibility(View.VISIBLE);
                    holder.tv_play.setVisibility(View.INVISIBLE);
                }
            });

            holder.video_postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    mediaPlayer.pause();
                }
            });


        }

        if(!postUserId.equals(myUserID))
        {
            holder.linearLayout6.setVisibility(View.GONE);
        }

        holder.tv_userFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), profile_page.class);
                intent.putExtra("userID", postUserId);
                intent.putExtra("myPosts", "2");
                view.getContext().startActivity(intent);

            }
        });

        holder.tv_editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText et_newPostMessage = new EditText(context);
                et_newPostMessage.setText(postMessage);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Edit your post")
                        .setView(et_newPostMessage)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String newPostMessage = et_newPostMessage.getText().toString();

                                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("postMessage", newPostMessage);

                                postDatabase.child(postId).updateChildren(hashMap);
                                Toast.makeText(context, "Post updated", Toast.LENGTH_SHORT).show();

                                notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();



            }
        });

        holder.tv_deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sDialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE);
                sDialog.setTitleText("Remove Post?");
                sDialog.setCancelText("Back");
                sDialog.setConfirmButton("Remove", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                postDatabase.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                                        {

                                            dataSnapshot.getRef().removeValue();
//                                            arrPostId.remove(currentPosition);
//                                            arrPosts.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                            sDialog.dismiss();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }

                        });
                sDialog.setContentText("Delete post?");
                sDialog.show();

            }
        });
    }

    private void generateUsersData(String postUserId, ItemViewHolder holder) {

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("Users");

        userDatabase.child(postUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Users users = snapshot.getValue(Users.class);
                    String userPhotoUrl = users.getImageUrl();

                    TextModifier textModifier = new TextModifier();

                    textModifier.setSentenceCase(users.getFname());
                    String fname = textModifier.getSentenceCase();

                    textModifier.setSentenceCase(users.getLname());
                    String lname = textModifier.getSentenceCase();
                    String fullName = fname + " " + lname;


                    Picasso.get().load(userPhotoUrl)
                            .into(holder.iv_userPhoto);
                    holder.tv_userFullName.setText(fullName);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrPosts.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userPhoto, iv_postPhoto;
        TextView tv_userFullName, tv_postDate, tv_postMessage, tv_play, tv_pause, tv_editBtn, tv_deleteBtn;
        VideoView video_postVideo;
        LinearLayout linearLayout6;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);
            iv_postPhoto = itemView.findViewById(R.id.iv_postPhoto);

            tv_userFullName = itemView.findViewById(R.id.tv_userFullName);
            tv_postDate = itemView.findViewById(R.id.tv_postDate);
            tv_postMessage = itemView.findViewById(R.id.tv_postMessage);
            tv_play = itemView.findViewById(R.id.tv_play);
            tv_pause = itemView.findViewById(R.id.tv_pause);
            tv_editBtn = itemView.findViewById(R.id.tv_editBtn);
            tv_deleteBtn = itemView.findViewById(R.id.tv_deleteBtn);

            video_postVideo = itemView.findViewById(R.id.video_postVideo);

            linearLayout6 = itemView.findViewById(R.id.linearLayout6);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }

                }
            });
        }
    }
}
