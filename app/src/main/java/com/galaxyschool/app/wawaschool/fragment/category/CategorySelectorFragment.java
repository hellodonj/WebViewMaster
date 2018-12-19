package com.galaxyschool.app.wawaschool.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

public class CategorySelectorFragment extends CategorySelectorBaseFragment
        implements View.OnClickListener {

    public static final String TAG = CategorySelectorFragment.class.getSimpleName();

    public interface Constants extends CategorySelectorBaseFragment.Constants {
        public static final String EXTRA_TITLE = "title";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.category_selector_with_title, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    private void initViews() {
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (getArguments() != null) {
                textView.setText(getArguments().getString(Constants.EXTRA_TITLE));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finish();
        }
    }

}
