package com.mistareader.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.chrisbanes.photoview.PhotoView;
import com.mistareader.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAvatarFullScreen extends AppCompatActivity {
    public static final String EXTRA_URL = "EXTRA_URL";

    @BindView(R.id.image) PhotoView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.red_700));
        }

        setContentView(R.layout.activity_user_avatar_fullscreen);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(200);
            getWindow().getSharedElementReturnTransition().setDuration(200);
        }

        ActivityCompat.postponeEnterTransition(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        ImageLoader.getInstance().displayImage(url, image, new ImageLoadingListener() {
            @Override public void onLoadingStarted(String imageUri, View view) {

            }

            @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ActivityCompat.startPostponedEnterTransition(UserAvatarFullScreen.this);
            }

            @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                ActivityCompat.startPostponedEnterTransition(UserAvatarFullScreen.this);
            }

            @Override public void onLoadingCancelled(String imageUri, View view) {
                ActivityCompat.startPostponedEnterTransition(UserAvatarFullScreen.this);
            }
        });
    }
}
