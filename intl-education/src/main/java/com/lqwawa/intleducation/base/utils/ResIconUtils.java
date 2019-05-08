package com.lqwawa.intleducation.base.utils;

import android.util.SparseArray;

import com.lqwawa.intleducation.R;

public class ResIconUtils {

    public static SparseArray<ResIcon> resIconSparseArray = new SparseArray<>();

    static {
        ResIcon resIcon = new ResIcon(R.drawable.ic_pic, R.drawable.ic_pic_read,
                R.drawable.ic_pic_shield, R.drawable.ic_pic_new);
        resIconSparseArray.put(1, resIcon);

        resIcon = new ResIcon(R.drawable.ic_audio, R.drawable.ic_audio_read,
                R.drawable.ic_audio_shield, R.drawable.ic_audio_new);
        resIconSparseArray.put(2, resIcon);

        resIcon = new ResIcon(R.drawable.ic_video, R.drawable.ic_video_read,
                R.drawable.ic_video_shield, R.drawable.ic_video_new);
        resIconSparseArray.put(3, resIcon);
        resIconSparseArray.put(30, resIcon);

        resIcon = new ResIcon(R.drawable.ic_pdf, R.drawable.ic_pdf_read,
                R.drawable.ic_pdf_shield, R.drawable.ic_pdf_new);
        resIconSparseArray.put(6, resIcon);

        resIcon = new ResIcon(R.drawable.ic_ppt, R.drawable.ic_ppt_read,
                R.drawable.ic_ppt_shield, R.drawable.ic_ppt_new);
        resIconSparseArray.put(20, resIcon);

        resIcon = new ResIcon(R.drawable.ic_word, R.drawable.ic_word_read,
                R.drawable.ic_word_shield, 0);
        resIconSparseArray.put(24, resIcon);

        resIcon = new ResIcon(R.drawable.ic_txt, R.drawable.ic_txt_read,
                R.drawable.ic_txt_shield, 0);
        resIconSparseArray.put(25, resIcon);

        resIcon = new ResIcon(R.drawable.ic_lqc, R.drawable.ic_lqc_read,
                R.drawable.ic_lqc_shield, R.drawable.ic_lqc_new);
        resIconSparseArray.put(5, resIcon);
        resIconSparseArray.put(16, resIcon);
        resIconSparseArray.put(17, resIcon);
        resIconSparseArray.put(18, resIcon);
        resIconSparseArray.put(19, resIcon);

        resIcon = new ResIcon(R.drawable.ic_task_not_flag, R.drawable.ic_task_read,
                R.drawable.ic_task_shield, R.drawable.ic_task_new);
        resIconSparseArray.put(23, resIcon);
    }


    public static class ResIcon {
        public int resId;
        public int readResId;
        public int shieldResId;
        public int newResId;

        public ResIcon(int resId, int readResId, int shieldResId, int newResId) {
            this.resId = resId;
            this.readResId = readResId;
            this.shieldResId = shieldResId;
            this.newResId = newResId;
        }
    }
}
