package com.android.pc.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

/*
 * Author: pan Email:gdpancheng@gmail.com
 * Created Date:2013-10-17
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class Handler_TextStyle {

	private SpannableString spannableString;

	public Handler_TextStyle() {
	}
	
	public Handler_TextStyle(String str) {
		spannableString = new SpannableString(str);
	}
	
	public void setString(String str) {
		spannableString = new SpannableString(str);
	}
	
	public SpannableString getSpannableString() {
		return spannableString;
	}

	public Handler_TextStyle setAbsoluteSize(int size,int start,int end,boolean dp) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new AbsoluteSizeSpan(size, dp), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
		return this;
	}

	public Handler_TextStyle setRelativeSize(float size,int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new RelativeSizeSpan(size), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 0.5f表示默认字体大小的一半
		return this;
	}
	
	public Handler_TextStyle setForegroundColor(int Color,int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new ForegroundColorSpan(Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
	
	
	public Handler_TextStyle setBackgroundColor(int Color,int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new BackgroundColorSpan(Color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
	
	public static void setFakeBold(TextView textView,boolean isBold) {
		TextPaint tp = textView.getPaint();
		tp.setFakeBoldText(isBold);
	}
	
	public Handler_TextStyle setUnderlineSpan(int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
	
	public Handler_TextStyle setStrikethroughSpan(int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
	public Handler_TextStyle setSubscriptSpan(int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new SubscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
	
	public Handler_TextStyle setSuperscriptSpan(int start,int end) {
		if (spannableString == null) {
			return this;
		}
		spannableString.setSpan(new SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // 设置前景色为洋红色
		return this;
	}
}
