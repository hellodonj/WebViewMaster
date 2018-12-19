package com.galaxyschool.app.wawaschool.fragment.library;

import android.widget.AdapterView;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterFragment<T> extends BaseFragment {

    protected Map<String, AdapterViewHelper> adapterViewHelpers =
            new HashMap<String, AdapterViewHelper>();

    protected AdapterView currAdapterView;
    protected AdapterViewHelper currAdapterViewHelper;

    protected List<AdapterViewHelper> getAllAdapterViewHelpers() {
        if (adapterViewHelpers.size() <= 0) {
            return null;
        }
        List<AdapterViewHelper> helpers = new ArrayList();
        for (Map.Entry<String, AdapterViewHelper> entry : adapterViewHelpers.entrySet()) {
            helpers.add(entry.getValue());
        }
        return helpers;
    }

    public void addAdapterViewHelper(String tag, AdapterViewHelper helper) {
        this.adapterViewHelpers.put(tag, helper);
    }

    public AdapterViewHelper getAdapterViewHelper(String tag) {
        return this.adapterViewHelpers.get(tag);
    }

    public AdapterView getCurrAdapterView() {
        return this.currAdapterView;
    }

    public void setCurrAdapterViewHelper(AdapterView adapterView,
                                         AdapterViewHelper adapterViewHelper) {
        this.currAdapterView = adapterView;
        this.currAdapterView.setOnItemClickListener(adapterViewHelper);
        this.currAdapterViewHelper = adapterViewHelper;
    }

    public AdapterViewHelper getCurrAdapterViewHelper() {
        return this.currAdapterViewHelper;
    }

}
