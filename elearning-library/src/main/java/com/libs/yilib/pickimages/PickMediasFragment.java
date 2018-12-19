package com.libs.yilib.pickimages;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libs.yilib.componets.list.LBaseGridFragment;
import com.libs.yilib.pickimages.ScanLocalMediaController.ScanLocalMediaListener;
import com.lqwawa.apps.R;
import com.lqwawa.tools.DialogHelper;
import com.lqwawa.tools.DialogHelper.LoadingDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.LogUtils;
import com.osastudio.common.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author 作者 shouyi
 * @version 创建时间：Dec 2, 2015 2:23:08 PM 类说明
 */
public class PickMediasFragment extends LBaseGridFragment implements
		ScanLocalMediaListener, OnItemClickListener {
	public static final String PICK_IMG_PARAM = "pic_img_param";
	public static final String PICK_IMG_RESULT = "pic_img_result";
	TextView mTitle;
	TextView mConfirmBtn;
	ScanLocalMediaController mScanLocalMediaController;
	ArrayList<MediaInfo> mSelectImageInfos = new ArrayList<MediaInfo>();
	PickMediaResultListener mPickImageListener;
	PickMediasParam mParam;
//	ImageLoader mImageLoader;
	LoadingDialog mLoadingDialog;
	List<MediaInfo> mMediaInfos = new ArrayList<MediaInfo>();

	@Override
	protected View getView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.pic_img_fragment, null);
	}

	@Override
	protected void initViews() {
		// TODO Auto-generated method stub
		super.initViews();
		getView().findViewById(R.id.back_base_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						cancelLoadImgs();
						goback();
					}
				});
		mTitle = (TextView) getView().findViewById(R.id.back_base_title);
		mConfirmBtn = (TextView) getView().findViewById(R.id.right_btn);
		mConfirmBtn.setVisibility(View.VISIBLE);
		mConfirmBtn.setText("confirm");
		mConfirmBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onConfrimBtnClick();
			}
		});
		mGridView.setOnItemClickListener(this);
		processParam();
	}

	private void processParam() {
		if (getArguments() != null) {
			mParam = (PickMediasParam) getArguments().getSerializable(
					PICK_IMG_PARAM);
			if (mParam != null) {
				if (mParam.mShowCountMode == 0) {
					if (mParam.mTitle != null) {
						mTitle.setText(mParam.mTitle);
					}
				} else if (mParam.mShowCountMode == 1) {
					if (mParam.mShowCountFormatString != null) {
						mTitle.setText(String.format(
								mParam.mShowCountFormatString,
								mParam.mPickLimitCount));
					}
				} else {
					if (mParam.mShowCountFormatString != null) {
						mTitle.setText(String.format(
								mParam.mShowCountFormatString, 0));
					}
				}
				if (mParam.mConfirmBtnName != null) {
					mConfirmBtn.setText(mParam.mConfirmBtnName);
				}
				if (mParam.mColumnWidth > 0) {
					mGridView.setColumnWidth(mParam.mColumnWidth);
				} else if (mParam.mColumns > 0) {
					mGridView.setNumColumns(mParam.mColumns);
				}
				if (mParam.mSortType > SortType.SORT_NONE) {
					if (mParam.mSortType == SortType.SORT_BY_TIME) {
						mLoadingDialog = DialogHelper.getIt(getActivity())
								.GetLoadingDialog(1);
						mLoadingDialog.setCancelable(false);
						mLoadingDialog.setCanceledOnTouchOutside(false);
					}
				}
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		mImageLoader = LQImageLoader.createAnImageLoader(getActivity());
		initDataHelper();
		mDataHelper.loadData();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		LQImageLoader.destroyImageLoader(mImageLoader);
	}

	@Override
	protected void initDataHelper() {
		// TODO Auto-generated method stub
		mDataHelper = new DataHelper<MediaInfo>() {
			@Override
			public void loadData() {
				// TODO Auto-generated method stub
				startLoad();
			}
		};
	}

	protected void startLoad() {
		LogUtils.logi("", "onSearched start");
		mScanLocalMediaController = new ScanLocalMediaController(
				mParam.mMediaType, getMediaFormaList(mParam.mMediaType), this);
		if (mParam.mSkipKeysOfFolder != null
				&& mParam.mSkipKeysOfFolder.size() > 0) {
			mScanLocalMediaController
					.setSkipKeysOfFolder(mParam.mSkipKeysOfFolder);
		}
		if (mParam != null && !TextUtils.isEmpty(mParam.mSearchPath)
				&& !isMountDir(mParam.mSearchPath)) {
			mScanLocalMediaController.start(mParam.mSearchPath);
		} else {
			mScanLocalMediaController.start(getDefaultSearchPaths());
		}
	}

	private boolean isMountDir(String path) {
		if (!TextUtils.isEmpty(path) && "/mnt".equals(path)) {
			return true;
		}
		return false;
	}

	private List<String> getDefaultSearchPaths() {
		List<String> volumes = Utils.getVolumeList(getActivity());
		if (volumes != null) {
			for (int i = 0; i < volumes.size(); i++) {
				LogUtils.logi("", "onSearched volume: " + volumes.get(i));
			}
		}
		return volumes;
	}

	private void onConfrimBtnClick() {
		if (mSelectImageInfos.size() > 0) {
			if (mPickImageListener != null) {
				mPickImageListener.onPickFinished(mSelectImageInfos);
			}
			goback();
		}
	}

	public void goback() {
		if (mGridView != null) {
			mGridView.setVisibility(View.GONE);
		}
		if (mParam != null && !mParam.mIsActivityCalled) {
			getActivity().getSupportFragmentManager().popBackStack();
		} else {
			getActivity().finish();
		}
	}

	public void setPickImageResultListener(PickMediaResultListener listener) {
		mPickImageListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (position < mDataHelper.getCount()) {
			MediaInfo info = (MediaInfo) mDataHelper.getObject(position);
			if (mParam != null && mParam.mPickLimitCount > 0) {
				if (mSelectImageInfos.size() >= mParam.mPickLimitCount
						&& !info.mIsSelected) {
					if (mParam.mPickLimitCount == 1) {
						mSelectImageInfos.get(0).mIsSelected = false;
						mSelectImageInfos.clear();
					} else {
						if (mParam.mLimitReachedTips != null) {
							Toast.makeText(getActivity(),
									mParam.mLimitReachedTips, Toast.LENGTH_LONG)
									.show();
						} else {
							Toast.makeText(getActivity(),
									"Can not select more", Toast.LENGTH_LONG)
									.show();
						}
						return;
					}
				}
			}
			info.mIsSelected = !info.mIsSelected;
			processSelect(info.mIsSelected, info);
		}
	}

	private void processSelect(boolean isAdd, MediaInfo imageInfo) {
		if (isAdd) {
			mSelectImageInfos.add(imageInfo);
		} else {
			for (MediaInfo info : mSelectImageInfos) {
				if (imageInfo.mPath != null) {
					if (imageInfo.mPath.equals(info.mPath)) {
						mSelectImageInfos.remove(info);
						break;
					}
				}
			}
		}
		updateView();
		if (mParam.mShowCountMode == 1) {
			if (mParam.mShowCountFormatString != null) {
				mTitle.setText(String.format(mParam.mShowCountFormatString,
						mParam.mPickLimitCount - mSelectImageInfos.size()));
			}
		} else if (mParam.mShowCountMode == 2) {
			if (mParam.mShowCountFormatString != null) {
				mTitle.setText(String.format(mParam.mShowCountFormatString,
						mSelectImageInfos.size()));
			}
		}
	}

	@Override
	public void onSearched(String file, int mediaType) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(file)) {
			MediaInfo mediaInfo = new MediaInfo(file);
			File file2 = new File(file);
			if (file2.exists()) {
				mediaInfo.mModifyTime = file2.lastModified();
			}
			if (mParam.mSortType == SortType.SORT_NONE) {
				mDataHelper.append(mediaInfo);
				updateViewInThread();
			} else {
				mMediaInfos.add(mediaInfo);
			}
		} else {
			LogUtils.logi("", "onSearched finish!!!");
			LogUtils.logi("", "onSearched mParam.mSortType: " + mParam.mSortType);
			if (mParam.mSortType != SortType.SORT_NONE) {
				if (mLoadingDialog.isShowing()) {
					mLoadingDialog.dismiss();
					LogUtils.logi("", "onSearched mLoadingDialog.dismiss()");
				}
				mDataHelper.setDatas(mMediaInfos);
				sortMedias(mParam.mSortType);
				updateViewInThread();
			}
		}
	}

	private void sortMedias(int sortType) {
		if (mDataHelper.getCount() > 0) {
			if (mParam.mSortType == SortType.SORT_BY_TIME) {
				Collections.sort(mDataHelper.getDatas(),
						new Comparator<MediaInfo>() {
							@Override
							public int compare(MediaInfo lhs, MediaInfo rhs) {
								// TODO Auto-generated method stub
								if (rhs.mModifyTime < lhs.mModifyTime) {
									return -1;
								} else if (rhs.mModifyTime == lhs.mModifyTime) {
									return 0;
								} else {
									return 1;
								}
							}
						});
			}
		}
	}

	private void updateViewInThread() {
		final Handler h = new Handler(Looper.getMainLooper());
		h.post(new Runnable() {
			public void run() {
				updateView();
			}
		});
	}

	protected void cancelLoadImgs() {
		if (mScanLocalMediaController != null) {
			mScanLocalMediaController.cancel(true);
		}
	}

	protected List<String> getMediaFormaList(int mediaType) {
		if (MediaType.MEDIA_TYPE_PHOTO == mediaType) {
			return getImageFormatList();
		} else if (MediaType.MEDIA_TYPE_AUDIO == mediaType) {
			return getDefaultAudioFormatList();
		} else if (MediaType.MEDIA_TYPE_VIDEO == mediaType) {
			return getDefaultVideoFormatList();
		} else {
			return getImageFormatList();
		}
	}

	public static List<String> getImageFormatList() {
		List<String> formats = new ArrayList<String>();
		formats.add(".jpg");
		formats.add(".jpeg");
		formats.add(".png");
		formats.add(".bmp");
		return formats;
	}

	public static List<String> getDefaultAudioFormatList() {
		List<String> formats = new ArrayList<String>();
		formats.add(".mp3");
		formats.add(".m4a");
		return formats;
	}

	public static List<String> getDefaultVideoFormatList() {
		List<String> formats = new ArrayList<String>();
		formats.add(".mp4");
		return formats;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View itemView = null;
		if (convertView != null) {
			itemView = convertView;
		} else {
			itemView = LayoutInflater.from(getActivity()).inflate(
					R.layout.resource_pic_grid_item, null);
		}
		int space = (int) (getResources().getDisplayMetrics().density * 10);
		int columns = mGridView.getNumColumns();
		int height = (mGridView.getWidth() - space * (columns - 1)) / columns;
		AbsListView.LayoutParams rlp = new AbsListView.LayoutParams(
				AbsListView.LayoutParams.FILL_PARENT, height);
		itemView.setLayoutParams(rlp);
		MediaInfo imageInfo = (MediaInfo) mDataHelper.getObject(position);
		ImageView image = (ImageView) itemView.findViewById(R.id.image);
		if (mParam.mMediaType == MediaType.MEDIA_TYPE_PHOTO) {
			String imgPath = (String) image.getTag();
			if (imgPath == null || !imgPath.equals(imageInfo.mPath)) {
				int defaultIcon = mParam.mDefaultImage > 0 ? mParam.mDefaultImage
						: R.drawable.default_photo;
				DisplayImageOptions options = LQImageLoader.getOpt(
						LQImageLoader.OUT_WIDTH, LQImageLoader.OUT_HEIGHT,
						defaultIcon);
				ImageLoader.getInstance().displayImage("file://" + imageInfo.mPath, image,
						options, mImageLoaderListener);
				image.setTag(imageInfo.mPath);
			}
		} else {
			if (mParam.mDefaultImage > 0) {
				image.setImageResource(mParam.mDefaultImage);
			}
			TextView name = (TextView) itemView.findViewById(R.id.name);
			name.setVisibility(View.VISIBLE);
			File file = new File(imageInfo.mPath);
			if (file.exists()) {
				name.setText(file.getName());
			}
		}
		ImageView flag = (ImageView) itemView.findViewById(R.id.flag);
		flag.setImageResource(imageInfo.mIsSelected ? R.drawable.select
				: R.drawable.unselect);

		return itemView;
	}

	private ImageLoadingListener mImageLoaderListener = new ImageLoadingListener() {
		@Override
		public void onLoadingStarted(String s, View view) {

		}

		@Override
		public void onLoadingFailed(String s, View view, FailReason failReason) {
			view.setTag(null);
		}

		@Override
		public void onLoadingComplete(String s, View view, Bitmap bitmap) {

		}

		@Override
		public void onLoadingCancelled(String s, View view) {

		}
	};
}
