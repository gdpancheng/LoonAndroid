package com.android.pc.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: TimeHandler
 * @Description: 时间工具类
 * @author  潘城 gdpancheng@gmail.com
 * @date 2012-6-27 上午9:48:35
 *
 */
public class Handler_Time {
	private Calendar cal;

	private Handler_Time() {
		this.cal = Calendar.getInstance();
	}

	public static Handler_Time getInstance() {
		return new Handler_Time();
	}

	public static Handler_Time getInstance(String year, String month) {
		Handler_Time th = new Handler_Time();
		if (year != null && !year.equals("") && !year.equals("null") && month != null && !month.equals("") && !month.equals("null")) {
			th.cal.set(Calendar.YEAR, Integer.parseInt(year));
			th.cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		}
		return th;
	}

	public static Handler_Time getInstance(String year, String month, String day) {
		Handler_Time th = new Handler_Time();
		if (year != null && !year.equals("") && !year.equals("null") && month != null && !month.equals("") && !month.equals("null") && day != null && !day.equals("") && !day.equals("null")) {
			th.cal.set(Calendar.YEAR, Integer.parseInt(year));
			th.cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			th.cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		}
		return th;
	}

	/**
	 * month is start with 1
	 * 
	 * @author LiuSong | mailto:liu@ezcom.net.cn | 2007/06/05 14:20:09
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Handler_Time getInstance(int year, int month, int day) {
		Handler_Time th = new Handler_Time();
		th.cal.set(Calendar.YEAR, year);
		th.cal.set(Calendar.MONTH, month - 1);
		th.cal.set(Calendar.DAY_OF_MONTH, day);

		return th;
	}

	/**
	 * @author 潘城 gdpancheng@gmail.com 2012-5-7 下午5:25:14
	 * @Title: getInstance
	 * @Description: 根据TimeStamp格式的字符串来获得TimeHandler的实例<br>
	 *               字符串格式可以为以下多种格式<br>
	 *               年：2007<br>
	 *               年月：2007-05<br>
	 *               年月日：2007-05-08<br>
	 *               年月日小时：2007-05-08 12<br>
	 *               年月日小时分：2007-05-08 12:10<br>
	 *               年月日小时分秒：2007-05-08 12:10:08
	 * @param @param date_time
	 * @param @return 设定文件
	 * @return TimeHandler 返回类型
	 */
	public static Handler_Time getInstance(String date_time) {
		int stringLength = date_time.length();
		if (stringLength < 4) {
			return null;
		}
		Handler_Time th = new Handler_Time();
		int year = Integer.parseInt(date_time.substring(0, 4));
		int month = 0;
		int day = 1;
		if (stringLength >= 7) {
			month = Integer.parseInt(date_time.substring(5, 7)) - 1;
		}
		if (stringLength >= 10) {
			day = Integer.parseInt(date_time.substring(8, 10));
		}
		// ------------------------------------------------------------
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (stringLength >= 13) {
			hour = Integer.parseInt(date_time.substring(11, 13));
		}
		if (stringLength >= 16) {
			minute = Integer.parseInt(date_time.substring(14, 16));
		}
		if (stringLength == 19) {
			second = Integer.parseInt(date_time.substring(17, 19));
		}
		th.set(year, month, day, hour, minute, second);
		return th;
	}

	public static Handler_Time getInstance(long timeInMillis) {
		Handler_Time th = new Handler_Time();
		th.set(timeInMillis);
		return th;
	}

	public Timestamp getTimestamp() {
		return new Timestamp(cal.getTimeInMillis());
	}

	public String getTimestampStr() {
		String str = (new Timestamp(cal.getTimeInMillis())).toString();
		while (str.length() < 23) {
			str += 0;
		}
		return str;
	}

	public Timestamp getTimestampPlus(long timeInMillis) {
		return new Timestamp(cal.getTimeInMillis() + timeInMillis);
	}

	public Timestamp getTimestamp(int year, int month, int day, int hour, int minute) {
		cal.set(year, month, day, hour, minute, 0);
		return new Timestamp(cal.getTimeInMillis());
	}

	public int getYear() {
		return cal.get(Calendar.YEAR);
	}

	/**
	 * @author 潘城 gdpancheng@gmail.com 2012-5-7 下午5:25:43
	 * @Title: getYearSimple
	 * @Description: 获得年的后两位数字<br>
	 *               例如，2008获得8，1998获得98<br>
	 * @param @return 设定文件
	 * @return int 返回类型
	 */
	public int getYearSimple() {
		int year = cal.get(Calendar.YEAR);
		int yearSimple;
		if (year > 2000) {
			yearSimple = year - 2000;
		} else if (year > 1900) {
			yearSimple = year - 1900;
		} else if (year > 1800) {
			yearSimple = year - 1800;
		} else {
			yearSimple = year - 1700;
		}
		return yearSimple;
	}

