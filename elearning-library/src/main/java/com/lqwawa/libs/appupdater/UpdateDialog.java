package com.lqwawa.libs.appupdater;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lqwawa.apps.R;
import com.osastudio.apps.Config;
import com.osastudio.common.utils.Utils;

public class UpdateDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private View contentView;
    private OnClickListener ignoreClickListener;
    private OnClickListener confirmClickListener;
    private OnClickListener cancelClickListener;
    private AppInfo appInfo;

    public UpdateDialog(Context context, AppInfo appInfo) {
        this(context, R.style.au_theme_update_dialog, appInfo);
    }

    public UpdateDialog(Context context, int theme, AppInfo appInfo) {
        super(context, theme);
        this.context = context;
        this.appInfo = appInfo;

        this.contentView = LayoutInflater.from(context).inflate(R.layout.au_update_dialog, null);
        setContentView(this.contentView);

        initViews();
    }

    public UpdateDialog setOnIgnoreClickListener(OnClickListener listener) {
        ignoreClickListener = listener;
        return this;
    }

    public UpdateDialog setOnConfirmClickListener(OnClickListener listener) {
        confirmClickListener = listener;
        return this;
    }

    public UpdateDialog setOnCancelClickListener(OnClickListener listener) {
        cancelClickListener = listener;
        return this;
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.au_update_content);
        if (textView != null) {
            if (!Config.UPDATE_FOR_BUGLY) {

                textView.setText(generateUpdateContent());
            }
        }

        Button button = (Button) findViewById(R.id.au_update_ignore);
        if (button != null) {
            button.setOnClickListener(this);
        }
        button = (Button) findViewById(R.id.au_update_now);
        if (button != null) {
            button.setOnClickListener(this);
        }
        button = (Button) findViewById(R.id.au_update_cancel);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    public View getContentView() {
        return this.contentView;
    }

    public AppInfo getAppInfo() {
        return this.appInfo;
    }

    public Button getIgnoreButton() {
        return (Button) findViewById(R.id.au_update_ignore);
    }

    public Button getConfirmButton() {
        return (Button) findViewById(R.id.au_update_now);
    }

    public Button getCancelButton() {
        return (Button) findViewById(R.id.au_update_cancel);
    }

    public TextView getUpdateTextView() {
        return (TextView) findViewById(R.id.au_update_content);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.au_update_ignore) {
            dismiss();
            if (ignoreClickListener != null) {
                ignoreClickListener.onClick(UpdateDialog.this, v.getId());
            }
        } else if (v.getId() == R.id.au_update_now) {
            dismiss();
            if (confirmClickListener != null) {
                confirmClickListener.onClick(UpdateDialog.this, v.getId());
            }
        } else if (v.getId() == R.id.au_update_cancel) {
            dismiss();
            if (cancelClickListener != null) {
                cancelClickListener.onClick(UpdateDialog.this, v.getId());
            }
        }
    }

    private String generateUpdateContent() {
        return new StringBuilder()
                .append(this.context.getString(R.string.au_new_version))
                .append(this.appInfo.getVersionName()).append("\n")
                .append(this.context.getString(R.string.au_file_size))
                .append(Utils.formatFileSize(this.appInfo.getFileSize())).append("\n")
                .append(this.context.getString(R.string.au_release_notes))
                .append(this.appInfo.getDescription()).append("\n")
                .toString();
    }

}
