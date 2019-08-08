package com.galaxyschool.app.wawaschool.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

public class ReMarkTaskListFragment extends ContactsListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_remark_task_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initView();
    }

    private void loadIntentData(){

    }

    private void initView() {
        TextView titleView = (TextView) findViewById(R.id.contacts_header_title);
        if (titleView != null){
            titleView.setText(getString(R.string.str_un_remark_homework));
        }
        ImageView backImageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (backImageView != null){
            backImageView.setOnClickListener(v -> finish());
        }
    }
}
