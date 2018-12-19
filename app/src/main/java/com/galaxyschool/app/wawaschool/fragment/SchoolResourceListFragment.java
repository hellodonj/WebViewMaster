package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.NewResourceDeleteHelper;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;

public class SchoolResourceListFragment extends SchoolResourceListBaseFragment {

    public static final String TAG = SchoolResourceListFragment.class.getSimpleName();
    public  GridView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_school_resource_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
         listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    loadResourceList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_delete);
                    NewResourceDeleteHelper helper = new NewResourceDeleteHelper(getActivity(), getCurrAdapterViewHelper(), NewResourceDeleteHelper.SCHOOL_MESSAGE_COURSE, data, imageView);
                    helper.initImageViewEvent(getMemeberId(), getString(R.string.delete_school_course));
                    return view;
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

}
