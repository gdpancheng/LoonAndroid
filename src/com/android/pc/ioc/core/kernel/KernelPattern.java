package com.android.pc.ioc.core.kernel;

import java.util.regex.Pattern;

public class KernelPattern {

	/**
	 * @param strs
	 * @param flags
	 * @return
	 */
	public static Pattern[] getPatterns(String[] strs, int flags) {
		if (strs == null) {
			return null;
		}

		int length = strs.length;
		if (length == 0) {
			return null;
		}

		Pattern[] patterns = new Pattern[length];
		for (int i = 0; i < length; i++) {
			patterns[i] = Pattern.compile(strs[i], flags);
		}

		return patterns;
	}

	/**
	 * @param str
	 * @param patterns
	 * @return
	 */
	public static boolean matchPatterns(String str, Pattern[] patterns) {
		if (patterns == null) {
			return false;
		}

		for (Pattern pattern : patterns) {
			if (pattern.matcher(str).find()) {
				return true;
			}
		}

		return false;
	}
}
