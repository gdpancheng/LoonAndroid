package com.android.pc.ioc.invoker;

import com.android.pc.ioc.util.InjectExcutor;

public class InjectPLayers extends InjectInvoker {

	private int id;
	private boolean isFull;
	private boolean isTile;
	private InjectExcutor injectExcutor;

	public InjectPLayers(int id, boolean isFull, boolean isTitle, InjectExcutor injectExcutor) {
		this.id = id;
		this.isFull = isFull;
		this.isTile = isTitle;
		this.injectExcutor = injectExcutor;
	}

	@Override
	public void invoke(Object beanObject, Object... args) {
		if (id != -1) {
			injectExcutor.setContentView(beanObject, id);
		}
	}

	public boolean isFull() {
		return isFull;
	}

	public boolean isTile() {
		return isTile;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Override
    public String toString() {
	    return "InjectPLayers [id=" + id + ", isFull=" + isFull + ", isTile=" + isTile + ", injectExcutor=" + injectExcutor + "]";
    }

}
