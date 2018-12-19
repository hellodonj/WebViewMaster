package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

	public RoundedImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();

		if (drawable == null) {
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap b = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = b.copy(Config.ARGB_8888, true);

		int w = getWidth(), h = getHeight();

		Bitmap roundBitmap = getCroppedBitmap(bitmap, w, h);
		canvas.drawBitmap(roundBitmap, 0, 0, null);

	}

	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius, int height) {
		Bitmap sbmp;
		
		//center crop
		
		if (bmp.getWidth() >= bmp.getHeight()) {
			sbmp = Bitmap.createBitmap(bmp,
					bmp.getWidth() / 2 - bmp.getHeight() / 2, 0,
					bmp.getHeight(), bmp.getHeight());
		} else {
			sbmp = Bitmap.createBitmap(bmp, 0,
					bmp.getHeight() / 2 - bmp.getWidth() / 2, bmp.getWidth(),
					bmp.getWidth());
		}

		sbmp = Bitmap.createScaledBitmap(sbmp, radius, height, false);

		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(Color.parseColor("#BAB399"));
		
		canvas.drawRoundRect(
				new RectF(0, 0, sbmp.getWidth() + 0f, sbmp.getHeight() + 0f),
				10f, 10f, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);

		return output;
	}

}