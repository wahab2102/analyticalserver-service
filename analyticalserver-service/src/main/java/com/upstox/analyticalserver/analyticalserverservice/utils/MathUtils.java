package com.upstox.analyticalserver.analyticalserverservice.utils;

import java.text.DecimalFormat;

/**
 * The Class MathUtils.
 *
 * @author Wahab
 */
public class MathUtils {

	public static final String TWO_DEC_DOUBLE_FORMAT = "##.00";

	/**
	 * Round double.
	 *
	 * @param value
	 *            the value
	 * @param format
	 *            the format
	 * @return the double
	 */
	public static double roundDouble(double value, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return Double.valueOf(df.format(value));
	}

}
