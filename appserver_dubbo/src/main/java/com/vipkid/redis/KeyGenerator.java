package com.vipkid.redis;

/**
 * Created by davieli on 2015/6/8.
 */
public class KeyGenerator {
    private KeyGenerator(){};
    public static String generateKey(String keyPrefix, String key) {
        return keyPrefix + "&" + key;
    }
    public static String prefixPermissions = "permissions";
}
