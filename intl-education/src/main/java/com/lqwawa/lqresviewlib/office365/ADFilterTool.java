package com.lqwawa.lqresviewlib.office365;

import android.content.Context;
import android.content.res.Resources;

import com.lqwawa.intleducation.R;


/**
 * Created by BrainWang on 05/01/2016.
 */
public class ADFilterTool {
    public static String getClearAdDivJs(Context context){
        String js = "javascript:";
        Resources res = context.getResources();
        String[] adDivs = res.getStringArray(R.array.adBlockDiv);
        for(int i=0;i<adDivs.length;i++){
            js += "var adDiv"+i+"= document.getElementById('"+adDivs[i]+"');if(adDiv"+i+" != null)adDiv"+i+".parentNode.removeChild(adDiv"+i+");";
            //js += "var adDiv"+i+"= document.getElementById('"+adDivs[i]+"');if(adDiv"+i+" != null)adDiv"+i+".style.display = 'none';";
        }
        return js;
    }
}