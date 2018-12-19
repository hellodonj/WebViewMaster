package com.galaxyschool.app.wawaschool.common;

import android.text.TextUtils;

import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2018/03/31
 * @desc:
 */
public class ZipFileUtil {

    public static final int BUFFER_SIZE = 2048;

    /**
     * 解压 zip 文件
     *
     * @param zipFile zip 压缩文件
     * @param destDir zip 压缩文件解压后保存的目录
     * @return 返回 zip 压缩文件里的文件名的 list
     * @throws Exception
     */
    public static List<String> unZip(File zipFile, String destDir) throws
            Exception {
        // 如果 destDir 为 null, 空字符串, 或者全是空格, 则解压到压缩文件所在目录
        if (TextUtils.isEmpty(destDir)) {
            destDir = zipFile.getParent();
        }

        destDir = destDir.endsWith(File.separator) ? destDir : destDir + File.separator;
        ZipArchiveInputStream is = null;
        List<String> fileNames = new ArrayList<String>();

        try {
            is = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            ZipArchiveEntry entry = null;

            while ((entry = is.getNextZipEntry()) != null) {
                String name = entry.getName();
                int index = name.indexOf(File.separator);
                if (index > 0 && ++index < name.length()) {
                    name = name.substring(index);
                }
                fileNames.add(name);

                if (entry.isDirectory()) {
                    File directory = new File(destDir, name);
                    directory.mkdirs();
                } else {
                    File file = new File(destDir, name);
                    if (file != null) {
                        File parentFile = file.getParentFile();
                        if (!parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                    }
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(destDir, name)), BUFFER_SIZE);
                        IOUtils.copy(is, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }

        return fileNames;
    }

    /**
     * 解压 zip 文件
     *
     * @param zipfile zip 压缩文件的路径
     * @param destDir zip 压缩文件解压后保存的目录
     * @return 返回 zip 压缩文件里的文件名的 list
     * @throws Exception
     */
    public static List<String> unZip(String zipfile, String destDir) throws
            Exception {
        File zipFile = new File(zipfile);
        return unZip(zipFile, destDir);
    }

    /**
     *
     * @param files
     * @param zipFilePath
     */
    public static void zip(File[] files, String zipFilePath) {
        if (files != null && files.length > 0) {
            ZipArchiveOutputStream zaos = null;
            File f = new File(zipFilePath);
            if (f.isDirectory()) {
                return;
            }
            if (f.exists()) {
                f.delete();
            }
            try {
                File zipFile = new File(zipFilePath);
                zaos = new ZipArchiveOutputStream(zipFile);
                zaos.setUseZip64(Zip64Mode.AsNeeded);
                for (File file : files) {
                    if (file != null) {
                        ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(
                                file, new File(file.getParent()).getName() + File.separator + file.getName());
                        zaos.putArchiveEntry(zipArchiveEntry);
                        InputStream is = null;
                        try {
                            is = new BufferedInputStream(new FileInputStream(
                                    file));
                            byte[] buffer = new byte[1024 * 5];
                            int len = -1;
                            while ((len = is.read(buffer)) != -1) {
                                // 把缓冲区的字节写入到ZipArchiveEntry
                                zaos.write(buffer, 0, len);
                            }
                            zaos.closeArchiveEntry();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (is != null)
                                is.close();
                        }
                    }
                }
                zaos.finish();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (zaos != null) {
                        zaos.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     *
     * @param filePath 压缩的文件夹
     * @param zipfilePath 
     */
    public static void zip(String filePath, String zipfilePath) {
        File file = new File(filePath);
        zip(file.listFiles(), zipfilePath);
    }
}
