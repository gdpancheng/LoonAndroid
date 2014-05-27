package com.android.pc.ioc.update;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.RemoteViews;

import com.android.pc.ioc.app.Ioc;

public class NotificationHelper {

	private Context mContext;
	private RemoteViews mRemoteViews;
	private Notification mDownProgrNotif;
	private PackageHelper mPackageHelper;
	private NotificationManager mContextNotificationManager;
	private int layout_id;
	private int icon_id;
	private int progress_id;
	private int progress_txt_id;
	private int id;
	private Class clazz;

	public NotificationHelper(Context ctx, int layout_id, int icon_id, int progress_id, int progress_txt_id, Class clazz) {
		this.mContext = ctx;
		this.mPackageHelper = new PackageHelper();
		this.mContextNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		this.layout_id = layout_id;
		this.icon_id = icon_id;
		this.progress_id = progress_id;
		this.progress_txt_id = progress_txt_id;
		this.clazz = clazz;
		id = Integer.valueOf((System.currentTimeMillis() + "").substring(4));
	}

	public void initNotif() {
		mDownProgrNotif = new Notification();
		mDownProgrNotif.icon = android.R.drawable.stat_sys_download;
		mDownProgrNotif.flags |= Notification.FLAG_ONGOING_EVENT;

		mRemoteViews = new RemoteViews(mPackageHelper.getPackageName(), layout_id);
		mRemoteViews.setImageViewResource(icon_id, mPackageHelper.getAppIcon());

		mDownProgrNotif.contentView = mRemoteViews;
		Intent intent = null;
		if (clazz != null) {
			intent = new Intent(Ioc.getIoc().getApplication(), clazz);
		}
		mDownProgrNotif.contentIntent = PendingIntent.getService(mContext, 0, intent == null ? new Intent() : intent, 0);
		// mContextNotificationManager.notify(id, mDownProgrNotif);
	}

	private Notification getDownFinishedNotification(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

		Notification noti = new Notification();
		noti.setLatestEventInfo(mContext, mPackageHelper.getAppName(), "下载完成,点击安装", pendingIntent);
		noti.icon = android.R.drawable.stat_sys_download_done;
		noti.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
		return noti;
	}

	/**
	 * 下载成功或者失败以后 刷新顶部通知栏
	 * 
	 * @author gdpancheng@gmail.com 2014-3-2 下午11:33:43
	 * @return Notification
	 */
	public void downShowNotification(String text) {
		Intent intent = new Intent();
		if (clazz != null) {
			intent = new Intent(Ioc.getIoc().getApplication(), clazz);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		Notification noti = new Notification();
		noti.setLatestEventInfo(mContext, mPackageHelper.getAppName(), text, pendingIntent);
		noti.icon = android.R.drawable.stat_sys_download_done;
		noti.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
		mContextNotificationManager.notify(id, noti);
	}

	/**
	 * 下载中 这个是不支持断点下载的 所以无法显示进度
	 * 
	 * @author gdpancheng@gmail.com 2014-3-2 下午11:33:43
	 * @return Notification
	 */
	public void downNotification(String text) {
		Notification notfi = new Notification();
		notfi.icon = android.R.drawable.stat_sys_download;
		notfi.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent intent = null;
		if (clazz != null) {
			intent = new Intent(Ioc.getIoc().getApplication(), clazz);
		}
		PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent == null ? new Intent() : intent, 0);
		notfi.setLatestEventInfo(mContext, mPackageHelper.getAppName(), text, pendingIntent);
		mContextNotificationManager.notify(id, notfi);
	}

	public void cancel() {
		mContextNotificationManager.cancel(id);
	}

	/**
	 * 更新下载进度
	 * 
	 * @param percent
	 */
	public void refreshProgress(float percent) {
		mRemoteViews.setProgressBar(progress_id, 100, (int) percent, false);
		mRemoteViews.setTextViewText(progress_txt_id, String.format("%.1f", percent));
		mContextNotificationManager.notify(id, mDownProgrNotif);
	}

	/**
	 * 通知用户下载已经完成
	 * 
	 * @param file
	 */
	public void notifyUpdateFinish(File file) {
		mContextNotificationManager.notify(id, getDownFinishedNotification(file));
	}

	class PackageHelper {
		private PackageInfo info = null;
		private PackageManager pm;

		public PackageHelper() {
			pm = Ioc.getIoc().getApplication().getPackageManager();
			try {
				info = pm.getPackageInfo(Ioc.getIoc().getApplication().getPackageName(), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

		public String getAppName() {
			return info != null ? (String) info.applicationInfo.loadLabel(pm) : "";
		}

		public String getPackageName() {
			return info != null ? info.packageName : "";
		}

		public int getAppIcon() {
			return info != null ? info.applicationInfo.icon : android.R.drawable.ic_dialog_info;
		}
	}
}
