package Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.profile_page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import Models.Posts;
import Models.Users;
import Objects.TextModifier;

public class AdapterPostsItem extends RecyclerView.Adapter<AdapterPostsItem.ItemViewHolder> {

    private List<Posts> arrPosts;
    private List<Users> arrUsers;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public AdapterPostsItem() {
    }

    public AdapterPostsItem(List<Posts> arrPosts, List<Users> arrUsers, Context context) {
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

        Posts posts = arrPosts.get(position);
//        Users users = arrUsers.get(position);



//        String fullName = fname + " " + lname;

        String dateCreated = posts.getDateCreated();
        String timeCreated = posts.getTimeCreated();
        String postMessage = posts.getPostMessage();
        String fileUrl = posts.getFileUrl();
        String fileType = posts.getFileType();
        String postUserId = posts.getUserId();

//        Picasso.get().load(userPhotoUrl).into(holder.iv_userPhoto);
//        holder.tv_userFullName.setText(fullName);
        holder.tv_postDate.setText(dateCreated + " " + timeCreated);
        holder.tv_postMessage.setText(postMessage);

        generateUsersData(postUserId, holder);

        if(fileType.equals("photo"))
        {
            holder.video_postVideo.setVisibility(View.INVISIBLE);
            Picasso.get().load(fileUrl)
                    .into(holder.iv_postPhoto);

        }else if(fileType.equals("video"))
        {
            holder.iv_postPhoto.setVisibility(View.INVISIBLE);

            Uri uri = Uri.parse(fileUrl);
            holder.video_postVideo.setVideoURI(uri);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.video_postVideo);
//            mediaController.setMediaPlayer(holder.video_postVideo);
            holder.video_postVideo.setMediaController(mediaController);

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
    }

    private void generateUsersData(String postUserId, ItemViewHolder holder) {

        DatabaseReference userDatabase = userDatabase = FirebaseDatabase.getInstance().getReference("Users");

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


                    Picasso.get().load(userPhotoUrl).into(holder.iv_userPhoto);
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
        TextView tv_userFullName, tv_postDate, tv_postMessage;
        VideoView video_postVideo;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);
            iv_postPhoto = itemView.findViewById(R.id.iv_postPhoto);

            tv_userFullName = itemView.findViewById(R.id.tv_userFullName);
            tv_postDate = itemView.findViewById(R.id.tv_postDate);
            tv_postMessage = itemView.findViewById(R.id.tv_postMessage);

            video_postVideo = itemView.findViewById(R.id.video_postVideo);

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
