package com.mistareader.ui.user;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.mistareader.R;
import com.mistareader.api.API;
import com.mistareader.api.ApiResult;
import com.mistareader.model.User;
import com.mistareader.ui.BaseActivity;
import com.mistareader.util.DateUtils;
import com.mistareader.util.S;
import com.mistareader.util.SystemUtils;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.TitleValueItem;
import com.mistareader.util.textProcessors.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


public class UserActivity extends BaseActivity {
    public static final String EXTRA_USER_NAME = "EXTRA_USER_NAME";

    @BindView(R.id.userName) TextView userName;
    @BindView(R.id.avatar) SimpleDraweeView avatar;
    @BindView(R.id.realName) TextView realName;
    @BindView(R.id.messages) TextView messages;
    @BindView(R.id.topics) TextView topics;
    @BindView(R.id.properties) LinearLayout properties;

    private API mApi;
    private String mName;
    private User mUser;
    private ArrayList<TitleValueItem> propertiesList;
    private boolean imageLoaded;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        avatar.setLegacyVisibilityHandlingEnabled(true);

        loadParams();
        initRecyclerView();

        userName.setText(mName);

        mApi = new API();
        mApi.getUser(mName, new ApiResult() {
            @Override
            public void onResult(Object result) {
                mUser = (User) result;
                if (S.isEmpty(mUser.real_name)) {
                    realName.setVisibility(View.GONE);
                } else {
                    realName.setText(mUser.real_name);
                }

                if (S.isEmpty(mUser.photo)) {
                    avatar.setVisibility(View.GONE);
                } else {
                    avatar.setController(Fresco.newDraweeControllerBuilder()
                            .setUri(mUser.photo)
                            .setControllerListener(new AvatarOnCompleteListener())
                            .build());
                }

                Drawable drawable;
                if (mUser.female) {
                    drawable = ThemesManager.tint(UserActivity.this, R.drawable.ic_female, android.R.attr.textColorPrimary);
                } else {
                    drawable = ThemesManager.tint(UserActivity.this, R.drawable.ic_male, android.R.attr.textColorPrimary);
                }

                userName.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(' ');
                DecimalFormat formatter = new DecimalFormat();
                formatter.setDecimalFormatSymbols(symbols);

                messages.setText(formatter.format(mUser.messages));
                topics.setText(formatter.format(mUser.topics));

                propertiesList = new ArrayList<>();

                if (mUser.registered_unixtime != null) {
                    String ago = DateUtils.formatDateRange(mUser.registered_unixtime);

                    String date = String.format("%s (%s)", DateUtils.SDF_D_MM_Y.format(mUser.registered_unixtime), ago);
                    if (mUser.female) {
                        propertiesList.add(new TitleValueItem("Зарегистрирована", date));
                    } else {
                        propertiesList.add(new TitleValueItem("Зарегистрирован", date));
                    }
                }

                addValue("Последняя активность", DateUtils.SDF_D_MM_Y_H_M.format(mUser.last_acted));
                addValue("URL", mUser.url);
                addValue("Skype", mUser.skype);
                addValue("Город, страна", StringUtils.concat(mUser.town, mUser.country));
                addValue("Год рождения", DateUtils.formatBirth(mUser.birthyear));

                addValue("Род занятий", mUser.profession);
                addValue("Достижения (опыт)", mUser.expirience);
                addValue("Интересы", mUser.interest);

                addValue("Роль", getRole(mUser.is_moderator, mUser.light_moderator));

                for (TitleValueItem item : propertiesList) {
                    View v = mLayoutInflater.inflate(R.layout.user_property_item, null);
                    TextView title = v.findViewById(R.id.title);
                    TextView value = v.findViewById(R.id.value);
                    if (item.title.equals("Skype")) {
                        value.setOnClickListener(v1 -> SystemUtils.callSkype(UserActivity.this, ((TextView) v1).getText().toString()));
                    }

                    title.setText(item.title);
                    value.setText(item.valueStr);
                    properties.addView(v);
                }
                Log.d("DBG", "onResult: ");
            }
        });
    }

    public String getRole(boolean isModerator, boolean lightModerator) {
        if (isModerator) {
            return "Модератор";
        } else if (lightModerator) {
            return "Лайт-модератор";
        }

        return null;
    }

    public class AvatarOnCompleteListener extends BaseControllerListener<ImageInfo> {
        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            if (imageLoaded) {
                return;
            }
            imageLoaded = true;
            int minWidth = SystemUtils.dpToPixel(UserActivity.this, 50);
            int imageWidth = imageInfo.getWidth();
            if (imageWidth < minWidth) {
                float scale = (float) minWidth / imageWidth;
                LayoutParams lp = avatar.getLayoutParams();
                lp.width = minWidth;
                lp.height = (int) (scale * imageInfo.getHeight());
                avatar.setLayoutParams(lp);
            } else if (imageWidth > imageInfo.getHeight()) {
                float scale = (float) imageWidth / imageInfo.getHeight();
                LayoutParams lp = avatar.getLayoutParams();
                lp.width = (int) (lp.height * scale);
                avatar.setLayoutParams(lp);
            } else if (imageWidth == imageInfo.getHeight()) {
                int maxHeight = SystemUtils.getDimen(UserActivity.this, R.dimen.avatar_height);
                if (imageInfo.getHeight() < maxHeight) {
                    LayoutParams lp = avatar.getLayoutParams();
                    lp.width = lp.height;
                    avatar.setLayoutParams(lp);
                }
            }
        }
    }

    private void addValue(String title, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Boolean && (Boolean) value) {
            propertiesList.add(new TitleValueItem(title, "true"));
        } else if (value instanceof String && !S.isEmpty(String.valueOf(value))) {
            propertiesList.add(new TitleValueItem(title, String.valueOf(value)));
        } else if (value instanceof Integer && (Integer) value != 0) {
            propertiesList.add(new TitleValueItem(title, Integer.toString((Integer) value)));
        }
    }

    private void initRecyclerView() {
        mLayoutInflater = getLayoutInflater();
        //        mList = new Recycler<>(properties, R.layout.user_property_item, new UserPropertiesAdapter(this), true);
    }

    @OnClick(R.id.avatar)
    void onAvatarClick() {
        if (TextUtils.isEmpty(mUser.photo) || !imageLoaded) {
            return;
        }

        Intent intent = new Intent(UserActivity.this, UserAvatarFullScreen.class);
        intent.putExtra(UserAvatarFullScreen.EXTRA_URL, mUser.photo);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, avatar, getString(R.string.transition_avatar));
        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @OnClick(R.id.close)
    void onCloseClick() {
        finish();
    }

    private void loadParams() {
        Intent intent = getIntent();
        mName = intent.getStringExtra(EXTRA_USER_NAME);
    }

}
