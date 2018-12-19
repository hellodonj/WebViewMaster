package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsPickerFragment extends ContactsListFragment {

    private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    private boolean treatAllHelpersAsOne;

    public void setTreatAllHelpersAsOne(boolean asOne) {
        this.treatAllHelpersAsOne = asOne;
    }

    public void selectItem(int position, boolean selected) {
        if (getCurrAdapterViewHelper() != null) {
            position += getCurrAdapterViewHelper().getPositionOffset();
        }
        this.map.put(position, selected);
    }

    protected void selectItem(AdapterViewHelper helper, int position, boolean selected) {
        this.map.put(position + helper.getPositionOffset(), selected);
    }

    public void selectAllItems(boolean selected) {
        AdapterViewHelper currHelper = getCurrAdapterViewHelper();
        if (currHelper != null) {
            selectAllItems(currHelper, selected);
            return;
        }
        if (!this.treatAllHelpersAsOne) {
            return;
        }
        List<AdapterViewHelper> allHelpers = getAllAdapterViewHelpers();
        if (allHelpers == null || allHelpers.size() <= 0) {
            return;
        }
        for (AdapterViewHelper helper : allHelpers) {
            selectAllItems(helper, selected);
        }
    }

    protected void selectAllItems(AdapterViewHelper helper, boolean selected) {
        List list = helper.getData();
        if (list == null || list.size() <= 0) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            this.map.put(i + helper.getPositionOffset(), selected);
        }
    }

    public boolean isItemSelected(int position) {
        if (getCurrAdapterViewHelper() != null) {
            position += getCurrAdapterViewHelper().getPositionOffset();
        }
        if (!this.map.containsKey(position)) {
            return false;
        }
        return this.map.get(position);
    }

    protected boolean isItemSelected(AdapterViewHelper helper, int position) {
        if (!this.map.containsKey(position + helper.getPositionOffset())) {
            return false;
        }
        return this.map.get(position + helper.getPositionOffset());
    }

    public boolean isAllItemsSelected() {
        AdapterViewHelper currHelper = getCurrAdapterViewHelper();
        if (currHelper != null) {
            return isAllItemsSelected(currHelper);
        }
        if (!this.treatAllHelpersAsOne) {
            return false;
        }
        List<AdapterViewHelper> allHelpers = getAllAdapterViewHelpers();
        if (allHelpers == null || allHelpers.size() <= 0) {
            return false;
        }
        boolean result = true;
        for (AdapterViewHelper helper : allHelpers) {
            result &= isAllItemsSelected(helper);
        }
        return result;
    }

    protected boolean isAllItemsSelected(AdapterViewHelper helper) {
        List list = helper.getData();
        if (list == null || list.size() <= 0) {
            return false;
        }

        boolean allSelected = true;
        for (int i = 0; i < list.size(); i++) {
            if (!this.map.containsKey(i + helper.getPositionOffset())
                    || !this.map.get(i + helper.getPositionOffset())) {
                allSelected = false;
                break;
            }
        }
        return allSelected;
    }

    public int getSelectedItemsCount() {
        AdapterViewHelper currHelper = getCurrAdapterViewHelper();
        if (currHelper != null) {
            return getSelectedItemsCount(currHelper);
        }
        if (!this.treatAllHelpersAsOne) {
            return 0;
        }
        List<AdapterViewHelper> allHelpers = getAllAdapterViewHelpers();
        if (allHelpers == null || allHelpers.size() <= 0) {
            return 0;
        }
        int result = 0;
        for (AdapterViewHelper helper : allHelpers) {
            result += getSelectedItemsCount(helper);
        }
        return result;
    }

    protected int getSelectedItemsCount(AdapterViewHelper helper) {
        List list = helper.getData();
        if (list == null || list.size() <= 0) {
            return 0;
        }

        int result = 0;
        for (int i = 0; i < list.size(); i++) {
            if (this.map.containsKey(i + helper.getPositionOffset())
                    && this.map.get(i + helper.getPositionOffset()).booleanValue()) {
                result++;
            }
        }

        return result;
    }

    public List getSelectedItems() {
        AdapterViewHelper currHelper = getCurrAdapterViewHelper();
        if (currHelper != null) {
            return getSelectedItems(currHelper);
        }
        if (!this.treatAllHelpersAsOne) {
            return null;
        }
        List<AdapterViewHelper> allHelpers = getAllAdapterViewHelpers();
        if (allHelpers == null || allHelpers.size() <= 0) {
            return null;
        }
        List result = new ArrayList();
        List list = null;
        for (AdapterViewHelper helper : allHelpers) {
            list = getSelectedItems(helper);
            if (list != null && list.size() > 0) {
                result.addAll(list);
            }
        }
        return result;
    }

    protected List getSelectedItems(AdapterViewHelper helper) {
        List list = helper.getData();
        if (list == null || list.size() <= 0) {
            return null;
        }

        List result = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (this.map.containsKey(i + helper.getPositionOffset())
                    && this.map.get(i + helper.getPositionOffset()).booleanValue()) {
                result.add(list.get(i));
            }
        }

        return result;
    }

    public boolean hasSelectedItems() {
        AdapterViewHelper currHelper = getCurrAdapterViewHelper();
        if (currHelper != null) {
            return hasSelectedItems(currHelper);
        }
        if (!this.treatAllHelpersAsOne) {
            return false;
        }
        List<AdapterViewHelper> allHelpers = getAllAdapterViewHelpers();
        if (allHelpers == null || allHelpers.size() <= 0) {
            return false;
        }
        for (AdapterViewHelper helper : allHelpers) {
            if (hasSelectedItems(helper)) {
                return true;
            }
        }
        return false;
    }

    protected boolean hasSelectedItems(AdapterViewHelper helper) {
        List list = helper.getData();
        if (list == null || list.size() <= 0) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            if (this.map.containsKey(i + helper.getPositionOffset())
                    && this.map.get(i + helper.getPositionOffset()).booleanValue()) {
                return true;
            }
        }

        return false;
    }

    protected void addStudentToMyClass(String classId, List<String> studentIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", getMemeberId());
        params.put("ClassId", classId);
        params.put("StudentList", studentIds);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            TipsHelper.showToast(getActivity(), R.string.add_student_success);
                            if(getActivity() != null) {
                                getActivity().finish();
                                ContactsPickerFragment.this.setResult(Activity.RESULT_OK);
                            }
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.ADD_STUDENT_TO_CLASS_URL,
                params, listener);
    }

    protected class MyViewHolder extends ViewHolder {
        int position;
        ImageView selectorView;
    }

}
