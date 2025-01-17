package app.util;

import io.javalin.http.Context;

public class ContextHelper {
	private ContextHelper() {
	}

	public static long getLongPathParam(Context context, String key) throws NumberFormatException {
		String value = context.pathParam(key);

		return Long.parseLong(value);
	}

	public static int getIntPathParam(Context context, String key) throws NumberFormatException {
		String value = context.pathParam(key);
		return Integer.parseInt(value);
	}

	public static long getLongQueryParam(Context context, String key) throws NullPointerException, NumberFormatException {
		String value = context.queryParam(key);
		if (value == null) throw new NullPointerException("No value for key: " + key);

		return Long.parseLong(value);
	}

	public static int getIntQueryParam(Context context, String key) throws NullPointerException, NumberFormatException {
		String value = context.queryParam(key);
		if (value == null) throw new NullPointerException("No value for key: " + key);

		return Integer.parseInt(value);
	}
}
