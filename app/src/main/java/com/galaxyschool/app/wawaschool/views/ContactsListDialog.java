package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.DataAdapter;

import java.util.List;

public class ContactsListDialog<T> extends Dialog implements View.OnClickListener {

    private Context context;
    private AdapterViewHelper adapterViewHelper;
    private DataAdapter.AdapterViewCreator itemViewCreator;
    private AdapterView.OnItemClickListener itemViewClickListener;
    private OnClickListener cancelButtonClickListner;
    private View rootView;
    private ListView listView;
    private Button cancelButton;
    private LinearLayout bodyLayout;

    public ContactsListDialog(Context context, String title, List<T> contentList,
            int itemViewLayout, DataAdapter.AdapterViewCreator itemViewCreator,
            AdapterView.OnItemClickListener itemClickListener,
            String cancelButtonText, OnClickListener cancelButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title, contentList,
                itemViewLayout, itemViewCreator, itemClickListener,
                cancelButtonText, cancelButtonClickListener);
    }

    public ContactsListDialog(Context context, int theme, String title, List<T> contentList,
            int itemViewLayout, DataAdapter.AdapterViewCreator itemViewCreator,
            AdapterView.OnItemClickListener itemClickListener,
            String cancelButtonText, OnClickListener cancelButtonClickListener) {
        super(context, theme);
        this.context = context;
        this.itemViewCreator = itemViewCreator;
        this.itemViewClickListener = itemClickListener;
        this.cancelButtonClickListner = cancelButtonClickListener;

        rootView = LayoutInflater.from(context).inflate(R.layout.contacts_list_dialog, null);
        TextView textView = (TextView) rootView.findViewById(R.id.contacts_dialog_title);
        if (textView != null) {
            textView.setText(title);
        }

        bodyLayout = (LinearLayout) rootView.findViewById(R.id.layout_body);

        View view = rootView.findViewById(R.id.contacts_dialog_title_layout);
        if (view != null && TextUtils.isEmpty(title)) {
            view.setVisibility(View.GONE);
        }

        listView = (ListView) rootView.findViewById(R.id.contacts_dialog_list_view);
        if (listView != null) {
            this.adapterViewHelper = new AdapterViewHelper(context, listView, itemViewLayout) {
                @Override
                public void loadData() {

                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    dismiss();
                    if (ContactsListDialog.this.itemViewClickListener != null) {
                        ContactsListDialog.this.itemViewClickListener.onItemClick(
                                parent, view, position, id);
                    }
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        view.setTag(getDataAdapter().getItem(position));
                    }
                    if (ContactsListDialog.this.itemViewCreator != null) {
                        view = ContactsListDialog.this.itemViewCreator.getView(
                                position, view, parent);
                    }
                    return view;
                }
            };
            this.adapterViewHelper.setData(contentList);
        }

        cancelButton = (Button) rootView.findViewById(R.id.contacts_dialog_cancel_button);
        if (cancelButton != null) {
            cancelButton.setText(cancelButtonText);
            cancelButton.setOnClickListener(this);
        }

        setContentView(rootView);
        resizeDialog(1.0f);
    }

    public View getContentView(){
        return rootView;
    }
    public ListView getListView() {
        return listView;
    }

    public Button getCancelButton(){
        return cancelButton;
    }

    public LinearLayout getBodyLayout(){
        return bodyLayout;
    }

    public void resizeDialog(float resize){
        if (resize <= 0){
            return;
        }
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        this.show();
        WindowManager windowManager = ((Activity)this.context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int)(display.getWidth() * resize);
        window.setAttributes(lp);
    }

    public AdapterViewHelper getAdapterViewHelper() {
        return this.adapterViewHelper;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_dialog_cancel_button) {
            dismiss();
            if (this.cancelButtonClickListner != null) {
                this.cancelButtonClickListner.onClick(ContactsListDialog.this, v.getId());
            }
        }
    }

}
