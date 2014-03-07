package com.android.pc.ioc.image.displayer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2014-2-11
 * Copyright @ 2014 BU
 * Description: 类描述
 *
 * History:
 */
public class FadeInAnimation implements DisplayerAnimation {
	@Override
	public void show(ImageView imageView, Bitmap bitmap) {
		final TransitionDrawable td = new TransitionDrawable(new Drawable[] { new ColorDrawable(Color.TRANSPARENT), new BitmapDrawable(imageView.getResources(), bitmap) });
		imageView.setImageDrawable(td);
		td.startTransition(800);
	}
}
