package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.galaxyschool.app.wawaschool.fragment.CommentStatisticFragment;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;


/**
 * Created by Administrator on 2016.06.17.
 */
public class CommentStatisticActivity extends BaseFragmentActivity {
    private CommentStatisticFragment fragment = null;

    public static void start(Activity activity,
                             SubscribeClassInfo classInfo){
        if (activity == null || classInfo == null){
            return;
        }
        Intent intent = new Intent(activity,CommentStatisticActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(SubscribeClassInfo.class.getSimpleName(),classInfo);
        intent.putExtras(args);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new CommentStatisticFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, CommentStatisticFragment.TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
