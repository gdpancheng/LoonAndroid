package com.android.pc.ioc.a.demo;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.ioc.a.demo.MyAdapter.ViewHolder;
import com.android.pc.ioc.adapter.LazyAdapter;
import com.android.pc.ioc.app.ApplicationBean;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;

/**
 * 懒人适配器demo
 * @author gdpancheng@gmail.com 2014-5-20 下午3:14:00
 */
public class MyAdapter extends LazyAdapter<HashMap<String, String>, ViewHolder> {

	/**
	 * 必须调用父类的super
	 * @param context
	 * @param dataList
	 * @param layout_id
	 */
	public MyAdapter(ListView view, ArrayList<HashMap<String, String>> dataList, int layout_id) {
	    super(view, dataList, layout_id);
    }
	
	/**
	 * 如果比较复杂的 则需要重写 这里实现的是类似getview的逻辑
	 * 但是记得不要调用super.deal(data, viewHold, position);
	 * 这里是为了能够显示所以调用
	 */
	@Override
	public void deal(HashMap<String, String> data, ViewHolder viewHold, int position) {
		super.deal(data, viewHold, position);
		System.out.println("getview的实现逻辑");
	}
	
	/**
	 * 这里实现的是图片下载 如果不重写则使用框架中的图片下载
	 */
	@Override
	public void download(ImageView view, String url) {
		super.download(view, url);
		System.out.println("图片下载的实现逻辑");
	}
	
	/**
	 * 这里是ViewHolder 其中支持@InjectView 和 @InjectView(int)注解
	 * 其中属性名称对应其在LazyAdapter<T1,T2>的T1中的属性key
	 * 例子 1
	 * 如果T1为HashMap<String, String> 则 属性名 image对应的链接在HashMap中的key为image
	 * 例子 2
	 * 如果T2 为实体类 则属性名 image 必须为实体类中的一个属性 而且必须有get方法
	 * @author gdpancheng@gmail.com 2014-5-20 下午3:11:49
	 */
	public class ViewHolder {
		@InjectView
		public TextView text1,text2,text3,text4,text5;
		@InjectView(binders={@InjectBinder(method="click",listeners = {OnClick.class})})
		public ImageView image;
		
		private void click(View v){
			Toast.makeText(ApplicationBean.getApplication(), "点击"+v.getTag(), Toast.LENGTH_LONG).show();
		}
	}
}
