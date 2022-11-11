package Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.revic_capstone.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;


public class PostPhotosAndVidFragment extends Fragment {

    private ExtendedFloatingActionButton btn_postFloating;
    private FloatingActionButton btn_addPhotos, btn_addVideos;
    private Button btn_postBtn;
    private EditText et_postDescription;
    private ImageView iv_post;
    private TextView tv_addVidText, tv_addPhotoText;

    private Uri imageUri, videoUri;

    public static final int PIC_ID = 3;
    public static final int VID_ID = 2;

    private Boolean isAllFabsVisible;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event, container, false);

        setRef(view);
        buttonActivity();
        clickListeners();

        return view;
    }



    private void clickListeners() {

        btn_postFloating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAllFabsVisible)
                {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    btn_addPhotos.show();
                    btn_addVideos.show();
                    tv_addVidText.setVisibility(View.VISIBLE);
                    tv_addPhotoText.setVisibility(View.VISIBLE);


                    // Now extend the parent FAB, as
                    // user clicks on the shrinked
                    // parent FAB
                    btn_postFloating.extend();

                    // make the boolean variable true as
                    // we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = true;
                }
                else
                {

                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs GONE.
                    btn_addPhotos.hide();
                    btn_addVideos.hide();
                    tv_addVidText.setVisibility(View.GONE);
                    tv_addPhotoText.setVisibility(View.GONE);

                    // Set the FAB to shrink after user
                    // closes all the sub FABs
                    btn_postFloating.shrink();

                    // make the boolean variable false
                    // as we have set the sub FABs
                    // visibility to GONE
                    isAllFabsVisible = false;
                }
            }
        });

        btn_addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PIC_ID);
            }
        });

        btn_addVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), VID_ID);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PIC_ID && resultCode == RESULT_OK){
            imageUri = data.getData();

            Picasso.get().load(imageUri)
                    .fit()
                    .centerCrop()
                    .into(iv_post);

            btn_postBtn.setVisibility(View.VISIBLE);
            btn_postBtn.setText("POST PICTURE?");
            buttonActivity();

        }else  if (requestCode==VID_ID && resultCode == RESULT_OK){

            videoUri = data.getData();

//            Picasso.get().load(videoUri)
//                    .fit()
//                    .centerCrop()
//                    .into(iv_post);

            Glide.with(getContext())
                    .load(videoUri)
                    .fitCenter()
                    .centerCrop()
                    .into(iv_post);

            btn_postBtn.setVisibility(View.VISIBLE);
            btn_postBtn.setText("POST VIDEO?");
            buttonActivity();
        }

    }

    private void buttonActivity() {
        btn_addPhotos.hide();
        btn_addVideos.hide();
        tv_addVidText.setVisibility(View.GONE);
        tv_addPhotoText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        btn_postFloating.shrink();
    }

    private void setRef(View view) {

        btn_postFloating = view.findViewById(R.id.btn_postFloating);
        btn_postBtn = view.findViewById(R.id.btn_postBtn);
        btn_addPhotos = view.findViewById(R.id.btn_addPhotos);
        btn_addVideos = view.findViewById(R.id.btn_addVideos);

        et_postDescription = view.findViewById(R.id.et_postDescription);

        iv_post = view.findViewById(R.id.iv_post);

        tv_addVidText = view.findViewById(R.id.tv_addVidText);
        tv_addPhotoText = view.findViewById(R.id.tv_addPhotoText);

    }
}