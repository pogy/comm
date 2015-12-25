package com.vipkid.redis.serializer;


/**
 * 类描述 序列化结果
 * Date: 2014/12/20
 * Time: 15:36
 */
public interface ISerializer {

    public SerializerType getSerializerType();

    /**
     * 将对象编码为byte数组
     *
     * @param object
     * @return
     */
    public byte[] encode(Object object);

    /**
     * 将byte数组解码为对象
     *
     * @param data
     * @return
     */
    public Object decode(byte[] data);

    /**
     * 将byte数组解码为对象
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T decode(byte[] data, Class<T> clazz);

    /**
     * 使用指定的实例帮助反序列化-只有Protobuff实现了才方法
     *
     * @param data
     * @param instanceCreator
     * @return
     */
    public Object decode(byte[] data, Object instanceCreator);
}
