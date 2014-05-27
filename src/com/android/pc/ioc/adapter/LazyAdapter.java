package com.android.pc.ioc.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.ioc.image.ImageDownloader;
import com.android.pc.ioc.image.Utils;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.util.ContextUtils;
import com.android.pc.ioc.view.listener.OnListener;

/**
 * <h1>全自动化适配器</h1>
 * T1为适配器的数据集合类型<br>
 * T2为适配器的ViewHolder<br>
 * 无需手动去绑定 适用于不太复杂的适配器
 * @author gdpancheng@gmail.com 2014-5-20 上午12:18:05
 */
public class LazyAdapter<T1, T2> extends BaseAdapter {

	private ArrayList<T1> dataList;
	private int layout_id = -1;
	private LayoutInflater layoutInflater;
	private Constructor<?> constructor;
	private Class<?> clazz;
	private String packageName;
	private Context context;
	ImageDownloader imageDownloader = null;

	@SuppressWarnings("unchecked")
	public LazyAdapter(ListView listView, ArrayList<T1> dataList, int layout_id) {
		this.dataList = dataList;
		this.layout_id = layout_id;
		this.context = listView.getContext();
		this.layoutInflater = LayoutInflater.from(context);
		this.packageName = Ioc.getIoc().getApplication().getPackageName();
		imageDownloader = new ImageDownloader(context, 0);
		listView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					if (!Utils.hasHoneycomb()) {
						imageDownloader.setPauseWork(true);
					}
				} else {
					imageDownloader.setPauseWork(false);
				}
			}
			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		Type[] types = parameterizedType.getActualTypeArguments();
		try {
			Constructor<?>[] constructors = ((Class<T2>) types[1]).getConstructors();
			if (constructors.length > 0) {
				constructor = constructors[0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ImageDownloader getImageDownloader() {
		return imageDownloader;
	}

	public void setImageDownloader(ImageDownloader imageDownloader) {
		this.imageDownloader = imageDownloader;
	}
	
	@Override
	public int getCount() {
		clazz = dataList.size() > 0 ? dataList.get(0).getClass() : null;
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		try {
			Object viewHold = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(layout_id, null);
				try {
					viewHold = constructor.newInstance(this);
				} catch (Exception e) {
					viewHold = constructor.newInstance(context);
				}
				// -------------------------------------------------------
				setView(viewHold, convertView, position);
				// 缓存绑定
				convertView.setTag(viewHold);
			} else {
				viewHold = convertView.getTag();
			}
			// -------------------------------------------------------
			// 绑定数据
			deal(dataList.get(position), (T2) viewHold, position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	public void deal(T1 data, T2 viewHold, int position) {
		injectAdapter(viewHold, position);
	};

	/**
	 * adapter里面使用 自动去注入组件
	 * @author gdpancheng@gmail.com 2013-10-22 下午12:59:07
	 * @param object
	 * @param view
	 * @return void
	 */
	public void injectAdapter(Object view, int position) {
		String data;
		Field[] fields = view.getClass().getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				InjectView v = field.getAnnotation(InjectView.class);
				if (v == null) {
					continue;
				}
				data = getString(position, field.getName());
				if (data == null) {
					continue;
				}
				try {
					field.setAccessible(true);
					Object object = field.get(view);
					if (object == null || !View.class.isAssignableFrom(object.getClass())) {
						continue;
					}
					((View)object).setTag(position);
					if (TextView.class.isAssignableFrom(object.getClass())) {
						((TextView) object).setText(data);
					}
					if (ImageView.class.isAssignableFrom(object.getClass())) {
						download(((ImageView) object), data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setView(Object viewHold, View view, int postion) {
		try {
			Field[] fields = viewHold.getClass().getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field : fields) {
					field.setAccessible(true);
					InjectView vs = field.getAnnotation(InjectView.class);
					if (vs == null) {
						continue;
					}
					View v;
					if (vs.value()!=ContextUtils.ID_NONE) {
						v = view.findViewById(vs.value());
                    }else {
                    	v = view.findViewById(Ioc.getIoc().getApplication().getResources().getIdentifier(field.getName(), "id", this.packageName));
					}
					if (v == null) {
						Ioc.getIoc().getLogger().e("变量  "+field+"  无法赋值，请检查ID和NAME");
	                    continue;
                    }
					try {
						if (View.class.isAssignableFrom(v.getClass())) {
							field.set(viewHold, v);
						} 
					} catch (Exception e) {
						e.printStackTrace();
					}
					InjectBinder[] binders = vs.binders();
					if (binders == null) {
						continue;
					}
					for (int i = 0; i < binders.length; i++) {
						InjectBinder injectBinder = binders[i];
						Class<? extends OnListener>[] clazzes = injectBinder.listeners();
						for (Class<? extends OnListener> clazz : clazzes) {
							try {
								OnListener listener = clazz.newInstance();
								listener.listener(v, viewHold, injectBinder.method());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 异步图片下载接口
	 * @author gdpancheng@gmail.com 2012-12-3 下午4:25:58
	 */
	public void download(ImageView view, String url) {
		imageDownloader.loadImage(url, view);
	};

	private String getString(int postion, String name) {
		Object object = dataList.get(postion);
		if (Map.class.isAssignableFrom(clazz)) {
			return ((Map<?, ?>) object).containsKey(name) ? ((Map<?, ?>) object).get(name).toString() : "";
		} else {
			try {
				Method readName = clazz.getDeclaredMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
				return readName.invoke(object).toString();
			} catch (Exception e) {
				return null;
			}
		}
	}
}
