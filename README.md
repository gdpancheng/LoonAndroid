

框架的说明
===================================
		如果你想看ui方面的东西，这里没有，想要看牛逼的效果这里也没有。这只是纯实现功能的框架，它的目标是节省代码量，降低耦合，让代码层次看起来更清晰。整个框架一部分是网上的，一部分是我改的，为了适应我的编码习惯，还有一部分像orm完全是网上的组件。在此感谢那些朋友们。
		整个框架式的初衷是为了偷懒，之前都是一个功能一个jar，做项目的时候拉进去，这样对于我来说依然还是比较麻烦。最后就导致我把所有的jar做成了一个工具集合包。
		有很多框架都含有这个工具集合里的功能，这些不一定都好用，因为这是根据我个人使用喜欢来实现的，如果你们有自己的想法，可以自己把架包解压了以后，源码拉出来改动下。
		目前很多框架都用到了注解，除了androidannotations没有入侵我们应用的代码以外，其他的基本上都有，要么是必须继承框架里面的activity,要么是必须在activity的oncreat里面调用某个方法。
		整个框架式不同于androidannotations，Roboguice等ioc框架，这是一个类似spring的实现方式。在整应用的生命周期中找到切入点，然后对activity的生命周期进行拦截，然后插入自己的功能。

框架的主要功能
-----------------------------------
其中分为以下几种：
* 1自动注入框架（只需要继承框架内的application既可）
* 2图片加载框架（多重缓存，自动回收，最大限度保证内存的安全性）
* 3网络请求模块（继承了基本上现在所有的http请求）
* 4 eventbus（集成一个开源的框架）
* 5验证框架（集成开源框架）
* 6 json解析（支持解析成集合或者对象）
* 7 数据库（不知道是哪位写的 忘记了）
* 8 多线程断点下载（自动判断是否支持多线程，判断是否是重定向）
* 9 自动更新模块
* 10 一系列工具类

一 自动注入框架
-----------------------------------
### 1 无需继承任何BaseActivity

举例：普通activity

		public class FourActivity extends Activity {
		　　
			View xx;
		
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main4);
				xx = find......;
				//---------------------------------------------------------
				组件的初始化
				//---------------------------------------------------------
			}
		}
这其中我们会耗费大量的代码或者重复性的去些一些代码。特别是布局比较复杂的情况下。

如果用框架
		@InjectLayer(R.layout.activity_main3)
		public class ThirdActivity extends Activity {
			@InjectView
			View xx;
		}
即可
　　
像软件的说明页面，就是单纯的展示一个布局，那么就是
		@InjectLayer(R.layout.activity_main3)
		public class ThirdActivity extends Activity {
		}
即可
　　
整个ioc框架不需要你继承任何的acitivity，这样就保证了不会在你的代码结构层次上造成影响，因为有的时候你需要自己的BaseActivity来实现你公用的功能。
　　
### 2 支持子父布局
　　
　　如下图
	
这种情况下，对于一般的框架来说，做法有以下几种：
* 1 ActivityGroup  一般的ioc框架都需要继承框架内的activity，activitygroup会让很多框架用不了，现在ActivityGroup也是不提倡的了。
* 2 BaseActivity 一般的Ioc框架会需要你的BaseActivity 去继承框架内的activity
* 3 中间用fragment 这样的情况也一样，你的FragmentActivity必须继承它的activity才能实现ioc框架功能。
对于这个框架来说很容易实现
* 1 ActivityGroup  你不需要继承任何activity 和普通activity 实现方式（如上面的例子）
* 2 BaseActivity 
见代码：
　　首先是BaseActivity
　　
		@InjectPLayer(R.layout.activity_com)
		public class BaseActivity extends Activity {}
其中R.layout.activity_com是包括上下导航的布局，中间是一个view子activity只需要这么写即可
		@InjectLayer(value = R.layout.activity_main, parent = R.id.common)
		public class MainActivity extends BaseActivity {}
