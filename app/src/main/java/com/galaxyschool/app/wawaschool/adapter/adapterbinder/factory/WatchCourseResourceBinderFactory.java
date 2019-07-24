package com.galaxyschool.app.wawaschool.adapter.adapterbinder.factory;

import android.view.View;

import com.galaxyschool.app.wawaschool.adapter.adapterbinder.LibraryLevelNodeViewBinder;
import com.galaxyschool.app.wawaschool.adapter.adapterbinder.LibrarySecondNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewFactory;

public class WatchCourseResourceBinderFactory extends BaseNodeViewFactory {

    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
        switch (level) {
            case 0:
                BaseNodeViewBinder firstLevelNodeViewBinder = new LibraryLevelNodeViewBinder(view);
                return firstLevelNodeViewBinder;
            case 1:
                BaseNodeViewBinder secondLevelNodeViewBinder = new LibrarySecondNodeViewBinder(view);
                return secondLevelNodeViewBinder;
            default:
                return null;
        }
    }
}
