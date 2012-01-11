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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ReflectionUtil {
	public static <T> T copy(Class<T> clazz, Object from) {
		return copy(clazz, from, false);
	}
	
	public static <T> T copy(Class<T> clazz, Object from, boolean primeOnly) {
		try {
			Object to = clazz.newInstance();
			copy(to, from, primeOnly);
			return (T) to;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(final Object to, final Object from) {
		copy(to, from, false);
	}

	public static void copy(final Object to, final Object from,
			final boolean primeOnly) {
		if ((from == null) || (to == null)) {
			return;
		}
		try {
			iterateFields(to, new FieldFoundCallback() {
				public void field(Object o, Field field) {
					Class<?> fieldType = field.getType();
					if (!isPrimeType(fieldType) && primeOnly) {
						return;
					}
					try {
                        Method setter = getSetter(to.getClass(), field.getName(),
                        		fieldType);
                        Method getter = getGetter(from.getClass(), field.getName());
                        if (setter == null || getter == null) {
                        	return;
                        }
                        Object fromFieldValue = getter.invoke(from, null);
                        if (isPrimeType(fieldType)) {
                        	setter.invoke(to, fromFieldValue);
                        } else {
                        	Object toFieldValue = copy(fieldType, fromFieldValue,
                        			primeOnly);
                        	setter.invoke(to, toFieldValue);
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void iterateClassTree(Class<?> clazz, ClassCallback callback) {
		while (true) {
			if (clazz == null || clazz.equals(Object.class)) {
				break;
			}
			callback.classFound(clazz);
			clazz = clazz.getSuperclass();
		}
	}

	public static void iterateFields(final Object o,
			final FieldFoundCallback callback) throws Exception {
		iterateFields(o.getClass(), o, callback);
	}

	public static void iterateFields(final Class<?> clazz, final Object o,
			final FieldFoundCallback callback) {
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> cls) {
				for (Field field : cls.getDeclaredFields()) {
					callback.field(o, field);
				}
			}
		});
	}

	public static void iterateMethods(final Object o,
			final MethodFoundCallback callback) throws Exception {
		iterateMethods(o.getClass(), o, callback);
	}

	private static void iterateMethods(final Class<?> clazz, final Object o,
			final MethodFoundCallback callback) throws Exception {
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> cls) {
				for (Method method : cls.getDeclaredMethods()) {
					callback.method(o, method);
				}
			}
		});
	}

	public static void iterateAnnotation(final Object o,
			final Class<?> annoClass, final AnnotationFoundCallback callback)
			throws Exception {
		iterateAnnotatedFields(o, annoClass, callback);
		iterateAnnotatedMethods(o, annoClass, callback);
	}

	public static void iterateAnnotatedFields(final Object o,
			final Class<?> annoClass, final AnnotatedFieldCallback callback)
			throws Exception {
		iterateAnnotatedFields(o.getClass(), o, annoClass, callback);
	}

	public static void iterateAnnotatedMethods(final Object o,
			final Class<?> annoClass, final AnnotatedMethodCallback callback)
			throws Exception {
		iterateAnnotatedMethods(o.getClass(), o, annoClass, callback);
	}

	public static void iterateAnnotatedFields(final Object o,
			final Class<?> annoClass, final Class<?> fieldType,
			final AnnotatedFieldCallback callback) throws Exception {
		iterateAnnotatedFields(o.getClass(), o, annoClass, fieldType, callback);
	}

	public static void iterateAnnotatedFields(final Class<?> clazz,
			final Object o, final Class<?> annoClass,
			final AnnotatedFieldCallback callback) throws Exception {
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> cls) {
				for (Field field : cls.getDeclaredFields()) {
					for (Annotation fieldAnnot : field.getAnnotations()) {
						if (fieldAnnot.annotationType().equals(annoClass)) {
							callback.field(o, field);
							break;
						}
					}
				}
			}
		});
	}

	private static void iterateAnnotatedFields(final Class<?> clazz,
			final Object o, final Class<?> annoClass, final Class<?> fieldType,
			final AnnotatedFieldCallback callback) throws Exception {
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> cls) {
				for (Field field : cls.getDeclaredFields()) {
					Class<?> type = field.getType();
					if (!fieldType.isAssignableFrom(type)) {
						continue;
					}
					for (Annotation fieldAnnot : field.getAnnotations()) {
						if (!fieldAnnot.equals(annoClass)) {
							continue;
						}
					}
					callback.field(o, field);
				}
			}
		});
	}

	private static void iterateAnnotatedMethods(final Class<?> clazz,
			final Object o, final Class<?> annoClass,
			final AnnotatedMethodCallback callback) throws Exception {
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> cls) {
				for (Method method : cls.getDeclaredMethods()) {
					for (Annotation methodAnnot : method.getAnnotations()) {
						if (methodAnnot.annotationType().equals(annoClass)) {
							callback.method(o, method);
							break;
						}
					}
				}
			}
		});
	}

	public static void setAnnotatedFields(final Object o,
			final Class<?> annoClass, final Object fieldValue) throws Exception {
		iterateAnnotatedFields(o, annoClass, new AnnotatedFieldCallback() {
			public void field(Object obj, Field field) {
				try {
                    field.setAccessible(true);
                    field.set(obj, fieldValue);
                }
                catch (Throwable e) {
                    throw new RuntimeException(e);
                }
			}
		});
	}

	public static void setAnnotatedFields(final Object o,
			final Class<?> annoClass, final Class<?> fieldType,
			final Object fieldValue) throws Exception {
		iterateAnnotatedFields(o, annoClass, fieldType,
				new AnnotatedFieldCallback() {
					public void field(Object obj, Field field) {
						try {
                            field.setAccessible(true);
                            field.set(obj, fieldValue);
		                }
		                catch (Throwable e) {
		                    throw new RuntimeException(e);
		                }
					}
				});
	}

	public static Class getParameterizedType(Class clazz) {
		return getParameterizedType(clazz, 0);
	}

	public static Class getParameterizedType(Class clazz, int index) {
		ParameterizedType s = (ParameterizedType) clazz.getGenericSuperclass();
		return (Class) s.getActualTypeArguments()[index];
	}

	public static Class getFieldGenericType(Class c, String fieldName)
			throws Exception {
		Field f = c.getDeclaredField(fieldName);
		return getFieldGenericType(f);
	}

	public static Class getFieldGenericType(Field f) throws Exception {
		ParameterizedType gt = (ParameterizedType) f.getGenericType();
		return (Class) gt.getActualTypeArguments()[0];
	}

	public static Class getFieldGenericType(Field f, int index) throws Exception {
		ParameterizedType gt = (ParameterizedType) f.getGenericType();
		return (Class) gt.getActualTypeArguments()[index];
	}

	public static Method getMethod(Class cls, final String name,
			final Class<?>... parmTypes) throws Exception {
		final ArrayList<Method> holder = new ArrayList<Method>();
		iterateClassTree(cls, new ClassCallback() {

			public void classFound(Class<?> clazz) {
				try {
					Method method = clazz.getMethod(name, parmTypes);
					holder.add(method);
				} catch (Exception e) {
					// don't remove try...catch
				}
			}
		});

		if (holder.size() == 0) {
			return null;
		}
		return holder.get(0);
	}

	public static boolean annotatedWith(Method method, Class annoCls) {
		try {
			Annotation annotation = method.getAnnotation(annoCls);
			return annotation != null;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean annotatedWith(Field field, Class annoCls) {
		try {
			Annotation annotation = field.getAnnotation(annoCls);
			return annotation != null;
		} catch (Exception e) {
			return false;
		}
	}

    public static boolean annotatedWith(Class<?> target, Class annoCls) {
        try {
            Annotation annotation = target.getAnnotation(annoCls);
            return annotation != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T extends Annotation>T getAnnotation(Class<?> target, Class<T> annoCls) {
        if(Object.class.equals(target)) {
            return null;
        }
        try {
            T annotation = target.getAnnotation(annoCls);
            if(annotation != null)
                return annotation;
            else
                return getAnnotation(target.getSuperclass(), annoCls);
        } catch (Exception e) {
            return null;
        }
    }

	public static Field getField(Class clazz, final String fieldName) {
		final ArrayList<Field> holder = new ArrayList<Field>();
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> clazz){
				try {
					Field field = clazz.getDeclaredField(fieldName);
					holder.add(field);
				} catch (Exception e) {
					// don't remove this try...catch block.
				}
			}
		});
		if (holder.size() == 0) {
			return null;
		}
		return holder.get(0);
	}

	public static Method getMethod(Class clazz, final String methodName,
			final Class<?> parameterTypes) throws Exception {
		final ArrayList<Method> holder = new ArrayList<Method>();
		iterateClassTree(clazz, new ClassCallback() {
			public void classFound(Class<?> clazz) {
				try {
					Method method = clazz.getDeclaredMethod(methodName,
							parameterTypes);
					holder.add(method);
				} catch (Exception e) {
					// don't remove this try...catch block.
				}
			}
		});
		if (holder.size() == 0) {
			return null;
		}
		return holder.get(0);
	}

	public static Method getSetter(Class clazz, final String fieldName,
			final Class<?> fieldType) throws Exception {
		final String methodName = "set"
				+ fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1);
		return getMethod(clazz, methodName, fieldType);
	}

	public static Method getGetter(Class clazz, final String fieldName)
			throws Exception {
		try {
			String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			return getMethod(clazz, methodName, new Class<?>[0]);
		} catch (Exception e) {
			String methodName = "is" + fieldName.substring(0, 1).toUpperCase()
					+ fieldName.substring(1);
			return getMethod(clazz, methodName, new Class<?>[0]);
		}
	}

	public static void setField(Object target, String fieldName,
			Object fieldValue) throws Exception {
		Field field = getField(target.getClass(), fieldName);
		field.setAccessible(true);
		field.set(target, fieldValue);
	}

	public static void callSetter(Object target, String fieldName,
			Object fieldValue) throws Exception {
		Method setter = getSetter(target.getClass(), fieldName,
				fieldValue.getClass());
		setter.invoke(target, fieldValue);
	}

	public static void callSetter(Object target, String fieldName,
			Object fieldValue, Class<?> fieldType) throws Exception {
		Method setter = getSetter(target.getClass(), fieldName, fieldType);
		setter.invoke(target, fieldValue);
	}

	public static void callSetter(Object target, String fieldName,
			String fieldValue) throws Exception {
		Field field = getField(target.getClass(), fieldName);
		if (field == null) {
			return;
		}
		Class<?> fieldType = field.getType();
		if (isPrimeType(fieldType)) {
			Object value = convert(fieldValue, fieldType);
			callSetter(target, fieldName, value, fieldType);
		} else if (fieldType.isEnum()) {
			for (Object o : fieldType.getEnumConstants()) {
				Enum<?> c = (Enum<?>) o;
				if (c.name().equals(fieldValue)) {
					callSetter(target, fieldName, c);
				}
			}
		} else {
			Method fromString = fieldType.getDeclaredMethod("fromString",
					String.class);
			Object value = fromString.invoke(fieldType.newInstance(),
					fieldValue);
			callSetter(target, fieldName, value);
		}
	}

	public static Object callGetter(Object target, String fieldName)
			throws Exception {
		Method getter = getGetter(target.getClass(), fieldName);
		return getter.invoke(target, new Object[0]);
	}

	public static boolean isPrimeField(Field field) {
		return isPrimeType(field.getType());
	}

	public static boolean isPrimeType(Class type) {
		if (type.equals(String.class)) {
			return true;
		}
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return true;
		}
		if (type.equals(Float.class) || type.equals(float.class)) {
			return true;
		}
		if (type.equals(Double.class) || type.equals(double.class)) {
			return true;
		}
		if (type.equals(Long.class) || type.equals(long.class)) {
			return true;
		}
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			return true;
		}
		if (type.equals(Date.class)) {
			return true;
		}
		if (type.equals(Calendar.class)) {
			return true;
		}
		if(type.isEnum()) {
		    return true;
		}

		return false;
	}

	public static Object convert(String value, Class toType) throws Exception {
		if (value == null) {
			return null;
		}
		if (toType.equals(String.class)) {
			return value;
		}
		if (toType.equals(Integer.class) || toType.equals(int.class)) {
			return Integer.parseInt(value);
		}
		if (toType.equals(Float.class) || toType.equals(float.class)) {
			return Float.parseFloat(value);
		}
		if (toType.equals(Double.class) || toType.equals(double.class)) {
			return Double.parseDouble(value);
		}
		if (toType.equals(Long.class) || toType.equals(long.class)) {
			return Long.parseLong(value);
		}
		if (toType.equals(Boolean.class) || toType.equals(boolean.class)) {
			value = value.toLowerCase();
			if (value.equals("1") || value.startsWith("t")
					|| value.startsWith("y") || value.equalsIgnoreCase("on")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		}
		if (toType.equals(Date.class)) {
			try {
				Long millisecond = Long.parseLong(value);
				if(millisecond == 0)
					return null;
				return new Date(millisecond);
			} catch (Exception e) {
				SimpleDateFormat sdf = getIso8601DateFormat();
				return sdf.parse(value);
			}
		}
		if (toType.equals(Calendar.class)) {
			try {
				Long millisecond = Long.parseLong(value);
				if(millisecond == 0)
					return null;
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(millisecond);
				return cal;
			} catch (Exception e) {
				return XCalendarAdapter.unmarshal(value);
			}
		}
        if (toType.isEnum()) {
            for (Object e : toType.getEnumConstants()) {
                if (e.toString().equals(value))
                    return e;
            }
        }
		throw new Exception("Unhandled data type: " + toType);
	}

	public static SimpleDateFormat getIso8601DateFormat() {
		final SimpleDateFormat ISO8601UTC = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");// 24
		// characters
		ISO8601UTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC == GMT
		return ISO8601UTC;
	}

	public static boolean isPrimeType(Object item) {
		return isPrimeType(item.getClass());
	}

	public static void setFieldValue(Object target, Field field, Object value) {
		try {
			field.setAccessible(true);
			if(value == null) {
				field.set(target, null);
			}
			else if(field.getType().isAssignableFrom(value.getClass())) {
				field.set(target, value);
			}
			else if (isPrimeField(field)){
				Object convert = convert(value.toString(), field.getType());
				field.set(target, convert);
			}
			else {
				throw new Exception("cannot set field value " + field.getName() + " for " + target.getClass());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String primeString(Object primeObject) {
		if(primeObject == null)
			return null;
		if(primeObject instanceof Date) {
			return getIso8601DateFormat().format((Date)primeObject);
		}
		if(primeObject instanceof Calendar) {
			return getIso8601DateFormat().format(((Calendar)primeObject).getTime());
		}
		return primeObject.toString();
	}
}
