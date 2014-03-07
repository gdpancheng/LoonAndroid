package com.android.pc.ioc.download;
/**
 * 版本更新通知栏实体类
 * @author gdpancheng@gmail.com 2014-3-2 下午10:44:21
 */
public class NotfiEntity {

	/**通知栏布局id**/
	private int layout_id;
	/**通知栏布局icon的id**/
	private int icon_id;
	/**通知栏进度条的id**/
	private int progress_id;
	/**通知栏进度条文字id**/
	private int progress_txt_id;
	/**完成或者失败以后点击通知栏跳转的页面**/
	private Class clazz;
	
	public int getLayout_id() {
		return layout_id;
	}
	/**通知栏布局id**/
	public void setLayout_id(int layout_id) {
		this.layout_id = layout_id;
	}
	public int getIcon_id() {
		return icon_id;
	}
	/**通知栏布局icon的id**/
	public void setIcon_id(int icon_id) {
		this.icon_id = icon_id;
	}
	public int getProgress_id() {
		return progress_id;
	}
	/**通知栏进度条的id**/
	public void setProgress_id(int progress_id) {
		this.progress_id = progress_id;
	}
	public int getProgress_txt_id() {
		return progress_txt_id;
	}
	/**通知栏进度条文字id**/
	public void setProgress_txt_id(int progress_txt_id) {
		this.progress_txt_id = progress_txt_id;
	}
	
	public Class getClazz() {
		return clazz;
	}
	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}
}
