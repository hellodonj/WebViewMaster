package com.lqwawa.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.text.TextUtils;

import com.osastudio.common.utils.LogUtils;

/**
 * @author 作者 shouyi
 * @version 创建时间：Jan 26, 2016 4:01:36 PM 类说明
 */
public class FileZipHelper {

	public static void unzip(ZipUnzipParam param, ZipUnzipFileListener listener) {
		new UnzipThread(param, listener).start();
	}

	public static void zip(ZipUnzipParam param, ZipUnzipFileListener listener) {
		new ZipThread(param, listener).start();
	}

	private static class UnzipThread extends Thread {
		ZipUnzipParam mParam;
		ZipUnzipFileListener mListener;

		public UnzipThread(ZipUnzipParam param, ZipUnzipFileListener listener) {
			mParam = param;
			mListener = listener;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ZipUnzipResult result = new ZipUnzipResult();
			result.mParam = mParam;
			if (mParam != null && !TextUtils.isEmpty(mParam.mSrcPath)
					&& new File(mParam.mSrcPath).exists()
					&& !TextUtils.isEmpty(mParam.mOutputPath)) {
				try {
					unzipMyZip(mParam.mSrcPath, mParam.mOutputPath);
					result.mIsOk = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogUtils.loge("unzip Exception", e.getMessage());
				}
			}
			if (mListener != null) {
				mListener.onFinish(result);
			}
		}
	}

	private static class ZipThread extends Thread {
		ZipUnzipParam mParam;
		ZipUnzipFileListener mListener;

		public ZipThread(ZipUnzipParam param, ZipUnzipFileListener listener) {
			mParam = param;
			mListener = listener;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ZipUnzipResult result = new ZipUnzipResult();
			result.mParam = mParam;
			if (mParam != null && !TextUtils.isEmpty(mParam.mSrcPath)
					&& !TextUtils.isEmpty(mParam.mOutputPath)) {
				File srcFile = new File(mParam.mSrcPath);
				File outFile = new File(mParam.mOutputPath);
				try {
					if (srcFile.exists()) {
						zip(srcFile, outFile);
						result.mIsOk = true;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					LogUtils.loge("unzip Exception", e.getMessage());
				}
			}
			if (mListener != null) {
				mListener.onFinish(result);
			}
		}
	}

	public static class ZipUnzipParam {
		public ZipUnzipParam(String srcFile, String outputFile) {
			mSrcPath = srcFile;
			mOutputPath = outputFile;
		}

		public String mSrcPath = null;
		public String mOutputPath = null;
	}

	public static class ZipUnzipResult {
		public ZipUnzipParam mParam;
		public boolean mIsOk;
	}

	public interface ZipUnzipFileListener {
		public void onFinish(ZipUnzipResult result);
	}

