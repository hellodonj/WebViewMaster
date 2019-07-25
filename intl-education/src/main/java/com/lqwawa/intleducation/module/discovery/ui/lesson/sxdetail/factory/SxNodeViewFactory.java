package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.factory;

import android.view.View;

import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewFactory;
import com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.binder.SxFirstLevelNodeViewBinder;
import com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.binder.SxSecondLevelNodeViewBinder;

public class SxNodeViewFactory extends BaseNodeViewFactory {

    //可以有无数级，只要继续setlevel等
    @Override
    public BaseNodeViewBinder getNodeViewBinder(View view, int level) {

        switch (level) {
            case 0:
               SxFirstLevelNodeViewBinder firstLevelNodeViewBinder = new SxFirstLevelNodeViewBinder(view);
//                binderMap.put(level, firstLevelNodeViewBinder);
                return firstLevelNodeViewBinder;
            case 1:
                SxSecondLevelNodeViewBinder secondLevelNodeViewBinder = new SxSecondLevelNodeViewBinder(view);
//                binderMap.put(level, secondLevelNodeViewBinder);
                return secondLevelNodeViewBinder;
            default:
                return null;
        }
    }

}
