package com.galaxyschool.app.wawaschool.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.galaxyschool.app.wawaschool.R;

public class ShareTypeSelectDialog extends Dialog implements
		View.OnClickListener, AdapterView.OnItemClickListener {
	Context mContext;
	ListView listView;
	int mPosition = 0;
	ShareTypeSelectHandler mHandler = null;

	public ShareTypeSelectDialog(Context context, ShareTypeSelectHandler handler) {
		super(context, com.oosic.apps.icoursebase.R.style.Theme_PageDialog);
		mContext = context;
		mHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_type_select_view);
		findViewById(R.id.confirm_btn).setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listview);
		String[] materialTypes = mContext.getResources().getStringArray(R.array.share_type);
		listView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.share_type_list_item, materialTypes));
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setItemChecked(mPosition, true);
		listView.setOnItemClickListener(this);
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.confirm_btn) {
			if (mPosition >= 0 && mHandler != null) {
				mHandler.shareTypeSelect(mPosition);
				ShareTypeSelectDialog.this.dismiss();
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mPosition = position;
		listView.setItemChecked(mPosition, true);
	}

	public interface ShareTypeSelectHandler {
		public void shareTypeSelect(int type);
	}
}
