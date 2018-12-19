package com.oosictech.library.mediaprovider;

public class MediaProvider {

	int mNativeProvider;

	private final static String TAG = "MediaProvider";

	public MediaProvider() {
		mNativeProvider = native_creator();
	}

	public void setSearchDepth(int depth) {
		nativeSetSearchDepth(mNativeProvider, depth);
	}
	
	public void setFileMinSize(int size) {
		nativeSetFileMinSize(mNativeProvider, size);
	}	

	public void setAndroidFolderSkip(boolean enable) {
		nativeSetAndroidFolderSkip(mNativeProvider, enable);
	}

	public boolean addFileExtends(String fileExt) {
		return nativeAddFileExtends(mNativeProvider, fileExt);
	}

	public void scanFiles(String fileRoot) {
		nativeScanFiles(mNativeProvider, fileRoot);
	}

	public void scanFolders(String fileRoot) {
		nativeScanFolders(mNativeProvider, fileRoot);
	}

	public void scanAllFiles(String fileRoot) {
		nativeScanAllFiles(mNativeProvider, fileRoot);
	}

	public void setMediaListener(MediaListener l) {
		if (l == null) {
			nativeSetListener(mNativeProvider, 0);
		} else {
			nativeSetListener(mNativeProvider, l.m_native_client_if);
		}
	}

	public void cancel() {
		nativeCancel(mNativeProvider);
	}

	protected void finalize() throws Throwable {
		try {
			if (mNativeProvider != 0) {
				native_destructor(mNativeProvider);
				mNativeProvider = 0;
			}
		} finally {

		}
	}
	
	public void recycle() {
		try {
			if (mNativeProvider != 0) {
				native_destructor(mNativeProvider);
				mNativeProvider = 0;
			}
		} finally {
		}
	}

	static {
		try {
			System.loadLibrary("fscan_jni");
		} catch (UnsatisfiedLinkError ule) {
			//System.err.println("WARNING: Could not load libmediaprovider_jni.so");
		}
	}

	protected native int native_creator();

	protected native void native_destructor(int nativeProvider);

	protected native void nativeSetSearchDepth(int nativeProvider, int depth);
	
	protected native void nativeSetFileMinSize(int nativeProvider, int size);

	protected native void nativeSetAndroidFolderSkip(int nativeProvider,
			boolean enabeldepth);

	protected native boolean nativeAddFileExtends(int nativeProvider,
			String extent);

	protected native void nativeScanFiles(int nativeProvider, String path);

	protected native void nativeScanFolders(int nativeProvider, String path);

	protected native void nativeScanAllFiles(int nativeProvider, String path);

	protected native void nativeCancel(int nativeProvider);

	protected native void nativeSetListener(int nativeProvider,
			int nativeListener);
}
