package com.lqwawa.mooc.modle.tutorial.regist;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.galaxyschool.app.wawaschool.R;
import com.hyphenate.chat.adapter.EMABase;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author mrmedici
 * @desc 提供身份证护照选择PopupWindow
 */
public class IdentifyPopupWindow extends PopupWindow {

    private View mRootView;
    private ListView mListView;
    private OnChoiceListener listener;

    public IdentifyPopupWindow(int width,int height,@NonNull OnChoiceListener listener){
        this.listener = listener;
        init(width,height);
    }

    private void init(int width,int height){
        this.setOutsideTouchable(true);
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new ColorDrawable(UIUtil.getColor(android.R.color.transparent)));

        mRootView = UIUtil.inflate(R.layout.popup_identify_layout);
        mListView = (ListView) mRootView.findViewById(R.id.list_view);
        setContentView(mRootView);

        List<IdentifyEntity> entities = new ArrayList<>();
        entities.add(new IdentifyEntity(IDType.ID_TYPE_IDENTITY_CARD,UIUtil.getString(R.string.label_identify_card)));
        entities.add(new IdentifyEntity(IDType.ID_TYPE_PASSPORT,UIUtil.getString(R.string.label_identify_passport)));
        ArrayAdapter adapter = new ArrayAdapter(UIUtil.getContext(),R.layout.item_text_layout,R.id.tv_content,entities);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(EmptyUtil.isNotEmpty(listener)){
                    IdentifyEntity entity = (IdentifyEntity) adapter.getItem(position);
                    listener.onChoiceMenu(entity);
                    if(isShowing()){
                        dismiss();
                    }
                }
            }
        });
    }

    public interface OnChoiceListener{
        void onChoiceMenu(@NonNull IdentifyEntity entity);
    }

    public class IdentifyEntity{

        private int identifyId;
        private String identifyName;

        public IdentifyEntity(int identifyId, String identifyName) {
            this.identifyId = identifyId;
            this.identifyName = identifyName;
        }

        public int getIdentifyId() {
            return identifyId;
        }

        public void setIdentifyId(int identifyId) {
            this.identifyId = identifyId;
        }

        public String getIdentifyName() {
            return identifyName;
        }

        public void setIdentifyName(String identifyName) {
            this.identifyName = identifyName;
        }

        @Override
        public String toString() {
            return identifyName;
        }
    }
}
