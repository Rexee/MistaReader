package com.mistareader.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mistareader.R;
import com.mistareader.model.User;
import com.mistareader.ui.BaseNetworkActivity;
import com.mistareader.util.DateUtils;
import com.mistareader.util.Empty;
import com.mistareader.util.SystemUtils;
import com.mistareader.util.ThemesManager;
import com.mistareader.util.TitleValueItem;
import com.mistareader.util.textProcessors.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


public class UserActivity extends BaseNetworkActivity {
    public static final String EXTRA_USER_NAME = "EXTRA_USER_NAME";

    @BindView(R.id.userName)   TextView     userName;
    @BindView(R.id.avatar)     ImageView    avatar;
    @BindView(R.id.realName)   TextView     realName;
    @BindView(R.id.messages)   TextView     messages;
    @BindView(R.id.topics)     TextView     topics;
    @BindView(R.id.properties) LinearLayout properties;

    private String                    mUserId;
    private String                    mUserName;
    private User                      mUser;
    private ArrayList<TitleValueItem> propertiesList;
    private boolean                   imageLoaded;
    private LayoutInflater            mLayoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);

        loadParams();
        initRecyclerView();

        userName.setText(mUserName);

        netProvider.getUser(mUserName, mUserId, result -> {
            mUser = result;

            if (mUser == null) {
                Toast.makeText(UserActivity.this, "Ошибка получения данных пользователя из API", Toast.LENGTH_SHORT).show();
                return;
            }
            userName.setText(mUser.name);

            if (Empty.is(mUser.real_name)) {
                realName.setVisibility(View.GONE);
            } else {
                realName.setText(mUser.real_name);
            }

            if (Empty.is(mUser.photo)) {
                avatar.setVisibility(View.GONE);
            } else {
                ImageLoader.getInstance().loadImage(mUser.photo, new ImageLoadingListener() {
                    @Override public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (imageLoaded) {
                            return;
                        }
                        imageLoaded = true;
                        int minWidth = SystemUtils.getDimen(UserActivity.this, R.dimen.avatar_min_width);
                        int imageWidth = loadedImage.getWidth();
                        if (imageWidth < minWidth) {
                            float scale = (float) minWidth / imageWidth;
                            LayoutParams lp = avatar.getLayoutParams();
                            lp.width = minWidth;
                            lp.height = (int) (scale * loadedImage.getHeight());
                            avatar.setLayoutParams(lp);
                        }

                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), loadedImage);
                        dr.setCornerRadius(Math.max(loadedImage.getWidth(), loadedImage.getHeight()) / 24.0f);
                        avatar.setImageDrawable(dr);
                    }

                    @Override public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }

            if (mUser.female == null) {
                setUserGenderIcon(null);
            } else if (mUser.female) {
                setUserGenderIcon(ThemesManager.tintAttr(UserActivity.this, R.drawable.ic_female, android.R.attr.textColorPrimary));
            } else {
                setUserGenderIcon(ThemesManager.tintAttr(UserActivity.this, R.drawable.ic_male, android.R.attr.textColorPrimary));
            }

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');
            DecimalFormat formatter = new DecimalFormat();
            formatter.setDecimalFormatSymbols(symbols);

            messages.setText(formatter.format(mUser.messages));
            topics.setText(formatter.format(mUser.topics));

            propertiesList = new ArrayList<>();

            if (mUser.registered_unixtime != 0) {
                String ago = DateUtils.formatDateRange(mUser.registered_unixtime);

                String date = String.format("%s (%s)", DateUtils.SDF_D_MM_Y.format(mUser.registered_unixtime), ago);
                if (mUser.female != null && mUser.female) {
                    propertiesList.add(new TitleValueItem("Зарегистрирована", date));
                } else {
                    propertiesList.add(new TitleValueItem("Зарегистрирован", date));
                }
            }

            addValue("Последняя активность", mUser.last_acted == 0 ? null : DateUtils.SDF_D_MM_Y_H_M.format(mUser.last_acted));
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
        });
    }

    private void setUserGenderIcon(Drawable right) {
        userName.setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
    }

    public String getRole(boolean isModerator, boolean lightModerator) {
        if (isModerator) {
            return "Модератор";
        } else if (lightModerator) {
            return "Лайт-модератор";
        }

        return null;
    }

    private void addValue(String title, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof Boolean && (Boolean) value) {
            propertiesList.add(new TitleValueItem(title, "true"));
        } else if (value instanceof String && !Empty.is(String.valueOf(value))) {
            propertiesList.add(new TitleValueItem(title, String.valueOf(value)));
        } else if (value instanceof Integer && (Integer) value != 0) {
            propertiesList.add(new TitleValueItem(title, Integer.toString((Integer) value)));
        }
    }

    private void initRecyclerView() {
        mLayoutInflater = getLayoutInflater();
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
        if (intent.hasExtra(EXTRA_USER_NAME)) {
            mUserName = intent.getStringExtra(EXTRA_USER_NAME);
            mUserId = null;
        } else if (intent.getData() != null) {
            Uri data = intent.getData();
            String query = data.getQuery();
            if (query != null && query.startsWith("id=")) {
                mUserName = null;
                mUserId = query.substring(3);
            }
        }
    }
}