当然 又会有问题了，那么我上下导航里面的点击事件怎么绑定，怎么去初始化，难道要每一个子activity都要去写吗？
当然不需要
		@InjectPLayer(R.layout.activity_com)
		public class BaseActivity extends Activity {
		@InjectInit
		private void init() {
			MeApplication.logger.s("公共类的初始化");
		}

	// 这里是第一种交互事件注入方式（单击）
		@InjectMethod(@InjectListener(ids = { R.id.top, R.id.bottom }, listeners = { OnClick.class }))
		private void click2(View view) {
			Handler_TextStyle handler_TextStyle = new Handler_TextStyle();
			switch (view.getId()) {
				case R.id.top:
					handler_TextStyle.setString("点击了顶部按钮(在基类中统一注册,也可以单独注册)");
					handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
					Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
					break;
				case R.id.bottom:
					handler_TextStyle.setString("点击了底部按钮(在基类中统一注册,也可以单独注册)");
					handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
					Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
				break;
				}
			}
		}
如上 其中@InjectInit注解表示不管是在子activity还是父activity 都是在布局初始化完成以后才会调用，其先后顺序是
		父布局layout->子布局layout->父布局ioc和事件绑定->子布局事件绑定。
父activity 中可以对所有的公用组件和事件进行初始化和绑定
还没完，又会有另一个问题，如果我某个页面下导航的a按钮和其他页面底部a按钮的功能不一样 要单独设置怎么办。
那么我们可以在子布局进行@InjectMethod和@InjectView进行事件绑定和组件注入，它们会覆盖父类中相同id的组件的操作
以下是view注入的方法说明：
### @InjectPLayer 
表示是Activity的setContentView
		@InjectLayer(value = R.layout.activity_main2, parent = R.id.common, isFull = true, isTitle = true)
其中需要哪个参数就用哪个，value 是必须的 如果只有layout可以这么写@InjectPLayer(R.layout.activity_com)。其中value 表示layout,parent表示它在父布局中所对应组件的id 如上图中 中间显示区域的view的id。Isfull是否全屏，默认为false.isTitle 是否有标题，默认false;
### @InjectView
自动注入view注解。
基本写法：
		@InjectView
		TextView test;
其中test表示它在xml中对应的Id为test
		@InjectView(R.id.next2)
		TextView test;
表示它在xml中对应的Id为next2
高级写法：
		@InjectView(binders = { @InjectBinder(method = "click", listeners = { OnClick.class, OnLongClick.class }) })
		Button next, next3, next4;
其中表示对id为next，next3，next4进行注解，其中binders 表示绑定了以下事件，binders 是个数组，也就是说可以用多个InjectBinder绑定多个事件，也可以用listeners = { OnClick.class, OnLongClick.class }来表示对组件注入了点击事件和长按事件
		@InjectView(value = R.id.next2, binders = { @InjectBinder(method = "click", listeners = { OnClick.class }) })
		Button button;
对于变量名和组件id不一致的view则需要设置value
Click 表示那些注入的事件触发以后所调用的方法，其必须在当前类内。
// 支持由参数和无参数 即click(View view)或者click() 当然click名字必须对于变量注解中的method = "click"
		private void click(View view) {
			switch (view.getId()) {
				case R.id.next:
				startActivity(new Intent(this, ThirdActivity.class));
				break;
				...
			}
		}
### @InjectResource
		@InjectResource
		String action_settings;

		@InjectResource
		Drawable ic_launcher;
InjectResource支持string和drawable的注解
### @InjectMethod
// 底部导航栏 子类覆盖父类
		@InjectMethod(@InjectListener(ids = { R.id.bottom }, listeners = { OnClick.class, OnLongClick.class }))
		private void click3(View view) {
			Handler_TextStyle handler_TextStyle = new Handler_TextStyle();
			handler_TextStyle.setString("点击了底部按钮 子类覆盖了父类");
			handler_TextStyle.setBackgroundColor(Color.RED, 3, 5);
			Toast.makeText(this, handler_TextStyle.getSpannableString(), Toast.LENGTH_LONG).show();
		}
@InjectMethod是当我们对一个组件只需要触发而不需要find出来的时候用到。
ids 表示绑定哪些id，listeners 表示绑定哪些事件 这两个参数都是数组


