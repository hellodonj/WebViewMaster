package com.lqwawa.libs.mediapaper.io;

import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class XmlReadHandler extends DefaultHandler {
    private String mUserTitle = null;
    private ArrayList<childViewData> mLoader_data = null;
    private childViewData mitem = null;
    private StringBuilder mBuilder = null;
    private boolean mbInUserTitle = false;
    private boolean mbInEditText = false;
//    private boolean mbInRecord = false;
//    private boolean mbInFilename = false;
//
//    private boolean mbIn_Data = false;

    public ArrayList<childViewData> getdata() {
        return mLoader_data;
    }

    public String getUserTitle() {
        return mUserTitle;
    }

    @Override
    public void startDocument() throws SAXException {
//         Log.v("pp", "startDocument");
    }

    @Override
    public void endDocument() throws SAXException {
//         Log.v("pp", "endDocument()");
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts) throws SAXException {
        String attsData = null;
        if (localName.equals("h")) {
            mbInUserTitle = true;
            mBuilder = new StringBuilder();
        } else if (localName.equals("p")) {
            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            mitem.mViewName = PaperUtils.EDITVIEW;
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            mbInEditText = true;
            mBuilder = new StringBuilder();
        } else if (localName.equals("br")) {
            if (mBuilder != null && (mbInEditText || mbInUserTitle)) {
                mBuilder.append("\n");
                if (mitem != null) {
                    mitem.mViewData = mBuilder.toString();
                }
            }
        } else if (localName.equals("img")) {

            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            mitem.mViewName = PaperUtils.IMAGEVIEW;
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            attsData = atts.getValue("src");
            mitem.mViewData = attsData;
            mLoader_data.add(mitem);
            mitem = null;
        } else if (localName.equals("audio")) {

            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            mitem.mViewName = PaperUtils.RECORDVIEW;
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            if (PaperUtils.RECORDVIEW.equals(mitem.mViewName)) {
                attsData = atts.getValue("src");
                mitem.mViewData = attsData;
            }
            mLoader_data.add(mitem);
            mitem = null;
        } else if (localName.equals("video")) {

            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            mitem.mViewName = PaperUtils.VIDEOVIEW;
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            attsData = atts.getValue("src");
            mitem.mViewData = attsData;
            attsData = atts.getValue("poster");
            mitem.mViewData2 = attsData;
            mLoader_data.add(mitem);
            mitem = null;
        } else if (localName.equals("cmc") || localName.equals("cmc2")) {

            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            if (localName.equals("cmc")) {
                mitem.mViewName = PaperUtils.COURSEVIEW;
            } else {
                mitem.mViewName = PaperUtils.COURSEVIEW2;
            }
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            attsData = atts.getValue("src");
            mitem.mViewData = attsData;
            attsData = atts.getValue("poster");
            mitem.mViewData2 = attsData;
            attsData = atts.getValue("cmctitle");
            mitem.mViewData3 = attsData;
            attsData = atts.getValue("webplayurl");
            mitem.mViewData4 = attsData;
            attsData = atts.getValue("orientation");
            if (attsData != null) {
                mitem.mViewData5 = Integer.parseInt(attsData);
            }
            mLoader_data.add(mitem);
            mitem = null;
        } else if (localName.equals("chw")) {

            if (mLoader_data == null) {
                mLoader_data = new ArrayList<childViewData>();
            }
            mitem = new childViewData();
            mitem.mViewName = PaperUtils.HWPAGEVIEW;
            attsData = atts.getValue("id");
            if (attsData != null) {
                mitem.mViewId = Integer.parseInt(attsData);
            }
            attsData = atts.getValue("src");
            mitem.mViewData = attsData;
            attsData = atts.getValue("poster");
            mitem.mViewData2 = attsData;
            attsData = atts.getValue("chwtitle");
            mitem.mViewData3 = attsData;
            attsData = atts.getValue("webplayurl");
            mitem.mViewData4 = attsData;
            mLoader_data.add(mitem);
            mitem = null;
        }
    }


    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (localName.equals("h")) {
            if (mbInUserTitle) {
                mbInUserTitle = false;
                mBuilder = null;
            }
        } else if (localName.equals("p")) {
            if (mbInEditText) {
                mbInEditText = false;
                mBuilder = null;
            }
            mLoader_data.add(mitem);
            mitem = null;
        }

    }


    @Override
    public void characters(char ch[], int start, int length) {
        if (mBuilder != null) {
            mBuilder.append(ch, start, length);
            if (mbInUserTitle) {
                mUserTitle = mBuilder.toString();
            } else if (mitem != null && mbInEditText) {
                mitem.mViewData = mBuilder.toString();//.trim();
            }
        }
    }

}
