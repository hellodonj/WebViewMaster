/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under.
 */

package com.lqwawa.intleducation.common.ui.treeview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.ui.treeview.animator.TreeItemAnimator;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewFactory;
import com.lqwawa.intleducation.common.ui.treeview.base.SelectableTreeAction;
import com.lqwawa.intleducation.common.ui.treeview.helper.TreeHelper;

import java.util.List;


/**
 * Created by xinyuanzhong on 2017/4/20.
 */

public class TreeView implements SelectableTreeAction {
    private TreeNode root;

    private Context context;

    private BaseNodeViewFactory baseNodeViewFactory;

    private RecyclerView rootView;

    private TreeViewAdapter adapter;

    private boolean itemSelectable = true;

    private Object extras;
    private RecyclerView recyclerView;

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        this.itemAnimator = itemAnimator;
        if (rootView != null && itemAnimator != null) {
            rootView.setItemAnimator(itemAnimator);
        }
    }

    private RecyclerView.ItemAnimator itemAnimator;

    public TreeView(@NonNull TreeNode root, @NonNull Context context, @NonNull BaseNodeViewFactory baseNodeViewFactory) {
        this.root = root;
        this.context = context;
        this.baseNodeViewFactory = baseNodeViewFactory;
        if (baseNodeViewFactory == null) {
            throw new IllegalArgumentException("You must assign a BaseNodeViewFactory!");
        }
    }

    public void setExtras(Object extras) {
        this.extras = extras;
    }

    public Object getExtras() {
        return extras;
    }

    public View getView() {
        if (rootView == null) {
            this.rootView = buildRootView();
        }

        return rootView;
    }

    @NonNull
    private RecyclerView buildRootView() {
        recyclerView = new RecyclerView(context);
        /**
         * disable multi touch event to prevent terrible data set error when calculate list.
         */
        recyclerView.setMotionEventSplittingEnabled(false);

        recyclerView.setItemAnimator(itemAnimator != null ? itemAnimator : new TreeItemAnimator());
        SimpleItemAnimator itemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        adapter = new TreeViewAdapter(context, root, baseNodeViewFactory);
        adapter.setTreeView(this);
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        if (recyclerView != null)
            recyclerView.addItemDecoration(itemDecoration);
    }


    @Override
    public void expandAll() {
        if (root == null) {
            return;
        }
        TreeHelper.expandAll(root);

        refreshTreeView();
    }


    public void refreshTreeView() {
        if (rootView != null) {
            ((TreeViewAdapter) rootView.getAdapter()).refreshView();
        }
    }

    public void notifychanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setIsShowCheckBox(boolean isShowCheckBox) {
        if (adapter != null) {
            adapter.setIsShowCheckBox(isShowCheckBox);
        }
    }

    @Override
    public void expandNode(TreeNode treeNode) {
        adapter.expandNode(treeNode);
    }

    @Override
    public void expandLevel(int level) {
        TreeHelper.expandLevel(root, level);

        refreshTreeView();
    }

    @Override
    public void collapseAll() {
        if (root == null) {
            return;
        }
        TreeHelper.collapseAll(root);

        refreshTreeView();
    }

    @Override
    public void collapseNode(TreeNode treeNode) {
        adapter.collapseNode(treeNode);
    }

    @Override
    public void collapseLevel(int level) {
        TreeHelper.collapseLevel(root, level);

        refreshTreeView();
    }

    @Override
    public void toggleNode(TreeNode treeNode) {
        if (treeNode.isExpanded()) {
            collapseNode(treeNode);
        } else {
            expandNode(treeNode);
        }
    }

    @Override
    public void deleteNode(TreeNode node) {
        adapter.deleteNode(node);
    }

    @Override
    public void addNode(TreeNode parent, TreeNode treeNode) {
        parent.addChild(treeNode);

        refreshTreeView();
    }

    @Override
    public List<TreeNode> getAllNodes() {
        return TreeHelper.getAllNodes(root);
    }

    @Override
    public void selectNode(TreeNode treeNode) {
        if (treeNode != null) {
            adapter.selectNode(true, treeNode);
        }
    }

    @Override
    public void deselectNode(TreeNode treeNode) {
        if (treeNode != null) {
            adapter.selectNode(false, treeNode);
        }
    }

    @Override
    public void selectAll() {
        TreeHelper.selectNodeAndChild(root, true);

        refreshTreeView();
    }

    @Override
    public void deselectAll() {
        TreeHelper.selectNodeAndChild(root, false);

        refreshTreeView();
    }

    @Override
    public List<TreeNode> getSelectedNodes() {
        return TreeHelper.getSelectedNodes(root);
    }

    public boolean isItemSelectable() {
        return itemSelectable;
    }

    public void setItemSelectable(boolean itemSelectable) {
        this.itemSelectable = itemSelectable;
    }

    public void onItemClickedListener(int position, TreeNode treeNode, boolean expanded, Context context) {
        if (onItemClilcedListener != null)
            onItemClilcedListener.onItemClickedListener(position, treeNode, expanded, context);
    }

    private OnItemClilcedListener onItemClilcedListener;

    public interface OnItemClilcedListener {
        void onItemClickedListener(int position, TreeNode treeNode, boolean expanded, Context context);
    }

    public void setOnItemClilcedListener(OnItemClilcedListener onItemClilcedListener) {
        this.onItemClilcedListener = onItemClilcedListener;
    }

    public interface OnItemCheckBoxSelectedChanged {
        void onItemCheckBoxSelectedChanged(Context context, TreeNode treeNode, boolean checked);
    }

    private OnItemCheckBoxSelectedChanged onItemCheckBoxSelectedChanged;

    public void setOnItemCheckBoxSelectedChanged(OnItemCheckBoxSelectedChanged onItemCheckBoxSelectedChanged) {
        this.onItemCheckBoxSelectedChanged = onItemCheckBoxSelectedChanged;
    }

    public void onItemCheckBoxSelectedChanged(Context context, TreeNode treeNode, boolean checked) {
        if (onItemCheckBoxSelectedChanged != null)
            onItemCheckBoxSelectedChanged.onItemCheckBoxSelectedChanged(context, treeNode, checked);
    }
}