当然 如果嫌注解字段太长，可以自己修改。这个是整个view的注入。

### @InjectInit
		@InjectInit
		void init() {
			MeApplication.logger.s("子类的初始化");
			test.setText("初始化完成，第一个页面");
		}

这个注解你在activity中添加到任何一个方法名上，那么，当所有的layout和所有的view以及事件绑定完毕以后，会第一个调用含有这个注解的方法。它相当于oncreat

		注意：框架注解了整个activity的生命周期， @InjectOnNewIntent，@InjectPause，@InjectResume，
		@InjectRestart，@InjectStart，@InjectStop 其中OnDestroy无注解。
		如果Activity中有含有这些注解的方法 那么不同生命周期下回自动调用这些方法

二：Fragment的自动注入
-----------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			this.inflater = inflater;
			View rootView = inflater.inflate(R.layout.activity_left, container, false);
			Handler_Inject.injectView(this, rootView);
			return rootView;
		}

		只需要在onCreateView里面调用Handler_Inject.injectView(this, rootView);
		即可


		在fragment中除了activity的生命周期注解和@InjectLayer注解无法使用外，组件绑定和事件绑定都可以使用，@InjectBefore也可以使用

		@InjectBefore 是在组件初始化之前调用

三：图片下载框架
-----------------------------------
		这个是图片框架重写了好多次了，总是有点问题，里面基本上每一段代码都有注释，还有一些bug，
		因为项目中用的还是这次重写之前的。
		如果大家发现问题，记得告诉我框架中调用的方法名和参数基本上都不会变，避免替换jar导致需要改动大部分代码。

整个图片下载的逻辑是这样的：

* 1 根据url和view去调用图片下载的方法
* 2 从缓存去拿bitmap
* 3 如果bitmap不为空 判断是否针对这个url有单独的配置 没有则使用全局配置加载图片
* 4 如果bitmap为空 则开启线程，放到本地线程池中，然后从本地文件读取
* 5 如果文件存在，则转为bitmap放到缓存，然后重复2，然后9
* 6 如果文件不存在，则开启线程放到网络线程池中去下载文件
* 7 下载成功则放到本地sdcard 然后把文件转为bitmap放到缓存，然后重复2，然后9
* 8 下载不成功，然后重复2，然后9
* 9 如果bitmap不为空 判断是否针对这个url有单独的配置 没有则使用全局配置加载这张图片 如果bitmap为空 则显示失败的默认图

具体的流程 可以参考源码


### 缓存分为三层

		第一层是LruCache（原理去百度）
		第二层是LinkedHashMap
		第三层是 view标记

*1 当LruCache中的图片超过了规定了内存，那么从LruCache移除一个使用最少的，放到LinkedHashMap中
*2 当每一张图片的url对应一个count，一旦加载一张图片，那么这个url的count加1
*3 自定义AsyImageView继承ImageView，重写了onDetachedFromWindow方法，一旦
*AsyImageView从当前视图移除掉会调用onDetachedFromWindow该方法，此刻该图片所对应的url数目count减1
*4 因为listview中的imageview如果用了ViewHolder那么第3条就不适合了，此刻每一个imagview的hashCode对应一个url,一旦imagview更换了一个新的url,那么该imagview的hashcode上一个的引用将被移除，那么上一次显示的url所对应的count将减1
*5 当LinkedHashMap超过了规定限制的时候，那么遍历所有的count一旦count为0 则移除回收

### 图片下载使用

一：必须条件

		必须在配置文件中添加配置，来打开图片下载引擎的初始化，为了减少启动时间，默认关闭。
		#开启框架内置的图片下载 如果不设置 则无法使用框架类的图片下载
		imageload_open=true

		二：使用方法

### 1 普通图片下载

		ImageDownloader.download("网络和本地图片链接",mAsyImageView)
如果需要配置bitmap的高宽

第一种方式：

		在xml布局文件中对AsyImageView的高宽进行设置

第二种方式：

全局图片配置，所有图片显示默认用此配置
		GlobalConfig globalConfig = GlobalConfig.getInstance();
		globalConfig.setMaxWidth(w);
来设置

