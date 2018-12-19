package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.CatalogLessonListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.Calalog;

import java.io.Serializable;
import java.util.List;

public class CatalogSelectFragment extends ContactsExpandListFragment implements View.OnClickListener {
    public interface Constants {
        public static final int REQUEST_CODE_SELECT_CATALOG = 10022;
    }

    ;
    private ExpandableListView catalogExpandListView;
    public static final String TAG = CatalogSelectFragment.class.getSimpleName();
    private List<Calalog> calalogs;
    private String bookName;
    private TextView headTitleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catalog_select_fragment, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        headTitleView = ((TextView) findViewById(R.id.contacts_header_title));
        initCataLogExpandListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCatalogData();
    }

    private void loadCatalogData() {
        calalogs = (List<Calalog>) getArguments().getSerializable(CatalogLessonListActivity.BOOK_CATALOG_LIST);
        bookName = getArguments().getString(CatalogLessonListActivity.EXTRA_BOOK_NAME);
        if (calalogs != null && calalogs.size() > 0) {
            getCurrListViewHelper().setData(calalogs);
            getCurrListView().expandGroup(0);
        }
        if (headTitleView != null&&bookName!=null) {
            headTitleView.setText(bookName);
        }
    }


    protected void initCataLogExpandListView() {
        catalogExpandListView = ((ExpandableListView) findViewById(R.id.catlog_expand_listview));
        if (catalogExpandListView != null) {
            catalogExpandListView.setGroupIndicator(null);
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.contacts_search_expand_list_item,
                    R.layout.contacts_expand_list_child_item) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((Calalog) getData().get(groupPosition))
                            .getChildren().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        Calalog calalog = (Calalog)
                                getData().get(groupPosition);
                        if (calalog != null && calalog.getChildren() != null) {
                            return calalog.getChildren().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    Calalog data = (Calalog) getChild(groupPosition, childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setVisibility(View.INVISIBLE);
                    }

                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    textView = (TextView) view.findViewById(R.id.contacts_item_status);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    Calalog data = (Calalog) getGroup(groupPosition);
                    View headerView = view.findViewById(R.id.contacts_item_header_layout);
                    if (headerView != null) {
                        headerView.setVisibility(View.GONE);
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
                    if (imageView != null) {
                        imageView.setImageResource(R.drawable.catalog_dot);
                        ((LinearLayout.LayoutParams) imageView.getLayoutParams()).setMargins(0, 0, 0, 0);
                        ;
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
                    if (textView != null) {
                        textView.setText(data.getName());
                    }
                    imageView = (ImageView) view.findViewById(R.id.contacts_item_arrow);
                    if (imageView != null) {
                        if (data.getChildren() == null || data.getChildren().size() == 0) {
                            imageView.setVisibility(View.INVISIBLE);
                        } else {
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setImageResource(isExpanded ?
                                    R.drawable.list_exp_up : R.drawable.list_exp_down);
                        }
                    }

                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    view.setTag(holder);
                    holder.data = data;
                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    catalogExpandListView, dataAdapter) {
                @Override
                public void loadData() {
                    loadCatalogData();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    Calalog calalog = (Calalog) holder.data;
                    getDataAdapter().notifyDataSetChanged();
                    itemClickEvent(calalog);
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    Calalog calalog = (Calalog) holder.data;
                    if (calalog.getChildren() != null && calalog.getChildren().size() > 0) {
                        return false;
                    }
                    getDataAdapter().notifyDataSetChanged();
                    itemClickEvent(calalog);
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(catalogExpandListView, listViewHelper);
        }
    }

    private void itemClickEvent(Calalog catalog) {
        Intent intent = new Intent();
        intent.putExtra(CatalogLessonListActivity.BOOK_CATALOG_ITEM, (Serializable) catalog);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    private class MyViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }


}
