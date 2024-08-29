package com.niu.core.common.utils.file;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public final class YmlToPropertiesConverter {

    private static final String TOP = "";

    private InputStream inputStream;

    public YmlToPropertiesConverter(String filePath) {
        try {
            this.inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public YmlToPropertiesConverter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * @return
     */
    public Properties convert() {
        Properties properties = new Properties();
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> entityMap = this.convertMapToMap(yaml.load(inputStream));
            Iterator<Map.Entry<String, Object>> it = entityMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                Object value = entry.getValue();
                if (value instanceof String) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Boolean) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Byte) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Short) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Integer) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Long) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Float) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                } else if (value instanceof Double) {
                    properties.setProperty(entry.getKey(), String.valueOf(value));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return properties;
    }

    /**
     * @return
     */
    public Map<String, Object> convertMap() {
        Map<String, Object> propsMap = new HashMap<>();
        try {
            Yaml yaml = new Yaml();
            propsMap = this.convertMapToMap(yaml.load(inputStream));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return propsMap;
    }

    /**
     * @param ymlMap
     * @return
     */
    private Map<String, Object> convertMapToMap(LinkedHashMap<String, Object> ymlMap) {
        final Map<String, Object> topMap = new Hashtable<>();
        this.mergePorpsToMap(TOP, topMap, ymlMap);
        return topMap;
    }

    /**
     * @param key
     * @param toMap
     * @param fromMap
     */
    private void mergePorpsToMap(String key, final Map<String, Object> toMap, Map<String, Object> fromMap) {
        Iterator<Map.Entry<String, Object>> it = fromMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            Object value = entry.getValue();
            if (value == null) {
                // ignore
            } else if (value instanceof Map) {
                String relKey = entry.getKey();
                if (key != null && !"".equals(key)) {
                    relKey = key + "." + entry.getKey();
                }
                this.mergePorpsToMap(relKey, toMap, (LinkedHashMap<String, Object>) value);
            } else {
                String relKey = entry.getKey();
                if (key != null) {
                    relKey = key + "." + entry.getKey();
                }
                toMap.put(relKey, entry.getValue());
            }
        }
    }

    /**
     * @param inputStream
     * @return
     */
    public static YmlToPropertiesConverter build(InputStream inputStream) {
        return new YmlToPropertiesConverter(inputStream);
    }

    /**
     * @param filePath
     * @return
     */
    public static YmlToPropertiesConverter build(String filePath) {
        return new YmlToPropertiesConverter(filePath);
    }

}
