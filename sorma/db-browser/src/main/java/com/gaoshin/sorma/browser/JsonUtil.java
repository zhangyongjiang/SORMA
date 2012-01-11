package com.gaoshin.sorma.browser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gaoshin.sorma.reflection.FieldFoundCallback;
import com.gaoshin.sorma.reflection.ReflectionUtil;

public class JsonUtil {
	public static <T> T toJavaObject(String jsonString, Class<T> clazz) {
		try {
			if(jsonString == null || jsonString.length() == 0)
				return null;
			if(ReflectionUtil.isPrimeType(clazz)) {
				return (T) ReflectionUtil.convert(jsonString, clazz);
			}
			
			JSONObject jsonObject = new JSONObject(jsonString);
			if(clazz.equals(JSONObject.class))
				return (T) jsonObject;
			
			return toJavaObject(jsonObject, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static <T> T toJavaObject(final JSONObject jsonObject, Class<T> clazz) throws Exception {
		final T javaObj = clazz.newInstance();
		ReflectionUtil.iterateFields(javaObj, new FieldFoundCallback() {
			public void field(Object javaObj, Field field) {
				try {
                    String fieldName = field.getName();
                    if(!jsonObject.has(fieldName))
                    	return;
                    
                    field.setAccessible(true);
                    Object jsonFieldValue = jsonObject.get(fieldName);
                    if(jsonFieldValue == null) {
                    	return;
                    }
                    
                    if(ReflectionUtil.isPrimeField(field)) {
                    	ReflectionUtil.setFieldValue(javaObj, field, jsonFieldValue);
                    }
                    else if (List.class.isAssignableFrom(field.getType())) {
                    	JSONArray jsonArray = null;
                    	if(jsonFieldValue instanceof JSONArray) {
                    		jsonArray = (JSONArray) jsonFieldValue;
                    	}
                    	else {
                    		jsonArray.put(jsonFieldValue);
                    	}
                    	ArrayList<Object> list = new ArrayList<Object>();
                    	field.setAccessible(true);
                    	field.set(javaObj, list);
                    	Class fieldGenericType = ReflectionUtil.getFieldGenericType(field);
                    	for(int i=0; i<jsonArray.length(); i++) {
                    		Object jsonItem = jsonArray.get(i);
                    		if(jsonItem == null) continue;
                    		if(ReflectionUtil.isPrimeType(fieldGenericType)) {
                    			list.add(ReflectionUtil.convert(jsonItem.toString(), fieldGenericType));
                    		}
                    		else {
                    			list.add(toJavaObject((JSONObject)jsonItem, fieldGenericType));
                    		}
                    	}
                    }
                    else if (Map.class.isAssignableFrom(field.getType())) {
                    	Class fieldGenericType = ReflectionUtil.getFieldGenericType(field, 1);
                    	JSONObject jsonMap = (JSONObject) jsonFieldValue;
                    	JSONArray names = jsonMap.names();
                    	if(names == null)
                    	    return;
                    	Map<String, Object> javaMap = new HashMap<String, Object>();
                    	field.setAccessible(true);
                    	field.set(javaObj, javaMap);
                    	for(int i=0; i<names.length(); i++) {
                    		String key = names.getString(i);
                    		Object object = jsonMap.get(key);
                    		if(object == null) continue;
                    		if(Object.class.equals(fieldGenericType)) {
                    		    javaMap.put(key, object.toString());
                    		}
                    		else if(ReflectionUtil.isPrimeType(fieldGenericType)) {
                    			javaMap.put(key, ReflectionUtil.convert(object.toString(), fieldGenericType));
                    		}
                    		else {
                    			javaMap.put(key, toJavaObject((JSONObject)object, fieldGenericType));
                    		}
                    	}
                    }
                    else {
                    	field.setAccessible(true);
                    	field.set(javaObj, toJavaObject((JSONObject)jsonFieldValue, field.getType()));
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
			}
		});
		return javaObj;
	}
	
	public static String toJsonString(Object javaObj) {
		JSONObject jsonObject = toJsonObject(javaObj);
		return jsonObject == null ? null : jsonObject.toString();
	}
	
	public static JSONObject toJsonObject(Object javaObj) {
		if(javaObj==null)
			return null;
		
		final JSONObject json = new JSONObject();
		try {
			ReflectionUtil.iterateFields(javaObj, new FieldFoundCallback() {
				public void field(Object javaObj, Field field) {
					try {
                        String fieldName = field.getName();
                        if(fieldName.indexOf("$")!=-1) {
                            return;
                        }
                        field.setAccessible(true);
                        Object fieldValue = field.get(javaObj);
                        if(fieldValue == null) {
                        	return;
                        }
                        
                        if(ReflectionUtil.isPrimeField(field)) {
                        	json.put(fieldName, ReflectionUtil.primeString(fieldValue));
                        }
                        else if(Modifier.isStatic(field.getModifiers())){
                        }else if (fieldValue instanceof List) {
                        	JSONArray jsonArray = new JSONArray();
                        	json.put(fieldName, jsonArray);
                        	List list = (List) fieldValue;
                        	for(Object item : list) {
                        		if(item == null) continue;
                        		if(ReflectionUtil.isPrimeType(item)) {
                        			jsonArray.put(item);
                        		}
                        		else {
                            		JSONObject jsonItem = toJsonObject(item);
                            		jsonArray.put(jsonItem);
                        		}
                        	}
                        }
                        else if (fieldValue instanceof Map) {
                        	JSONObject subjson = new JSONObject();
                        	json.put(fieldName, subjson);
                        	Map<String, Object> map = (Map) fieldValue;
                        	for(Entry<String, Object> entry : map.entrySet()) {
                        		if(entry.getValue() == null)continue;
                        		if(ReflectionUtil.isPrimeType(entry.getValue())) {
                        			subjson.put(entry.getKey(), entry.getValue());
                        		}
                        		else {
                            		JSONObject jsonItem = toJsonObject(entry.getValue());
                            		subjson.put(entry.getKey(), jsonItem);
                        		}
                        	}
                        } else {
                        	JSONObject subjson = toJsonObject(fieldValue);
                        	json.put(fieldName, subjson);
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
		
		return json;
	}

    public static Object get(Object jsonObj, String... keys) throws JSONException {
        return get((JSONObject) jsonObj, keys);
    }

    public static String getString(Object jsonObj, String... keys) throws JSONException {
        Object obj = get(jsonObj, keys);
        if (obj == null)
            return null;
        else
            return obj.toString();
    }

    public static Long getLong(Object jsonObj, String... keys) throws NumberFormatException, JSONException {
        return Long.parseLong(getString(jsonObj, keys));
    }

    public static Double getDouble(Object jsonObj, String... keys) throws NumberFormatException, JSONException {
        return Double.parseDouble(getString(jsonObj, keys));
    }

    public static Object get(JSONObject jsonObj, String... keys) throws JSONException {
        Object ret = jsonObj;
        for (String s : keys) {
            ret = ((JSONObject) ret).get(s);
        }
        return ret;
    }

    public static JSONArray getList(Object jsonObj, String... keys) throws JSONException {
        return (JSONArray) get(jsonObj, keys);
    }
}
