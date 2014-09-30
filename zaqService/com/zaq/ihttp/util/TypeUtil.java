package com.zaq.ihttp.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtil {
	@SuppressWarnings("unchecked")
	public static Type getSuperclassTypeParameter(Class subclass) {
		Type superclass = subclass.getGenericSuperclass();
		if (superclass instanceof Class)
			throw new RuntimeException("Missing type parameter.");
		else
			return ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}
}
