package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;
import com.galaxyschool.app.wawaschool.pojo.PerformMember;
import com.galaxyschool.app.wawaschool.views.RoundedImageView;
import java.util.ArrayList;
import java.util.List;

public class ActActorListFragment extends ContactsListFragment {

    public static final String TAG = ActActorListFragment.class.getSimpleName();
    private View rootView;
    private GridView gridView;

    private PerformClassList performClassList;
    private List<PerformMember> performMembers=new ArrayList<>();

    public ActActorListFragment(PerformClassList performClassList) {
        this.performClassList = performClassList;
        performMembers=performClassList.getPerformMemberList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_actor_list, null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initGridView();
    }

    private void initGridView() {
        gridView = (GridView) findViewById(R.id.actor_list_gridview);
        if (gridView != null) {
            gridView.setNumColumns(4);
            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.item_actor_gridview) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        PerformMember data = (PerformMember) getData().get(position);
                        if (data == null) {
                            return view;
                        }
                        ViewHolder holder = (ViewHolder) view.getTag();
                        if (holder == null) {
                            holder = new ViewHolder();
                        }
                        holder.data = data;

                        RoundedImageView thumbnail = (RoundedImageView) view.findViewById(R.id.icon_head);
                        if (thumbnail != null) {
                            MyApplication.getThumbnailManager((Activity) context)
                                    .displayUserIconWithDefault(AppSettings.getFileUrl(data.getHeadPicUrl()),
                                            thumbnail, R.drawable.default_user_icon);
                        }
                        TextView studentName = (TextView) view.findViewById(R.id.title);
                        if (studentName != null) {
                            String realName = data.getRealName();
                            if (!TextUtils.isEmpty(realName)) {
                                studentName.setText(realName);
                            } else {
                                studentName.setText(data.getNickName());
                            }
                        }
                        view.setTag(holder);
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) return;
                }
            };
            setCurrAdapterViewHelper(gridView, listViewHelper);
            if (performMembers!=null&&performMembers.size()>0){
                getCurrAdapterViewHelper().setData(performMembers);
            }
        }
    }
}