	/**
	 * 获得年的后两位数字的字符串<br>
	 * 例如，2008获得08，1998获得98<br>
	 * 
	 * @return
	 */
	public String getYearSimpleStr() {
		int year = getYearSimple();
		return Handler_String.addPrefixZero(year);

	}

	public int getYearPrev() {
		if (this.getMonth() == 1) {
			return this.getYear() - 1;
		} else {
			return this.getYear();
		}
	}

	public int getYearNext() {
		if (this.getMonth() == 12) {
			return this.getYear() + 1;
		} else {
			return this.getYear();
		}
	}

	public String getYearStr() {
		return String.valueOf(this.getYear());
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH) + 1;
	}

	public int getMonthPrev() {
		if (this.getMonth() == 1) {
			return 12;
		} else {
			return this.getMonth() - 1;
		}
	}

	public int getMonthNext() {
		if (this.getMonth() == 12) {
			return 1;
		} else {
			return this.getMonth() + 1;
		}
	}

	public String getMonthStr() {
		return Handler_String.addPrefixZero(this.getMonth());
	}

	public int getDay() {
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public String getDayStr() {
		return Handler_String.addPrefixZero(this.getDay());
	}

	public int getHour() {
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public String getHourStr() {
		return Handler_String.addPrefixZero(this.getHour());
	}

	public int getMinute() {
		return cal.get(Calendar.MINUTE);
	}

	public String getMinuteStr() {
		return Handler_String.addPrefixZero(this.getMinute());
	}

	public int getSecond() {
		return cal.get(Calendar.SECOND);
	}

	public String getSecondStr() {
		return Handler_String.addPrefixZero(this.getSecond());
	}

	/**
	 * 获得TimeStamp格式的年月日字符串<br>
	 * 例：2007-05-08
	 * 
	 * @return
	 */
	public String getYYYYMMDD() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(this.getYearStr());
		sbf.append("-");
		sbf.append(this.getMonthStr());
		sbf.append("-");
		sbf.append(this.getDayStr());
		return sbf.toString();
	}

	/**
	 * 获得中日文的年月日字符串<br>
	 * 例：2007年05月08日
	 * 
	 * @return
	 */
	public String getYYYYMMDDLabel() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(this.getYearStr()).append("年");
		sbf.append(this.getMonthStr()).append("月");
		sbf.append(this.getDayStr()).append("日");
		return sbf.toString();
	}

	/**
	 * 获得前一天的TimeStamp格式的年月日字符串<br>
	 * 例：2007-05-08
	 * 
	 * @return
	 */
	public String getYYYYMMDDPrevious() {
		Handler_Time previousDay = getTimeHandlerPrevious();
		return previousDay.getYYYYMMDD();
	}

	/**
	 * 获得后一天的TimeStamp格式的年月日字符串<br>
	 * 例：2007-05-08
	 * 
	 * @return
	 */
	public String getYYYYMMDDNext() {
		Handler_Time nextDay = getTimeHandlerNext();
		return nextDay.getYYYYMMDD();
	}

	/**
	 * 获得前一天的TimeHandler实例<br>
	 * 
	 * @return
	 */
	public Handler_Time getTimeHandlerPrevious() {
		long todayTimeInMillis = this.getTimeInMillis();
		Handler_Time previousDay = Handler_Time.getInstance(todayTimeInMillis - 24 * 3600 * 1000l);
		return previousDay;
	}

	/**
	 * 获得后一天的TimeHandler实例<br>
	 * 
	 * @return
	 */
	public Handler_Time getTimeHandlerNext() {
		long todayTimeInMillis = this.getTimeInMillis();
		Handler_Time nextDay = Handler_Time.getInstance(todayTimeInMillis + 24 * 3600 * 1000l);
		return nextDay;
	}

	/**
	 * 获得TimeStamp格式的年月字符串<br>
	 * 例：2007-05
	 * 
	 * @return
	 */
	public String getYYYYMM() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(this.getYearStr());
		sbf.append("-");
		sbf.append(this.getMonthStr());
		return sbf.toString();
	}

	/**
	 * 获得中日文的年月字符串<br>
	 * 例：2007年05月
	 * 
	 * @return
	 */
	public String getYYYYMMLabel() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(this.getYearStr()).append("年");
		sbf.append(this.getMonthStr()).append("月");
		return sbf.toString();
	}

	/**
	 * 获得前一个月的TimeStamp格式的年月字符串<br>
	 * 例：2007-05
	 * 
	 * @return
	 */
	public String getYYYYMMPrevious() {
		int year = this.getYear();
		int previousMonth = getMonthPrev();
		if (previousMonth == 12) {
			year = year - 1;
		}
		StringBuffer sbf = new StringBuffer();
		sbf.append(year).append("-").append(Handler_String.addPrefixZero(previousMonth));
		return sbf.toString();
	}

	/**
	 * 获得下一个月的TimeStamp格式的年月字符串<br>
	 * 例：2007-05
	 * 
	 * @return
	 */
	public String getYYYYMMNext() {
		int year = this.getYear();
		int nextMonth = getMonthNext();
		if (nextMonth == 1) {
			year = year + 1;
		}
		StringBuffer sbf = new StringBuffer();
		sbf.append(year).append("-").append(Handler_String.addPrefixZero(nextMonth));
		return sbf.toString();
	}

	public String getYyyyMmKanji() {
		StringBuffer sbf = new StringBuffer();
		sbf.append(this.getYearStr());
		sbf.append(" 年 ");
		sbf.append(this.getMonth());
		sbf.append(" 月 ");
		return sbf.toString();
	}

	public void set(int year, int month, int day, int hour, int minute, int second) {
		cal.set(year, month, day, hour, minute, second);
	}

	public void set(int year, int month, int day, int hour, int minute) {
		cal.set(year, month, day, hour, minute, 0);
	}

	public void set(int field, int value) {
		cal.set(field, value);
	}

	public void set(long timeInMillis) {
		cal.setTimeInMillis(timeInMillis);
	}

	public long getTimeInMillis() {
		return cal.getTimeInMillis();
	}

	/**
	 * 获得UNIX_TIMESTAMP的秒的INT型数字的值<br>
	 * 值的长度应该和MYSQL的UNIX_TIMESTAMP()函数的值的长度是一致的<br>
	 * 例如：1196440210<br>
	 * 例如：1246026128<br>
	 * 
	 * @return UNIX_TIMESTAMP的秒的INT型数字的值
	 */
	public int getTimeInSeconds() {
		return (int) (this.getTimeInMillis() / 1000);
	}

	public boolean checkDate(int year, int month, int day) {
		cal.set(year, month, 1);
		if (day > cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得当月的天数
	 * 
	 * @return
	 */
	public int getMaxDayOfTheMonth() {
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @return
	 */
	public int getDayOfTheWeek() {
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	public Date getDate() {
		return new Date(this.getTimeInMillis());
	}

	public Calendar getCalendar() {
		return cal;
	}

	public static String now() {
		return Handler_Time.getInstance().getTimestamp().toString().substring(0, 19);
	}

	public String getPeriodStr(long timeInMillis) {
		long result = 0;
		StringBuffer sbf = new StringBuffer();
		//
		result = (timeInMillis / 1000) / 3600;
		if (result > 0) {
			sbf.append(Handler_String.addPrefixZero((int) result)).append(":");
			timeInMillis = timeInMillis - result * 3600 * 1000;
		} else {
			sbf.append("00:");
		}
		//
		result = (timeInMillis / 1000) / 60;
		if (result > 0) {
			sbf.append(Handler_String.addPrefixZero((int) result)).append(":");
			timeInMillis = timeInMillis - result * 60 * 1000;
		} else {
			sbf.append("00:");
		}
		//
		result = timeInMillis / 1000;
		sbf.append(Handler_String.addPrefixZero((int) result));
		return sbf.toString();
	}

	public static Handler_Time linuxTimeToWinTime(Handler_Time timeHandler) {
		int year = timeHandler.getYear();
		int month = timeHandler.getMonth();
		int day = timeHandler.getDay();
		int hour = timeHandler.getHour();
		int minute = timeHandler.getMinute();
		int second = timeHandler.getSecond();
		hour = hour + 8;
		timeHandler.set(year, month - 1, day, hour, minute, second);
		return timeHandler;
	}

	public String toString() {
		return this.getTimestampStr();
	}
	
	public static String formatDate(int year, int month, int day) {
		StringBuffer sbf = new StringBuffer();
		sbf.append(year);
		sbf.append("-");
		sbf.append(Handler_String.addPrefixZero(month + 1));
		sbf.append("-");
		sbf.append(Handler_String.addPrefixZero(day));
		return sbf.toString();
	}
	
	public static String formatDuring(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
	}
}
