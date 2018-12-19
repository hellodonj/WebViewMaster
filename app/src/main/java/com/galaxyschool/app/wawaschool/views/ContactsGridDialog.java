package com.galaxyschool.app.wawaschool.views;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;

import java.util.List;

public class ContactsGridDialog<T> extends Dialog implements View.OnClickListener {

    private Context context;
    private AdapterViewHelper adapterViewHelper;
    private DataAdapter.AdapterViewCreator itemViewCreator;
    private AdapterView.OnItemClickListener itemViewClickListener;
    private OnClickListener cancelButtonClickListner;
    private AbsListView absListView;
    private TextView titleTextView;
    private ImageView leftCancelImageView;

    public ContactsGridDialog(Context context, String title, List<T> contentList,
                              int itemViewLayout, DataAdapter.AdapterViewCreator itemViewCreator,
                              AdapterView.OnItemClickListener itemClickListener,
                              String cancelButtonText, OnClickListener cancelButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title, contentList,
                itemViewLayout, itemViewCreator, itemClickListener,
                cancelButtonText, cancelButtonClickListener);
    }

    public ContactsGridDialog(Context context, int theme, String title, List<T> contentList,
                              int itemViewLayout, DataAdapter.AdapterViewCreator itemViewCreator,
                              AdapterView.OnItemClickListener itemClickListener,
                              String cancelButtonText, OnClickListener cancelButtonClickListener) {
        super(context, theme);
        this.context = context;
        this.itemViewCreator = itemViewCreator;
        this.itemViewClickListener = itemClickListener;
        this.cancelButtonClickListner = cancelButtonClickListener;

        View rootView = LayoutInflater.from(context).inflate(R.layout.contacts_grid_dialog, null);
        TextView textView = (TextView) rootView.findViewById(R.id.contacts_dialog_title);
        if (textView != null) {
            titleTextView = textView;
            textView.setText(title);
        }

        View view = rootView.findViewById(R.id.contacts_dialog_title_layout);
        if (view != null && TextUtils.isEmpty(title)) {
            view.setVisibility(View.GONE);
        }

        GridView listView = (GridView) rootView.findViewById(R.id.contacts_dialog_grid_view);
        if (listView != null) {
            this.absListView = listView;
            this.adapterViewHelper = new AdapterViewHelper(context, listView, itemViewLayout) {
                @Override
                public void loadData() {

                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    dismiss();
                    if (ContactsGridDialog.this.itemViewClickListener != null) {
                        ContactsGridDialog.this.itemViewClickListener.onItemClick(
                                parent, view, position, id);
                    }
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        view.setTag(getDataAdapter().getItem(position));
                    }
                    if (ContactsGridDialog.this.itemViewCreator != null) {
                        view = ContactsGridDialog.this.itemViewCreator.getView(
                                position, view, parent);
                    }
                    return view;
                }
            };
            this.adapterViewHelper.setData(contentList);
        }

        ImageView button = (ImageView) rootView.findViewById(R.id.contacts_dialog_left_button);
        if (button != null) {
//            button.setText(canceuttonText);
            leftCancelImageView = button;
            button.setOnClickListener(this);
        }

        setContentView(rootView);
    }

    public TextView getTitleTextView(){
        return titleTextView;
    }

    public ImageView getLeftCancelImageView(){
        return leftCancelImageView;
    }

    public AdapterViewHelper getAdapterViewHelper() {
        return this.adapterViewHelper;
    }

    public AbsListView getAbsListView(){
        return absListView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_dialog_left_button) {
            dismiss();
            if (this.cancelButtonClickListner != null) {
                this.cancelButtonClickListner.onClick(ContactsGridDialog.this, v.getId());
            }
        }
    }

}
