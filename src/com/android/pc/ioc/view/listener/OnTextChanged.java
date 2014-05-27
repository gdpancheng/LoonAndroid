/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-5-3 上午10:25:26
 */
package com.android.pc.ioc.view.listener;

import java.lang.reflect.Method;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.core.kernel.KernelClass;
import com.android.pc.ioc.core.kernel.KernelReflect;
import com.android.pc.ioc.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class OnTextChanged extends OnListener implements TextWatcher {

	/** beforeMethod */
	private String beforeMethod;

	/** beforeTargetMethod */
	private Method beforeTargetMethod;

	/** afterMethod */
	private String afterMethod;

	/** afterTargetMethod */
	private Method afterTargetMethod;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		if (beforeMethod != null) {
			beforeTargetMethod = KernelReflect.assignableMethod(target.getClass(), beforeMethod, 0, true, KernelClass.parameterTypes(new Object[] { s, start, count, after }));
			beforeMethod = null;
		}

		if (beforeTargetMethod != null) {
			KernelReflect.invoke(target, beforeTargetMethod, s, start, count, after);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if (afterMethod != null) {
			afterTargetMethod = KernelReflect.assignableMethod(target.getClass(), afterMethod, 0, true, KernelClass.parameterTypes(new Object[] { s }));
			afterMethod = null;
		}

		if (afterTargetMethod != null) {
			KernelReflect.invoke(target, afterTargetMethod, s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence, int, int, int)
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		invoke(s, start, before, count);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.android.view.listener.Listener#listener(android.view.View)
	 */
	@Override
	protected void listener(View view) {
		// TODO Auto-generated method stub
		if (view instanceof TextView) {
			String[] methods = method.split(",");
			if (methods.length > 1) {
				method = methods[0];
				beforeMethod = methods[1];
				if (methods.length > 2) {
					afterMethod = methods[2];
				}

			} else {
				beforeMethod = "none" + KernelString.uncapitalize(method);
				beforeMethod = "after" + KernelString.uncapitalize(method);
			}
			((TextView) view).addTextChangedListener(this);
		} else {
			Ioc.getIoc().getLogger().e(view.getClass() + " 无法设置OnTextChanged 请检查InjectMethod的参数\n");
		}
	}
}
