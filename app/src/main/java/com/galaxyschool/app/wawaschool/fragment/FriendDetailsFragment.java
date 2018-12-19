package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.ContactsQrCodeDetailsActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.activity.ChatActivity;
import com.galaxyschool.app.wawaschool.chat.applib.controller.HXSDKHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendDetails;
import com.galaxyschool.app.wawaschool.pojo.ContactsFriendDetailsResult;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;

import java.util.HashMap;
import java.util.Map;

public class FriendDetailsFragment extends BaseFragment
        implements View.OnClickListener {

    public static final String TAG = FriendDetailsFragment.class.getSimpleName();

    public interface Constants {
        public static final int REQUEST_CODE_FRIEND_DETAILS = 6102;

        public static final String EXTRA_FRIEND_ID = "id";
        public static final String EXTRA_FRIEND_ICON = "icon";
        public static final String EXTRA_FRIEND_NAME = "name";
        public static final String EXTRA_FRIEND_NICKNAME = "nickname";
        public static final String EXTRA_FRIEND_DETAILS_CHANGED = "changed";
        public static final String EXTRA_FRIEND_REMARK = "remark";
        public static final String EXTRA_FRIEND_REMARK_CHANGED = "remarkChanged";
        public static final String EXTRA_FRIEND_DELETED = "deleted";
        public static final String EXTRA_FROM_CHAT = "fromChat";
    }

    private String id;
    private String icon;
    private String name;
    private String nickname;
    private boolean isMyself;
    private ContactsFriendDetails details;
    private String remark;
    private boolean remarkChanged;
    private boolean deleted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_friend_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    @Override
    public void finish() {
        super.finish();
       // getResultData().putExtra(Constants.EXTRA_FRIEND_NICKNAME,nickname);
        getActivity().setResult(getResultCode(), getResultData());
        getActivity().finish();
    }

    private void init() {
        this.id = getArguments().getString(Constants.EXTRA_FRIEND_ID);
        this.icon = getArguments().getString(Constants.EXTRA_FRIEND_ICON);
        this.name = getArguments().getString(Constants.EXTRA_FRIEND_NAME);
        this.nickname = getArguments().getString(Constants.EXTRA_FRIEND_NICKNAME);
        if (this.id.equals(getUserInfo().getMemberId())) {
            this.isMyself = true;
        }

        initTitle();
        showViews(false);
        loadFriendDetails();
    }

    private void initTitle() {
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.personal_materials);
        }
    }

    private void initViews() {
        if (this.details == null) {
            return;
        }

        View view = findViewById(R.id.contacts_person_attrs_layout);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_user_icon);
        if (imageView != null) {
            getThumbnailManager().displayUserIcon(
                    AppSettings.getFileUrl(this.details.getHeadPicUrl()), imageView);
        }
        TextView textView = (TextView) findViewById(R.id.contacts_user_name);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.details.getRealName()) ?
                    this.details.getRealName() : this.details.getNickname());
        }
        textView = (TextView) findViewById(R.id.contacts_user_description);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.details.getNoteName()) ?
                    this.details.getNickname() : null);
        }
        View itemView = findViewById(R.id.contacts_remark_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(getText(R.string.remark));
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.details.getNoteName());
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditFriendRemarkDialog();
                }
            });
            if (this.isMyself) {
                itemView.setVisibility(View.GONE);
            }
        }
        itemView = findViewById(R.id.contacts_qrcode_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_key);
            if (textView != null) {
                textView.setText(getText(R.string.personal_qrcode));
            }
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText("");
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterQrCodeDetails();
                }
            });
        }

        Button button = (Button) findViewById(R.id.contacts_send_message);
        if (button != null) {
            button.setText(R.string.send_message);
            button.setOnClickListener(this);
            if (this.isMyself || getArguments().getBoolean(Constants.EXTRA_FROM_CHAT)) {
                button.setVisibility(View.GONE);
            }
        }

        button = (Button) findViewById(R.id.contacts_delete_friend);
        if (button != null) {
            button.setText(R.string.delete);
            button.setOnClickListener(this);
            if (this.isMyself) {
                button.setVisibility(View.GONE);
            }
        }
    }

    private void showViews(boolean show) {
        View rootView = getView();
        if (rootView == null) {
            return;
        }

        int visible = show ? View.VISIBLE : View.INVISIBLE;
        View view = rootView.findViewById(R.id.contacts_person_attrs_layout);
        if (view != null) {
            view.setVisibility(visible);
        }
    }

    private void updateRemark(String remark) {
        if (this.details == null) {
            return;
        }

        this.details.setNoteName(remark);
        TextView textView = (TextView) findViewById(R.id.contacts_user_name);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.details.getRealName()) ?
                    this.details.getRealName() : this.details.getNickname());
        }
        textView = (TextView) findViewById(R.id.contacts_user_description);
        if (textView != null) {
            textView.setText(!TextUtils.isEmpty(this.details.getNoteName()) ?
                    this.details.getNickname() : null);
        }
        View itemView = findViewById(R.id.contacts_remark_attr);
        if (itemView != null) {
            textView = (TextView) itemView.findViewById(R.id.contacts_attribute_value);
            if (textView != null) {
                textView.setText(this.details.getNoteName());
            }
        }
    }

    private void loadFriendDetails() {
        Map<String, Object> params = new HashMap();
        params.put("Id", this.id);
        DefaultListener listener =
                new DefaultListener<ContactsFriendDetailsResult>(
                        ContactsFriendDetailsResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ContactsFriendDetailsResult result = getResult();
                if (result == null || !result.isSuccess()) {
                    return;
                }
                updateViews(result);
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_FRIEND_DETAILS, params, listener);
    }

    private void updateViews(ContactsFriendDetailsResult result) {
        details = result.getModel();
        initViews();
        showViews(true);
    }

    private void showEditFriendRemarkDialog() {
        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(getActivity(),
                R.style.Theme_ContactsDialog, getString(R.string.remark),
                null, this.details.getNoteName(),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String text = ((ContactsInputBoxDialog) dialog).getInputText();
                        if (!TextUtils.isEmpty(text) && !text.equals(details.getNoteName())) {
                            editFriendRemark(text);
                        }
                    }
                });
        dialog.show();
    }

    private void editFriendRemark(String newRemark) {
        this.remark = newRemark;
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", this.id);
        params.put("NoteName", newRemark);
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
//                        TipsHelper.showToast(getActivity(),
//                            getString(R.string.remark_friend_failed));
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.remark_friend_success));
                remarkChanged = true;
                updateRemark(remark);
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_MODIFY_FRIEND_REMARK_URL, params, listener);
    }

    private void showDeleteFriendDialog() {
        String name = this.details.getNoteName();
        if (TextUtils.isEmpty(name)) {
            name = this.details.getNickname();
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(),
                R.style.Theme_ContactsDialog, null,
                getString(R.string.confirm_to_delete_friend, name),
                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
                },
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteFriend();
                    }
                });
        dialog.show();
    }

    private void deleteFriend() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", this.id);
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
//                            getString(R.string.delete_friend_failed));
                    return;
                }
                TipsHelper.showToast(getActivity(),
                        getString(R.string.delete_friend_success));
                deleted = true;
                notifyChanges();
            }
        };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_REMOVE_FRIEND_URL, params, listener);
    }

    private void enterQrCodeDetails() {
        if (this.details == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TITLE,
                getString(R.string.personal_qrcode));
        args.putInt(ContactsQrCodeDetailsActivity.EXTRA_TARGET_TYPE,
                ContactsQrCodeDetailsActivity.TARGET_TYPE_PERSON);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ID,
                this.details.getMemberId());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_ICON,
                this.details.getHeadPicUrl());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_NAME,
                !TextUtils.isEmpty(this.details.getNoteName()) ?
                        this.details.getNoteName() : this.details.getNickname());
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_DESCRIPTION,
                !TextUtils.isEmpty(this.details.getNoteName()) ?
                        this.details.getNickname() : null);
        args.putString(ContactsQrCodeDetailsActivity.EXTRA_TARGET_QR_CODE,
                this.details.getQRCode());
        Intent intent = new Intent(getActivity(), ContactsQrCodeDetailsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void notifyChanges() {
        boolean changed = false;
        if (this.deleted || this.remarkChanged) {
            changed = true;
        }
        Bundle data = new Bundle();
        data.putString(Constants.EXTRA_FRIEND_ID, this.id);
        data.putBoolean(Constants.EXTRA_FRIEND_DETAILS_CHANGED, changed);
        if (this.deleted) {
            data.putBoolean(Constants.EXTRA_FRIEND_DELETED, this.deleted);
        }
        if (this.remarkChanged) {
            data.putBoolean(Constants.EXTRA_FRIEND_REMARK_CHANGED, this.remarkChanged);
            data.putString(Constants.EXTRA_FRIEND_REMARK, this.remark);
        }
        Intent intent = new Intent();
        intent.putExtras(data);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void enterConversation(String userId, String nickname, String avatar) {
        if (!HXSDKHelper.getInstance().isLogined()) {
            TipsHelper.showToast(getActivity(),
                    R.string.chat_service_not_works);
            return;
        }
        String userName = "hx" + userId;
        Bundle args = new Bundle();
        args.putInt(ChatActivity.EXTRA_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE);
        args.putString(ChatActivity.EXTRA_USER_ID, userName);
        args.putString(ChatActivity.EXTRA_USER_AVATAR, avatar);
        args.putString(ChatActivity.EXTRA_USER_NICKNAME, nickname);
        args.putInt(ChatActivity.EXTRA_FROM_WHERE, ChatActivity.FROM_FRIEND);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        notifyChanges();
        return true;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            notifyChanges();
        } else if (v.getId() == R.id.contacts_delete_friend) {
            showDeleteFriendDialog();
        } else if (v.getId() == R.id.contacts_send_message) {
            String userName = this.details.getNoteName();
            if (TextUtils.isEmpty(userName)) {
                userName = this.details.getNickname();
            }
            enterConversation(this.details.getMemberId(),
                    userName, AppSettings.getFileUrl(this.icon));
        }
    }

}