	public static boolean zip(File input, File output) throws Exception {
		try {
			if (!output.getParentFile().exists()) {
				output.getParentFile().mkdirs();
			}
			File outputFile = output;
			FileOutputStream outputStream = new FileOutputStream(outputFile);
			ZipOutputStream outZip = new ZipOutputStream(outputStream);

			File inputfile = input;
			if (inputfile.exists()) {
				if (ZipFiles(inputfile.getParent(), inputfile.getName(), outZip) < 0) {
					outZip.finish();
					outZip.close();
					return false;
				} else {
					outZip.finish();
					outZip.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static int ZipFiles(String folderString, String fileString,
			ZipOutputStream zipOutputSteam) throws Exception {
		// SlideUtils.outLog("ZipFiles", folderString + "/" + fileString);
		if (zipOutputSteam == null)
			return -1;

		File file = new File(folderString, fileString);
		if (file.isFile()) {
			LogUtils.logi(null, "zipFile: " + fileString);
			ZipEntry zipEntry = new ZipEntry(fileString);
			FileInputStream inputStream = new FileInputStream(file);
			zipOutputSteam.putNextEntry(zipEntry);
			int len;
			byte[] buffer = new byte[4096];
			while ((len = inputStream.read(buffer)) != -1) {
				zipOutputSteam.write(buffer, 0, len);
			}
			zipOutputSteam.closeEntry();
		} else {
			String fileList[] = file.list();
			if (fileList.length <= 0) {
				ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
				zipOutputSteam.putNextEntry(zipEntry);
				zipOutputSteam.closeEntry();
			}
			for (int i = 0; i < fileList.length; i++) {
				ZipFiles(folderString, fileString + File.separator
						+ fileList[i], zipOutputSteam);
			}
		}

		return 0;
	}

	@Deprecated
	public static void unzip(String inputFilePath, String outputFilePath)
			throws Exception {
		if (TextUtils.isEmpty(inputFilePath)
				|| TextUtils.isEmpty(outputFilePath)) {
			return;
		}
		if (!outputFilePath.endsWith(File.separator)) {
			outputFilePath = outputFilePath + File.separator;
		}
		ZipInputStream in = new ZipInputStream(new FileInputStream(
				inputFilePath));
		ZipEntry z;
		String name = "";
		int counter = 0;

		while ((z = in.getNextEntry()) != null) {
			name = z.getName();

			if (name.contains("\\\\")) {
				name = name.replace("\\\\", "/");
			}
			if (name.contains("\\")) {
				name = name.replace("\\", "/");
			}
			int index = name.indexOf(File.separator);
			if (index > 0 && ++index < name.length()) {
				name = name.substring(index);
			}
			if (z.isDirectory()) {

				name = name.substring(0, name.length() - 1);

				File folder = new File(outputFilePath + name);
				folder.mkdir();
				if (counter == 0) {
				}
				counter++;
			} else {
				File file = new File(outputFilePath + name);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				// get the output stream of the file
				FileOutputStream out = new FileOutputStream(file);
				int ch;
				byte[] buffer = new byte[1024];
				// read (ch) bytes into buffer
				while ((ch = in.read(buffer)) > 0) {
					// write (ch) byte from buffer at the position 0
					out.write(buffer, 0, ch);
					out.flush();
				}
				out.close();
			}
		}

		in.close();
	}
	
	public static void unzipMyZip(String zipFileName,String directoryToExtractTo) {
        Enumeration entriesEnum;
        ZipFile zipFile;
        File file;
        try {
            zipFile = new ZipFile(zipFileName);
            entriesEnum = zipFile.entries();

            File directory = new File(directoryToExtractTo);
            if (!directory.exists()) {
                new File(directoryToExtractTo).mkdir();
            }
            while (entriesEnum.hasMoreElements()) {
                try {
                    ZipEntry entry = (ZipEntry) entriesEnum.nextElement();
                    file = new File(entry.getName());
                    if (entry.isDirectory()) {
                        file.mkdir();
                    } else {
                        int index = 0;
                        String fileName = entry.getName();
                        //window系统下单反斜杠替换成斜杠
                        fileName = fileName.replaceAll("\\\\","/");
                        fileName = fileName.replace("\\", "/");

                        index = fileName.lastIndexOf("/");
                        if (index < 0 && index != fileName.length()) {
                            fileName = entry.getName().substring(index + 1);
                        }
                        else {
                            String tempPath = directoryToExtractTo + fileName.substring(0, index);
                            File tempDirectory = new File(tempPath);
                            if (!tempDirectory.exists()) {
                                tempDirectory.mkdirs();
                            }
                        }



                        writeFile(zipFile.getInputStream(entry),
                                new BufferedOutputStream(new FileOutputStream(
                                        directoryToExtractTo + fileName))
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Some Exception Occurred:");
            ioe.printStackTrace();
            return;
        }
    }
	
	public static final void writeFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        while ((len = in.read(buffer)) > 0)
        out.write(buffer, 0, len);

        in.close();
        out.close();
    }

	static public void unzip(InputStream inputStream, String outputFilePath)
			throws Exception {
		if (!outputFilePath.endsWith(File.separator)) {
			outputFilePath = outputFilePath + File.separator;
		}
		ZipInputStream in = new ZipInputStream(inputStream);
		ZipEntry z;
		String name = "";
		int counter = 0;

		while ((z = in.getNextEntry()) != null) {
			name = z.getName();
			int index = name.indexOf(File.separator);
			if (index > 0 && ++index < name.length()) {
				name = name.substring(index);
			}
			if (z.isDirectory()) {
				int separatorIndex = name.indexOf(File.separator);
				if (separatorIndex >= 0 && separatorIndex < name.length() - 1) {
					name = name.substring(0, name.length() - 1);

					File folder = new File(outputFilePath + name);
					folder.mkdir();
					if (counter == 0) {
					}
					counter++;
				}
			} else {
				File file = new File(outputFilePath + name);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				// get the output stream of the file
				FileOutputStream out = new FileOutputStream(file);
				int ch;
				byte[] buffer = new byte[1024];
				// read (ch) bytes into buffer
				while ((ch = in.read(buffer)) > 0) {
					// write (ch) byte from buffer at the position 0
					out.write(buffer, 0, ch);
					out.flush();
				}
				out.close();
			}
		}

		in.close();
	}
}
