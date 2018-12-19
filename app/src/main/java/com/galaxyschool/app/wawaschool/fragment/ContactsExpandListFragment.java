package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.MyPageHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.Date;

public class ContactsExpandListFragment extends ExpandListFragment
        implements View.OnClickListener {

    public static final String TAG = ContactsExpandListFragment.class.getSimpleName();

    protected PullToRefreshView pullToRefreshView;
    protected MyPageHelper pageHelper = new MyPageHelper();
    protected boolean pullToRefreshByTouch;
    protected  boolean stopPullUpState=false;
    protected  boolean stopPullDownState=false;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void finish() {
        super.finish();
        getActivity().finish();
    }

    private void initViews() {
        ImageView imageView = (ImageView) getView().findViewById(
                R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
    }

    public void setPullToRefreshView(PullToRefreshView view) {
        this.pullToRefreshView = view;

        if (view != null) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(stopPullUpState){
                        if (pullToRefreshView.getPullState() == PullToRefreshView.PULL_UP_STATE) {
                            return true;
                        }
                    }
                    if(stopPullDownState){
                        if (pullToRefreshView.getPullState() == PullToRefreshView.PULL_DOWN_STATE) {
                            return true;
                        }
                    }
                    if(isLoadingData) {
                        return true;
                    }
                    return false;
                }
            });
            view.setOnHeaderRefreshListener(
                    new PullToRefreshView.OnHeaderRefreshListener() {
                        @Override
                        public void onHeaderRefresh(PullToRefreshView view) {
                            pullToRefreshByTouch = true;
                            pullToRefreshView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    pullToRefreshByTouch = false;
                                	if (getActivity() != null) {
                                		String updateTime = getActivity().getString(R.string.cs_update_time);
                                		pullToRefreshView.onHeaderRefreshComplete(
                                				updateTime + new Date().toLocaleString());
									}
                                }
                            }, 1000);
                            pageHelper.clear();
//                            pageHelper.setFetchingPageIndex(pageHelper.getCurrPageIndex());
//                            getCurrListViewHelper().clearData();
                            pageHelper.setFetchingPageIndex(0);
                            getCurrListViewHelper().loadData();
                        }
                    });
           view.setOnFooterRefreshListener(
                   new PullToRefreshView.OnFooterRefreshListener() {
                       @Override
                       public void onFooterRefresh(PullToRefreshView view) {
                           pullToRefreshByTouch = true;
                           pullToRefreshView.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   pullToRefreshByTouch = false;
                                   pullToRefreshView.onFooterRefreshComplete();
                               }
                           }, 1000);
                          // if(pullToRefreshView.isFootViewVisiable()){
                               pageHelper.setFetchingPageIndex(pageHelper.getCurrPageIndex() + 1);
                               getCurrListViewHelper().loadData();
                           //}
                       }
                   });
            if (getActivity()!=null) {
                String updateTime = getActivity().getString(R.string.cs_update_time);
                view.setLastUpdated(updateTime + new Date().toLocaleString());
            }
        }
    }

    protected MyPageHelper getPageHelper() {
        return this.pageHelper;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finish();
        }
    }

    protected class DefaultPullToRefreshListener<T extends ModelResult>
            extends DefaultListener<T> {
        private boolean showPullToRefresh = true;

        public boolean isShowPullToRefresh() {
            return showPullToRefresh;
        }

        public void setShowPullToRefresh(boolean show) {
            this.showPullToRefresh = show;
        }

        public DefaultPullToRefreshListener(Class resultClass) {
            super(resultClass);
            setShowLoading(false);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (this.showPullToRefresh && !pullToRefreshByTouch) {
                pullToRefreshView.showRefresh();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (this.showPullToRefresh) {
                pullToRefreshView.hideRefresh();
            }
        }
    }

    protected class DefaultPullToRefreshDataListener<T extends DataModelResult>
            extends DefaultDataListener<T> {
        private boolean showPullToRefresh = true;

        public boolean isShowPullToRefresh() {
            return showPullToRefresh;
        }

        public void setShowPullToRefresh(boolean show) {
            this.showPullToRefresh = show;
        }

        public DefaultPullToRefreshDataListener(Class resultClass) {
            super(resultClass);
            setShowLoading(false);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (pullToRefreshView != null) {
                if (this.showPullToRefresh && !pullToRefreshByTouch) {
                    pullToRefreshView.showRefresh();
                }
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if (pullToRefreshView != null) {
                if (this.showPullToRefresh) {
                    pullToRefreshView.hideRefresh();
                }
            }
        }
    }

    public static void showSoftKeyboard(Activity activity) {
        try {
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInputFromInputMethod(activity.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.SHOW_FORCED);
        } catch (Exception e) {

        }
    }

    public boolean isStopPullDownState() {
        return stopPullDownState;
    }

    public void setStopPullDownState(boolean stopPullDownState) {
        this.stopPullDownState = stopPullDownState;
    }

    public boolean isStopPullUpState() {
        return stopPullUpState;
    }

    public void setStopPullUpState(boolean stopPullUpState) {
        this.stopPullUpState = stopPullUpState;
    }
}
