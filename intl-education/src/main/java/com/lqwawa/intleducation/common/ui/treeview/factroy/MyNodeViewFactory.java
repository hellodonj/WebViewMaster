package com.lqwawa.intleducation.common.ui.treeview.factroy;

import android.view.View;

import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewFactory;
import com.lqwawa.intleducation.common.ui.treeview.binder.FirstLevelNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.binder.SecondLevelNodeViewBinder;

import java.util.HashMap;
import java.util.Map;

public class MyNodeViewFactory extends BaseNodeViewFactory {

//    private Map<Integer, BaseNodeViewBinder> binderMap = new HashMap<>();

    //可以有无数级，只要继续setlevel等级
    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {

        switch (level) {
            case 0:
                FirstLevelNodeViewBinder firstLevelNodeViewBinder = new FirstLevelNodeViewBinder(view);
//                binderMap.put(level, firstLevelNodeViewBinder);
                return firstLevelNodeViewBinder;
            case 1:
                SecondLevelNodeViewBinder secondLevelNodeViewBinder = new SecondLevelNodeViewBinder(view);
//                binderMap.put(level, secondLevelNodeViewBinder);
                return secondLevelNodeViewBinder;
            default:
                return null;
        }
    }

//    public BaseNodeViewBinder getViewBinder(int level) {
//        return binderMap.get(level);
//    }
}
