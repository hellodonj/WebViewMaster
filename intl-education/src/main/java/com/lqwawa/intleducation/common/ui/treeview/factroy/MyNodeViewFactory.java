package com.lqwawa.intleducation.common.ui.treeview.factroy;

import android.view.View;

import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewFactory;
import com.lqwawa.intleducation.common.ui.treeview.binder.FirstLevelNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.binder.SecondLevelNodeViewBinder;

public class MyNodeViewFactory extends BaseNodeViewFactory {

    //可以有无数级，只要继续setlevel等级
    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {
        switch (level) {
            case 0:
                return new FirstLevelNodeViewBinder(view);
            case 1:
                return new SecondLevelNodeViewBinder(view);
            default:
                return null;
        }
    }
}
