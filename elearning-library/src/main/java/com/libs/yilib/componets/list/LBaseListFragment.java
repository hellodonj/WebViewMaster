package com.libs.yilib.componets.list;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.lqwawa.apps.R;

/**
 * @author 作者 shouyi
 * @version 创建时间：Sep 29, 2015 5:37:15 PM 类说明
 */
public abstract class LBaseListFragment extends Fragment {
	protected ListView mListView;
	protected ViewHelper mViewHelper;
	protected DataHelper mDataHelper;
	protected BaseAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = getView(inflater);
		mAdapter = new EntryListFragmengAdapter();
		return view;
	}

	protected View getView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.common_listview, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
	}

	protected void initViews() {
		mListView = (ListView) getView().findViewById(R.id.listview);
		mListView.setAdapter(mAdapter);
	}

	protected void clearData() {
		if (mDataHelper != null) {
			mDataHelper.clear();
		}
	}

	protected void updateView() {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	protected class ViewHelper {

	}

	abstract protected class DataHelper<T> {
		protected List<T> mDatas;

		abstract public void loadData();
		
		public List<T> getDatas() {
			return mDatas;
		}
		
		public void setDatas(List<T> datas) {
			mDatas = datas;
		}

		public void append(T data) {
			if (mDatas == null) {
				mDatas = new ArrayList<T>();
			}
			mDatas.add(data);
		}
		
		protected void clear() {
			mDatas.clear();
		}

		public int getCount() {
			return mDatas == null ? 0 : mDatas.size();
		}

		public Object getObject(int index) {
			return mDatas == null ? null : mDatas.get(index);
		}
	}

	private class EntryListFragmengAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDataHelper == null ? 0 : mDataHelper.getCount();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return mDataHelper == null ? null : mDataHelper.getObject(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return LBaseListFragment.this
					.getView(position, convertView, parent);
		}
	}

	TheBroadCastReceiver mReceiver;

	public void registReceiver(IntentFilter filter) {
		if (getActivity() != null) {
			mReceiver = new TheBroadCastReceiver();
			getActivity().registerReceiver(mReceiver, filter);
		}
	}

	public void unregistReceiver() {
		if (mReceiver != null && getActivity() != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}

	protected void onReceive(Context context, Intent intent) {

	}

	class TheBroadCastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			LBaseListFragment.this.onReceive(context, intent);
		}
	}

	abstract public View getView(int position, View convertView,
			ViewGroup parent);

	abstract protected void initDataHelper();
}
