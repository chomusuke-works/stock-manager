package app.util;

import io.javalin.http.Context;

public class ContextHelper {
	private ContextHelper() {
	}

	public static long getLongQueryParam(Context context, String key) throws NullPointerException, NumberFormatException {
		String value = context.queryParam(key);
		if (value == null) throw new NullPointerException("No value for key: " + key);

		return Long.parseLong(value);
	}
}
