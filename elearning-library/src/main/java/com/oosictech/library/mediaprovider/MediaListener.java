package com.oosictech.library.mediaprovider;

public class MediaListener {
	int m_native_client_if;

	public MediaListener() {
		m_native_client_if = native_creator();
	}

	protected void finalize() throws Throwable {
		try {
			native_destructor(m_native_client_if);
			m_native_client_if = 0;
		} finally {
		}
	}

	public void onFolderFound(String path, int count) {

	}

	public void onFileFound(String path) {

	}
	
	public void recycle() {
		try {
			native_destructor(m_native_client_if);
			m_native_client_if = 0;
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

	// !< -------- Private functions --------
	protected native int native_creator();

	protected native void native_destructor(int nativeCallback);
}
