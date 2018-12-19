package com.galaxyschool.app.wawaschool.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.Netroid;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassQrCodeInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassQrCodeInfoResult;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsQrCodeDetailsFragment extends BaseFragment
    implements View.OnClickListener {

    public static final String TAG = ContactsQrCodeDetailsFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_TITLE = "title";
        public static final String EXTRA_TARGET_TYPE = "type";
        public static final String EXTRA_TARGET_ID = "id";
        public static final String EXTRA_TARGET_ICON = "icon";
        public static final String EXTRA_TARGET_NAME = "name";
        public static final String EXTRA_TARGET_DESCRIPTION = "description";
        public static final String EXTRA_TARGET_QR_CODE = "qrCode";

        public static final int TARGET_TYPE_PERSON = 0;
        public static final int TARGET_TYPE_CLASS = 1;
        public static final int TARGET_TYPE_SCHOOL = 2;
    }

    private int type;
    private String id;
    private Object data;
    private ImageView qrCodeView;
    private String qrCodeImageUrl;
    private String qrCodeImagePath;
    private String name;
    private String description;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_qrcode_details, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
    }

    private void init() {
        this.type = getArguments().getInt(Constants.EXTRA_TARGET_TYPE);
        this.id = getArguments().getString(Constants.EXTRA_TARGET_ID);

        initTitle();
        showViews(false);
        if (this.type == Constants.TARGET_TYPE_CLASS) {
            loadQrCodeDetails();
        } else if (this.type == Constants.TARGET_TYPE_PERSON
                || this.type == Constants.TARGET_TYPE_SCHOOL) {
            initViews();
            showViews(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        getActivity().finish();
    }

    private void initTitle() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setText(getArguments().getString(Constants.EXTRA_TITLE));
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            imageView.setImageResource(R.drawable.selector_icon_navi_more);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        String icon = null;
        String qrCode = null;

        if (this.type == Constants.TARGET_TYPE_PERSON
                || this.type == Constants.TARGET_TYPE_SCHOOL) {
            icon = getArguments().getString(Constants.EXTRA_TARGET_ICON);
            name = getArguments().getString(Constants.EXTRA_TARGET_NAME);
            description = getArguments().getString(Constants.EXTRA_TARGET_DESCRIPTION);
            qrCode = getArguments().getString(Constants.EXTRA_TARGET_QR_CODE);
        } else if (this.type == Constants.TARGET_TYPE_CLASS) {
            name = getArguments().getString(Constants.EXTRA_TARGET_NAME);
            description = getArguments().getString(Constants.EXTRA_TARGET_DESCRIPTION);
            if (this.data != null) {
                ContactsClassQrCodeInfo obj = (ContactsClassQrCodeInfo) this.data;
                icon = obj.getHeadPicUrl();
                name = obj.getClassMailName();
                qrCode = obj.getQRCode();
            }
        }
        qrCodeImageUrl = qrCode;

        View view = findViewById(R.id.contacts_user_basic_info_layout);
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_user_icon);
        if (imageView != null) {
            if (this.type == Constants.TARGET_TYPE_PERSON) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(icon),
                        imageView, R.drawable.default_user_icon);
            } else if (this.type == Constants.TARGET_TYPE_SCHOOL) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(icon),
                        imageView, R.drawable.default_school_icon);
            } else if (this.type == Constants.TARGET_TYPE_CLASS) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(icon),
                        imageView, R.drawable.default_class_icon);
            }
        }
        TextView textView = (TextView) findViewById(R.id.contacts_user_name);
        if (textView != null) {
            textView.setText(name);
        }
        textView = (TextView) findViewById(R.id.contacts_user_description);
        if (textView != null) {
            textView.setText(description);
        }
        imageView = (ImageView) findViewById(R.id.contacts_qrcode_image);
        if (imageView != null) {
//            getThumbnailManager().displayThumbnail(AppSettings.getFileUrl(qrcode), imageView);
            //设置二维码的大小为屏幕短边的1/3
            int screenWidth = ScreenUtils.getScreenWidth(getActivity());
            int screenHeight = ScreenUtils.getScreenHeight(getActivity());
            int minSize = screenWidth < screenHeight ? screenWidth : screenHeight;
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)imageView
                    .getLayoutParams();
            layoutParams.width = minSize / 3;
            layoutParams.height = minSize / 3;
            imageView.setLayoutParams(layoutParams);
            imageView.setVisibility(View.INVISIBLE);
            qrCodeImageUrl = AppSettings.getFileUrl(qrCode);
            qrCodeView = imageView;
            loadQrCodeImage();
        }
    }

    private void showViews(boolean show) {
        int visible = show ? View.VISIBLE : View.INVISIBLE;
        View view = findViewById(R.id.contacts_user_basic_info_layout);
        if (view != null) {
            view.setVisibility(visible);
        }

        ImageView imageView = (ImageView) findViewById(R.id.contacts_qrcode_image);
        if (imageView != null) {
            imageView.setVisibility(visible);
        }
    }

    public void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList();
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.save_qrcode));
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.subscription_recommend));
        AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (position == 0) {
                        saveQrCodeImage();
                    } else if (position == 1) {
                        share();
                    }
                }
            };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void loadQrCodeDetails() {
        if (this.type != Constants.TARGET_TYPE_CLASS) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("Id", this.id);
        DefaultListener listener =
            new DefaultListener<ContactsClassQrCodeInfoResult>(
                ContactsClassQrCodeInfoResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    ContactsClassQrCodeInfoResult result = getResult();
                    if (result == null || !result.isSuccess()) {
                        return;
                    }
                    updateViews(result);
                }
            };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_QRCODE_URL, params, listener);
    }

    private void updateViews(ContactsClassQrCodeInfoResult result) {
        this.data = result.getModel();
        initViews();
        showViews(true);
    }

    private void loadQrCodeImage() {
        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        qrCodeImagePath = ImageLoader.getCacheImagePath(qrCodeImageUrl);
        File file = new File(qrCodeImagePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(qrCodeImagePath);
            if (bitmap != null) {
                qrCodeView.setImageBitmap(bitmap);
                return;
            }
            file.delete();
        }

        Netroid.downloadFile(getActivity(), qrCodeImageUrl, qrCodeImagePath,
            new Listener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if(getActivity() == null) {
                        return;
                    }
                    qrCodeView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                }

                @Override
                public void onError(NetroidError error) {
                    if(getActivity() == null) {
                        return;
                    }
                    super.onError(error);
                    TipsHelper.showToast(getActivity(),
                            R.string.picture_download_failed);
                }
            });
    }

    private void saveQrCodeImage() {
        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        String filePath = ImageLoader.saveImage(getActivity(), qrCodeImageUrl);
        if (filePath != null) {
            TipsHelper.showToast(getActivity(),
                    getString(R.string.image_saved_to, filePath));
        } else {
            TipsHelper.showToast(getActivity(), getString(R.string.save_failed));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finish();
        } else if (v.getId() == R.id.contacts_header_right_ico) {
            showMoreMenu(findViewById(R.id.contacts_header_layout));
        }
    }

    private void share() {
        String shareAddress;
        int type = -1;
        if (this.type == Constants.TARGET_TYPE_PERSON) {
            type = 1;
        } else if (this.type == Constants.TARGET_TYPE_CLASS) {
            type = 0;
        } else if (this.type == Constants.TARGET_TYPE_SCHOOL) {
            type = 2;
        }
        if (type < 0) {
            return;
        }

        shareAddress = String.format(ServerUrl.SUBSCRIBE_SHARE_QRCODE_URL, this.id)
                + "&type=" + type;
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(name);
        if (!TextUtils.isEmpty(description)) {
            shareInfo.setContent(description);
        } else {
            shareInfo.setContent(" ");
        }
        shareInfo.setTargetUrl(shareAddress);
//        UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
//        if (!TextUtils.isEmpty(qrCodeImageUrl)) {
//            umImage = new UMImage(getActivity(), qrCodeImageUrl);
//        }
        UMImage umImage = new UMImage(getActivity(), R.drawable.qrcode_share_default);
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(name);
        resource.setDescription(description);
        resource.setShareUrl(shareAddress);
        resource.setThumbnailUrl(qrCodeImageUrl);
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

}
