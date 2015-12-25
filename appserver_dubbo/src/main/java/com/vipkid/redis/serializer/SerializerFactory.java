package com.vipkid.redis.serializer;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.vipkid.redis.serializer.support.HessianSerializerImpl;
import com.vipkid.redis.serializer.support.JavaSerializerImpl;
import com.vipkid.redis.serializer.support.ProtobuffSerializer;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ConcurrentMap;

/**
 * 类描述：序列化工厂
 * Time: 16:05
 */
public class SerializerFactory {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final ConcurrentMap<SerializerType, ISerializer> CODECS = new MapMaker().makeMap();

    /**
     * Java内置的序列化编码
     */
    private static final ISerializer Java_Serializer = new JavaSerializerImpl(SerializerType.java);
    /**
     * 由Hessian提供的序列化编码机制
     */
    private static final ISerializer Hessian_Serializer = new HessianSerializerImpl(SerializerType.hessian);
    /**
     * 由Google Protobuffer提供的序列化编码机制
     */
    private static final ISerializer Protobuff_Serializer = new ProtobuffSerializer(SerializerType.protobuff);

    private SerializerFactory() {
    }

    //注册内置的编码解码器
    static {
        add(Java_Serializer);
        add(Hessian_Serializer);
        add(Protobuff_Serializer);
    }

    /**
     * 取得指定类型的编解码器
     *
     * @param type
     * @return
     */
    public static ISerializer getSerializer(SerializerType type) {
        return CODECS.get(type);
    }

    /**
     * 注册一个编解码器
     *
     * @param codec
     * @throws IllegalStateException 如果{@link ISerializer#getSerializerType()} 已经被注册,则会抛出此异常
     */
    public static void add(ISerializer codec) {
        Preconditions.checkNotNull(codec, "The codec must be set.");
        ISerializer preCodec = CODECS.putIfAbsent(codec.getSerializerType(), codec);
        Preconditions.checkState(preCodec == null, "The type  %s has been set with class %s", codec.getSerializerType(), preCodec);
    }

    /**
     * 是否需要实例帮助反序列化(例如Google proto buffer就需要对象实例反序列化)
     *
     * @param type
     * @return
     */
    public static boolean needInstanceCreator(SerializerType type) {
        return type == Protobuff_Serializer.getSerializerType();
    }

    /**
     * 按照UTF-8编码取得byte数组
     *
     * @param str
     * @return
     * @throws RuntimeException
     */
    public static byte[] getStringBytes(String str) {
        try {
            return str.getBytes(DEFAULT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Can't get bytes for [" + str + "] with charset [" + DEFAULT_CHARSET + "]", e);
        }
    }
}
