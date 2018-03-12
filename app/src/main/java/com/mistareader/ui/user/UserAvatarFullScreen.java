package com.mistareader.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.image.ImageInfo;
import com.mistareader.R;
import com.mistareader.util.views.frescoZoomable.DraweeTransition;
import com.mistareader.util.views.frescoZoomable.ZoomableDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAvatarFullScreen extends Activity {
    public static final String EXTRA_URL = "EXTRA_URL";

    @BindView(R.id.image) ZoomableDraweeView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_avatar_fullscreen);

        ActivityCompat.postponeEnterTransition(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra(EXTRA_URL);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(url)
                .setControllerListener(new BaseControllerListener<ImageInfo>() {
                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        ActivityCompat.startPostponedEnterTransition(UserAvatarFullScreen.this);
                    }
                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        ActivityCompat.startPostponedEnterTransition(UserAvatarFullScreen.this);
                    }
                })
                .build();
        image.setController(controller);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.FIT_CENTER));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.FIT_CENTER, ScalingUtils.ScaleType.CENTER_CROP));
        }
    }

    @Override
    public void onBackPressed() {
        image.onBackPressed();
        super.onBackPressed();
    }
}
