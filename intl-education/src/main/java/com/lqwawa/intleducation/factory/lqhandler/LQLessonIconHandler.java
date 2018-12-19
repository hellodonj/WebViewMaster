package com.lqwawa.intleducation.factory.lqhandler;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 听说课, 读写单 Icon显示
 * @date 2018/04/14 11:31
 * @history v1.0
 * **********************************
 */
public class LQLessonIconHandler {

    /**
     * 听说课,读写单详情批阅页面 HeaderLayout Icon的显示统一处理
     *
     * @param view 图片控件
     * @param vo   数据
     */
    public static void fillImage(@NonNull ImageView view, @NonNull SectionResListVo vo) {
        if (vo == null) {
            return;
        }
        if (vo.isIsShow()) {
            int resType = vo.getResType();
            if (resType > 10000) {
                resType -= 10000;
            }
            switch (resType) {
                case 1:
                    view.setImageResource(R.drawable.ic_pic);
                    break;
                case 2:
                    view.setImageResource(R.drawable.ic_audio);
                    break;
                case 3:
                    view.setImageResource(R.drawable.ic_video);
                    break;
                case 6:
                    view.setImageResource(R.drawable.ic_pdf);
                    break;
                case 20:
                    view.setImageResource(R.drawable.ic_ppt);
                    break;
                case 24:
                    view.setImageResource(R.drawable.ic_word);
                    break;
                case 25:
                    view.setImageResource(R.drawable.ic_txt);
                    break;
                case 5:
                case 16:
                case 17:
                case 18:
                case 19:
                    view.setImageResource(R.drawable.ic_lqc);
                    break;
                case 23:
                    view.setImageResource(R.drawable.ic_task_not_flag);
                    break;
                default:
                    break;

            }

        }
    }
}
