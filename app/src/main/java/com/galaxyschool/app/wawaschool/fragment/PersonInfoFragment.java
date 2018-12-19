package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.HashMap;
import java.util.Map;

public class PersonInfoFragment extends BaseFragment
        implements View.OnClickListener {

    public static final String TAG = PersonInfoFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_PERSON_ID = "id";
        public static final String EXTRA_PERSON_ICON = "icon";
        public static final String EXTRA_PERSON_NAME = "name";
        public static final String EXTRA_PERSON_NICKNAME = "nickname";
        public static final String EXTRA_IS_FRIEND = "isFriend";
    }

    private String id;
    private String icon;
    private String name;
    private String nickname;
    private boolean isFriend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_person_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void finish() {
        super.finish();
        getActivity().finish();
    }

    private void init() {
        this.id = getArguments().getString(Constants.EXTRA_PERSON_ID);
        this.icon = getArguments().getString(Constants.EXTRA_PERSON_ICON);
        this.name = getArguments().getString(Constants.EXTRA_PERSON_NAME);
        this.nickname = getArguments().getString(Constants.EXTRA_PERSON_NICKNAME);
        this.isFriend = getArguments().getBoolean(Constants.EXTRA_IS_FRIEND);

        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(R.string.personal_materials);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        imageView = (ImageView) findViewById(R.id.contacts_user_icon);
        if (imageView != null) {
            getThumbnailManager().displayUserIcon(
                    AppSettings.getFileUrl(this.icon), imageView);
        }
        textView = (TextView) findViewById(R.id.contacts_user_name);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.nickname) ?
                    this.nickname : "");
        }
        textView = (TextView) findViewById(R.id.contacts_user_description);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.name) ?
                    this.name : null);
            textView.setVisibility(View.VISIBLE);
        }

        Button button = (Button) findViewById(R.id.contacts_add_friend);
        if (button != null) {
            button.setText(R.string.add_as_friend);
            button.setOnClickListener(this);
            button.setVisibility(this.isFriend ? View.GONE : View.VISIBLE);
        }
    }

    private void addFriend() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("NewFriendId", this.id);
        DefaultListener listener =
                new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ModelResult result = getResult();
                if (result == null || !result.isSuccess()) {
//                    TipsHelper.showToast(getActivity(),
//                            getString(R.string.friend_request_send_failed));
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.friend_request_send_success));
                finish();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_ADD_FRIEND_URL, params, listener);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finish();
        } else if (v.getId() == R.id.contacts_add_friend) {
            addFriend();
        }
    }

}
