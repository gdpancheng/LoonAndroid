package com.android.pc.ioc.invoker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.pc.ioc.util.ContextUtils;
import com.android.pc.ioc.util.InjectExcutor;
import com.android.pc.ioc.util.InjectViewUtils;

public class InjectLayers extends InjectInvoker {

	private int id;
	private boolean isFull;
	private boolean isTile;
	private InjectExcutor injectExcutor;
	private int parent;
	private InjectPLayers injectPLayers;

	public InjectLayers(int id, boolean isFull, boolean isTitle, int parent, InjectExcutor injectExcutor) {
		this.id = id;
		this.isFull = isFull;
		this.isTile = isTitle;
		this.parent = parent;
		this.injectExcutor = injectExcutor;
	}

	@SuppressWarnings("unchecked")
    @Override
	public void invoke(Object beanObject, Object... args) {
		if (id == -1) {
	        return;
        }
		if (parent==ContextUtils.ID_NONE) {
			injectExcutor.setContentView(beanObject, id);
			return;
		}
		if (injectPLayers!=null) {
			injectExcutor.setContentView(beanObject, injectPLayers.getId());
//			injectPLayers.invoke(beanObject, args);
			ViewGroup view = (ViewGroup) InjectViewUtils.Inject_Excutors[0].findViewById((Activity) beanObject, parent);
			LayoutInflater laInflater = LayoutInflater.from((Activity) beanObject);
			if (LinearLayout.class.isAssignableFrom(view.getClass())) {
				LinearLayout.LayoutParams layout =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
				view.addView(laInflater.inflate(id, null),layout);
            }
			if (RelativeLayout.class.isAssignableFrom(view.getClass())) {
				RelativeLayout.LayoutParams layout =new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
				view.addView(laInflater.inflate(id, null),layout);
			}
			if (AbsoluteLayout.class.isAssignableFrom(view.getClass())) {
				AbsoluteLayout.LayoutParams layout =new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams.FILL_PARENT, 0, 0);
				view.addView(laInflater.inflate(id, null),layout);
            }
			if (FrameLayout.class.isAssignableFrom(view.getClass())) {
				FrameLayout.LayoutParams layout =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
				view.addView(laInflater.inflate(id, null),layout);
            }
			
			try {
	            Class clazz = Class.forName("android.widget.GridLayout");
	            if (clazz.isAssignableFrom(view.getClass())) {
	            	GridLayout.LayoutParams layout =new GridLayout.LayoutParams();
	            	view.addView(laInflater.inflate(id, null),layout);
	            }
            } catch (Exception e) {
            }
        }
	}
	
	public int getParent() {
		return parent;
	}

	public boolean isFull() {
		return isFull;
	}

	public boolean isTile() {
		return isTile;
	}

	public InjectPLayers getInjectPLayers() {
		return injectPLayers;
	}

	public void setInjectPLayers(InjectPLayers injectPLayers) {
		this.injectPLayers = injectPLayers;
	}

	@Override
	public String toString() {
		return "InjectLayers [id=" + id + "]";
	}

}
