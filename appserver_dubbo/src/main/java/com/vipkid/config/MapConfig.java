package com.vipkid.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 配置的工具类
 *
 * @version 1.0.0
 */
public final class MapConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapConfig.class);

    private MapConfig() {

    }

    /**
     * 从配置中取得int类型的值,如果name不存在,则返回默认值
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static int getInt(String name, Map<String, String> config, int defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }

    /**
     * 从配置中取得byte类型的值,如果name不存在,则返回默认值
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static byte getByte(String name, Map<String, String> config, byte defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Byte.parseByte(s);
    }

    /**
     * 从配置中取得long类型的值,如果name不存在,则返回默认值
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static long getLong(String name, Map<String, String> config, long defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Long.parseLong(s);
    }

    /**
     * 从配置中取得String类型的值,如果name不存在,则返回默认值
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static String getString(String name, Map<String, String> config, String defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return s;
    }

    /**
     * 从配置文件中取得boolean类型的值,如果name不存在,则返回默认值<code>defaultValue</code>
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String name, Map<String, String> config, boolean defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Boolean.valueOf(s);
    }

    /**
     * 从配置文件中取得double类型的值,如果name不存在,则返回默认值<code>defaultValue</code>
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static double getDouble(String name, Map<String, String> config, double defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Double.valueOf(s);
    }

    /**
     * 从配置文件中取得float类型的值,如果name不存在,则返回默认值<code>defaultValue</code>
     *
     * @param name
     * @param config
     * @param defaultValue
     * @return
     */
    public static float getFloat(String name, Map<String, String> config, float defaultValue) {
        Preconditions.checkArgument(name != null, "name");
        if (config == null) {
            return defaultValue;
        }
        String s = config.get(name);
        if (s == null) {
            return defaultValue;
        }
        return Float.valueOf(s);
    }


    /**
     * 解析配置文件
     *
     * @param appConfPath
     * @return
     */
    public static ImmutableMap<String, String> pasreConf(String appConfPath) {
        Map<String, String> all = Maps.newHashMap();
        if (appConfPath != null) {
            String[] appConfs = appConfPath.split(",");
            for (final String conf : appConfs) {
                LOGGER.info("Load config from " + conf);
                Map<String, String> confMap = XmlProperties.loadFromXml(conf);
                if (confMap != null) {
                    Set<Map.Entry<String, String>> entries = confMap.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        String key = entry.getKey();
                        String s = all.get(key);
                        if (s != null) {
                            LOGGER.warn("Found duplicate key {}:{},will be overrided by new value{},config:{}", new Object[]{key, s, entry.getValue(), conf});
                        }
                        LOGGER.info("{}====>{}", key, entry.getValue().trim());
                        all.put(key, entry.getValue().trim());
                    }
                }
            }
        }
        return ImmutableMap.<String, String>builder().putAll(all).build();
    }
}
