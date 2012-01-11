/**
 * Copyright (c) 2008-2011, Open & Green Inc.
 * All rights reserved.
 * info@gaoshin.com
 * 
 * This version of SORMA is licensed under the terms of the Open Source GPL 3.0 license. http://www.gnu.org/licenses/gpl.html. Alternate Licenses are available.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, 
 * AND NON-INFRINGEMENT OF THIRD-PARTY INTELLECTUAL PROPERTY RIGHTS.  
 * See the GNU General Public License for more details.
 * 
 */
package com.gaoshin.sorma.reflection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class XCalendarAdapter {
	private static final SimpleDateFormat sdf = ReflectionUtil.getIso8601DateFormat();

	public static String marshal(Calendar arg0) throws Exception {
		return sdf.format(arg0.getTime());
	}

	public static Calendar unmarshal(String arg0) throws Exception {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(sdf.parse(arg0));
		return cal;
	}
}
