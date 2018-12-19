package com.lqwawa.libs.mediapaper.io;

import android.content.Context;
import com.lqwawa.libs.mediapaper.PaperManger;
import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Loader {
    private String mUsertitle = null;
//    private ArrayList<childViewData> mLoader_data = null;
    private PaperManger mPaperManger = null;
    Context mContext = null;
    private String mCacheFolderPath = null;

    public Loader(Context context, PaperManger paperManger) {
        mContext = context;
        mPaperManger = paperManger;
    }

//    public ArrayList<childViewData> getData() {
//        return mLoader_data;
//    }

    public String getUserTitle() {
        return mUsertitle;
    }

    public void onlinePaperSetCacheFolder(String cacheFolderPath) {
        mCacheFolderPath = cacheFolderPath;
    }


    public ArrayList<childViewData> loadDataFromIndexFile(String pathName) {
        if (pathName == null || pathName.equals("")) {
            return null;
        }
        if (!pathName.endsWith(File.separator)) {
            pathName = pathName + File.separator;
        }

        String xmlFilePath = pathName + Saver.XML_FILE_NAME;

        if (xmlFilePath != null && xmlFilePath.startsWith("http")) {
            File cacheIndexFile = new File(mCacheFolderPath, Saver.XML_FILE_NAME);
            if (cacheIndexFile.exists()) {
                cacheIndexFile.delete();
            }
            xmlFilePath = PaperUtils.getFile(xmlFilePath, cacheIndexFile.getPath());
        }

        if (xmlFilePath != null && new File(xmlFilePath).exists()) {
            ArrayList<childViewData> childDatas = parse_file(xmlFilePath);
            if (childDatas != null && childDatas.size() > 0) {
                    childViewData item;
                    for (int i = 0; i < childDatas.size(); i++) {
                        item = childDatas.get(i);
                        if (item != null) {
                            if (!item.mViewName.equals(PaperUtils.EDITVIEW)
                                    && (item.mViewData != null || item.mViewData2 != null)) {
                                if (item.mViewData != null) {
                                    if (!item.mViewData.startsWith("http")) {
                                        item.mViewData = pathName + item.mViewData;
                                    }
                                }
                                if (item.mViewData2 != null) {
                                    if (!item.mViewData2.startsWith("http")) {
                                        item.mViewData2 = pathName + item.mViewData2;
                                    }
                                }
                            }
                        }
                    }
            }
            return childDatas;
        }

        return null;
    }

    public boolean restoreChildFromChildDatas(String userTitle, ArrayList<childViewData> childDatas) {
        boolean rtn = false;
        if (userTitle != null) {
            mPaperManger.fillUserTitle(userTitle);
        }

        if (childDatas != null && childDatas.size() > 0) {
            childViewData item;
            rtn = true;
            boolean bUpdateMedia = false;
            for (int i = 0; i < childDatas.size(); i++) {
                item = childDatas.get(i);
                if (item != null) {
                    if (!item.mViewName.equals(PaperUtils.EDITVIEW)
                            && (item.mViewData != null || item.mViewData2 != null)) {
                        File file = new File(item.mViewData);
                        if (!item.mViewData.startsWith("http") && !file.exists() && item.mViewName.equals(PaperUtils.IMAGEVIEW)) {
                            continue;
                        }
                    }
                    mPaperManger.addView(mPaperManger.createNewView(
                            item.mViewName, item.mViewData, item.mViewData2, item.mViewData3, item.mViewData4, item.mViewData5,
                            item.mMarginTop, item.width, item.height, -1), PaperUtils.AddViewLoad, -1);

//                    if (item.mViewData2 != null) {
//                        mPaperManger.addView(mPaperManger.createNewView(
//                                item.mViewName, item.mViewData, item.mViewData2, item.mViewData3, item.mViewData4, item.mMarginTop, item.width, item.height, -1), PaperUtils.AddViewLoad, -1);
//                    } else {
//                        mPaperManger.addView(mPaperManger.createNewView(
//                                item.mViewName, item.mViewData, item.mMarginTop, item.width, item.height, -1), PaperUtils.AddViewLoad, -1);
//                    }
                }
            }
        }
        return rtn;
    }

    public boolean restoreChild(String pathName) {
        boolean rtn = false;
        ArrayList<childViewData> childDatas = loadDataFromIndexFile(pathName);
        if (mUsertitle != null || childDatas != null) {
            restoreChildFromChildDatas(mUsertitle, childDatas);
        }

//        if (mUsertitle != null) {
//            mPaperManger.fillUserTitle(mUsertitle);
//        }

//        if (mLoader_data != null) {
//            childViewData item;
//            rtn = true;
//            boolean bUpdateMedia = false;
//            for (int i = 0; i < mLoader_data.size(); i++) {
//                item = mLoader_data.get(i);
//                if (item != null) {
//                    if (!item.mViewName.equals(PaperUtils.EDITVIEW)
//                            && (item.mViewData != null || item.mViewData2 != null)) {
//                        if (item.mViewData2 != null && !item.mViewData2.startsWith("http")) {
//                            item.mViewData2 = pathName + item.mViewData2;
//                        }
//                        if (!item.mViewData.startsWith("http")) {
//                            item.mViewData = pathName + item.mViewData;
//                        }
//                        File file = new File(item.mViewData);
//                        if (!file.exists() &&
//                                (!item.mViewName.equals(PaperUtils.COURSEVIEW) && !item.mViewName.equals(PaperUtils.HWPAGEVIEW))) {
//                            if (item.mViewName.equals(PaperUtils.IMAGEVIEW)) {
//                                String str = item.mViewData;
//                                String path = str;
//                            }
//                            continue;
//                        }
//                    }
//                    if (item.mViewData2 != null) {
//                        mPaperManger.addView(mPaperManger.createNewView(
//                                item.mViewName, item.mViewData, item.mViewData2, item.mViewData3, item.mViewData4, item.mMarginTop, item.width, item.height, -1), PaperUtils.AddViewLoad, -1);
//                    } else {
//                        mPaperManger.addView(mPaperManger.createNewView(
//                                item.mViewName, item.mViewData, item.mMarginTop, item.width, item.height, -1), PaperUtils.AddViewLoad, -1);
//                    }
//                }
//            }
//            if (!bUpdateMedia) {
//
////            if(Utils.getDiaryDB(mContext) != null)
////               Utils.getDiaryDB(mContext).updateDiaryMedia(fileName, null, null);
//            }
//        }
        return rtn;
    }

    private ArrayList<childViewData> parse_file(String xmlFilePath) {
        String pathname = xmlFilePath;
        File file = new File(pathname);
        if (!(file.exists() && file.isFile()))
            return null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        SAXParserFactory spf = SAXParserFactory.newInstance();
        XmlReadHandler handler = null;
        try {
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            handler = new XmlReadHandler();
            xr.setContentHandler(handler);
            xr.parse(new InputSource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (handler != null) {
            mUsertitle = handler.getUserTitle();
            return handler.getdata();
        }

        return null;
    }

    // public interface OnAddChildListener {
    // void onAddChild(View child, LayoutParams lp);
    // }

}