第三种

		SingleConfig config = new SingleConfig();
		config ....设置宽高
		ImageDownloader.download("网络和本地图片链接",mAsyImageView,config )

其中优先级

### 第三种 > 第一种 > 第二种

			其中GlobalConfig  支持的设置有高宽的设置，内存缓存的大小，默认图片，
			下载失败的图片，最大缓存数目，线程池，缓存类型，显示控制，listview得滑动监听，图片加载动画
			其中SingleConfig 支持的设置有高宽的设置，默认图片，下载失败的图片，下载进度，显示控制，加载动画

其中SingleConfig 优先于GlobalConfig  

### 支持配置文件配置：
		mAsyImageView.setTemplate("one");
		ImageDownloader.download("url",mAsyImageView);

其中one在配置文件里面配置，这样 不管在任何地方，只要AsyImageView.setTemplate("one");就可以使用名称为one的配置了。

支持本地文件加载调用接口不变。

需要进度显示的：
		SingleConfig config = new SingleConfig();
		config.setDisplayer(new DisplayerLister() {
			@Override
			public void startLoader(AsyImageView imageView) {
			    super.startLoader(imageView);
			}
			@Override
			public Bitmap finishLoader(Bitmap bitmap, AsyImageView imageView) {
				pin_progress_1.setVisibility(View.GONE);
				return bitmap;
			}
			
			@Override
			public void progressLoader(int progress, AsyImageView imageView) {
				pin_progress_1.setProgress(progress);
			    super.progressLoader(progress, imageView);
			}
		});
		ImageDownloader.download("url",photo,config);
其中url的服务器必须支持获取文件长度

需要显示动画的：
如果是单独某一个图片
		SingleConfig config = new SingleConfig();
		config.setDisplayerAnimation(new FadeInAnimation());

如果是全局的
		GlobalConfig config = new GlobalConfig();
		config.setDisplayerAnimation(new FadeInAnimation());

其中FadeInAnimation是框架自带的一个渐变的动画
如果需要自定义 实现DisplayerAnimation接口即可


### 2 listview中图片下载
		只要在listview的注解@InjectView(isasy=true)中添加了isasy=true(默认为false)
		那么系统会自动给你注入OnScrollListener滚动事件，以便实现图片飞行停止才加载,缓慢拖动加载的功能。如果你要实现自己的OnScrollListener

如下

		@InjectBefore
		void test(){
			//@InjectView(isasy=true)表示这个listview里面有网络图片下载，并且需要实现滑动停止才加载的功能
			//@InjectView(isasy=true)框架会给listview自动注入OnScrollListener,如果你自己也要滚动监听
			//那么请在此配置，如下
			GlobalConfig config = GlobalConfig.getInstance();
			config.setOnScrollLoaderListener(new MyOnScrollListener());
			System.out.println("before");
		}
		//必须继承框架内的滚动监听
		class MyOnScrollListener extends OnScrollLoaderListener{
		@Override
        public void onScrollListener(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			ApplicationBean.logger.s("滚动监听:"+firstVisibleItem);
		}

		@Override
        public void onScrollStateChange(AbsListView view, int scrollState) {
			ApplicationBean.logger.s("滚动状态");
			}
		}
@InjectBefore 表示在组件初始化以前开始调用，因为滚动监听必须在listview被初始化之前赋值，否则无效 将默认使用框架内的滚动监听


### 3 无需显示的图片下载
		ImageDownloader.download("url", new LoaderLister() {
			@Override
			public void finishLoader(String url, File file) {
				System.out.println("下载完成"+file.getPath());
			}
			@Override
			public void failLoader(String url) {
				System.out.println("下载失败");
			}
		});
如果需要下载进度
		ImageDownloader.download("url", new LoaderLister() {
			
			@Override
			public void startLoader(String url) {
				System.out.println("开始下载");
			    super.startLoader(url);
			}
			
			
			@Override
			public void finishLoader(String url, File file) {
				System.out.println("下载完成"+file.getPath());
			}
			
			@Override
			public void progressLoader(int progress) {
				System.out.println("下载进度"+progress);
			    super.progressLoader(progress);
			}
		});