package com.android.pc.ioc.invoker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
			view.addView(laInflater.inflate(id, null));
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
