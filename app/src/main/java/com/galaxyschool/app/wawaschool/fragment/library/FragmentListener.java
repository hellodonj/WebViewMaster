package com.galaxyschool.app.wawaschool.fragment.library;

import android.content.Intent;

public interface FragmentListener {

    public interface FragmentResultListener {
        public void onFragmentResult(int requestCode, int resultCode, Intent data);
    }

}
