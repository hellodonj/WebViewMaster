package com.galaxyschool.app.wawaschool.fragment.resource;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.CatalogResourceListResult;
import com.galaxyschool.app.wawaschool.pojo.MaterialInfo;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.ResourceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: wangchao
 * Time: 2015/11/07 09:53
 */
public class ResourcePicFragment extends ContactsListFragment {
    public static final String TAG = ResourcePicFragment.class.getSimpleName();


    private PullToRefreshView pullToRefreshView;
    private TextView checkBox;
    private List<ResourceInfo> resourceInfos = new ArrayList<ResourceInfo>();
    private ArrayList<ResourceInfo> selectResourceInfos = new ArrayList<ResourceInfo>();
    private boolean isChecked;
    private String sectionId;
    private String materialId;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resource_pic, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadResourceList(sectionId, materialId);
        isChecked = false;
    }

    private void initViews() {
        if(getArguments() != null) {
//            this.materialId = getArguments().getString(BookListActivity.EXTRA_MATERIAL_ID);
            this.sectionId = getArguments().getString(CatalogLessonListActivity.EXTRA_BOOK_CATALOG_ID);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        if (toolbarTopView != null) {
//            toolbarTopView.getTitleView().setText(getArguments().getString(CatalogLessonListActivity.EXTRA_TITLE));
            toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
            toolbarTopView.getCommitView().setText(R.string.confirm);
            toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getCommitView().setOnClickListener(this);
        }

        checkBox = (TextView) findViewById(R.id.checkbox);
        checkBox.setOnClickListener(this);

        pullToRefreshView = (PullToRefreshView) findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView gridView = (GridView) findViewById(R.id.gridview);
        int padding = getResources().getDimensionPixelOffset(R.dimen.resource_gridview_padding);
        final int gridItemSize = (ScreenUtils.getScreenWidth(getActivity()) - padding * 3) / 2;
        if (gridView != null) {
//            gridView.setBackgroundColor(Color.WHITE);
            gridView.setNumColumns(2);
            gridView.setPadding(padding, padding, padding, padding);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), gridView, R.layout.resource_pic_grid_item) {
                @Override
                public void loadData() {
                    loadResourceList(sectionId, materialId);
                }

                @Override
                public View getView(int position, final View convertView, ViewGroup parent) {
                    final View view = super.getView(position, convertView, parent);

                    view.setLayoutParams(new AbsListView.LayoutParams(gridItemSize, gridItemSize));

                    ResourceInfo data = (ResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;

                    ImageView image = (ImageView) view.findViewById(R.id.image);
                    ImageView flag = (ImageView) view.findViewById(R.id.flag);

                    if (!TextUtils.isEmpty(data.getImgPath())) {
                        getThumbnailManager().displayThumbnail(data.getImgPath(), image);
                    } else {
                        image.setImageResource(R.drawable.ic_launcher);
                    }
                    flag.setImageResource(data.isSelected() ? R.drawable.select : R.drawable.unselect);

                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    if (resourceInfos != null && resourceInfos.size() > 0) {
                        if (position < resourceInfos.size()) {
                            ResourceInfo resourceInfo = resourceInfos.get(position);
                            if (resourceInfo != null) {
                                resourceInfo.setIsSelected(!resourceInfo.isSelected());
                            }
                        }
                        getCurrAdapterViewHelper().setData(resourceInfos);
                        loadSelectPicInfos();
                    }
                }
            };
            setCurrAdapterViewHelper(gridView, helper);
        }
    }

    private void selectAll(boolean isChecked) {
        if (resourceInfos != null && resourceInfos.size() > 0) {
            for (ResourceInfo info : resourceInfos) {
                if (info != null) {
                    info.setIsSelected(isChecked);
                }
            }
        }
        getCurrAdapterViewHelper().setData(resourceInfos);

        loadSelectPicInfos();
    }


    private void loadSelectPicInfos() {
        if (resourceInfos != null && resourceInfos.size() > 0) {
            selectResourceInfos.clear();
            for (ResourceInfo info : resourceInfos) {
                if (info != null && info.isSelected()) {
                    selectResourceInfos.add(info);
                }
            }
        }

        if (selectResourceInfos.size() > 0) {
            if (resourceInfos.size() == selectResourceInfos.size()) {
                updateCheckBoxState(true);
            } else {
                updateCheckBoxState(false);
            }
        } else {
            updateCheckBoxState(false);
        }
    }

    private void updateCheckBoxState(boolean isChecked) {
        this.isChecked = isChecked;
        Drawable drawable = getResources().getDrawable(R.drawable.resource_uncheck);
        Drawable checkedDrawable = getResources().getDrawable(R.drawable.resource_checked);
        if (isChecked) {
            checkBox.setCompoundDrawablesWithIntrinsicBounds(checkedDrawable, null, null, null);
        } else {
            checkBox.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    private List<String> getSelectedPaths() {

        if (selectResourceInfos == null || selectResourceInfos.size() == 0) {
            return null;
        }

        List<String> paths = new ArrayList<String>();
        for (ResourceInfo info : selectResourceInfos) {
            if (info != null && !TextUtils.isEmpty(info.getImgPath())) {
                paths.add(info.getImgPath());
            }
        }

        return paths;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_top_back_btn) {
            FragmentManager fm = getFragmentManager();
            if(fm != null) {
                fm.popBackStack();
            }
        } else if (v.getId() == R.id.toolbar_top_commit_btn) {
            if (selectResourceInfos== null || selectResourceInfos.size() == 0) {
                TipMsgHelper.ShowLMsg(getActivity(), R.string.no_resource_select);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("resourseInfoList", selectResourceInfos);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            if(getActivity() != null) {
                getActivity().setResult(getActivity().RESULT_OK, intent);
                getActivity().finish();
            }
        } else if (v.getId() == R.id.checkbox) {
            selectAll(!isChecked);
        }
    }


    protected void loadResourceList(String sectionId, final String materialId) {
        Map<String, Object> mParams = new HashMap<String, Object>();
        mParams.put("SectionId", sectionId);
        mParams.put("MaterialType", materialId);
        mParams.put("Pager", getPageHelper().getFetchingPagerArgs());
        postRequest(
            ServerUrl.LOAD_RESOURCE_LIST_URL, mParams,
            new DefaultListener<CatalogResourceListResult>(CatalogResourceListResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (getActivity() == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    CatalogResourceListResult result = getResult();
                    if(result != null && result.getModel().getMaterialList() != null) {
                        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
                            if (getPageHelper().isFetchingFirstPage()) {
                                getCurrAdapterViewHelper().clearData();
                            }
                            List<MaterialInfo> materialInfos = result.getModel().getMaterialList();
                            if (materialInfos != null && materialInfos.size() > 0) {
                                getPageHelper().updateByPagerArgs(result.getModel().getPager());
                                getPageHelper().setCurrPageIndex(getPageHelper().getFetchingPageIndex());
                            }
                            if (materialInfos != null && materialInfos.size() > 0) {
                                if (resourceInfos == null) {
                                    resourceInfos = new ArrayList<ResourceInfo>();
                                }
                                resourceInfos.clear();
                                for (MaterialInfo materialInfo : materialInfos) {
                                    if (materialInfo != null) {
                                        ResourceInfo resourceInfo = new ResourceInfo();
                                        resourceInfo.setImgPath(AppSettings.getFileUrl(materialInfo.getThumbUrl()));
                                        resourceInfo.setType(5);
                                        resourceInfo.setIsSelected(false);
                                        resourceInfos.add(resourceInfo);
                                    }
                                }
                                getCurrAdapterViewHelper().setData(resourceInfos);
                            }
                        }
                    }
                }
            });
        showLoadingDialog();
    }
}
