package com.vipkid.redis.serializer.support;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.vipkid.redis.serializer.ISerializer;
import com.vipkid.redis.serializer.SerializerType;


/**
 * 类描述 具体的序列化实现
 * Date: 2014/12/20
 * Time: 15:37
 */
public class ProtobuffSerializer implements ISerializer {

    private final SerializerType serializerType;

    public ProtobuffSerializer(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    public SerializerType getSerializerType() {
        return this.serializerType;
    }

    @Override
    public byte[] encode(Object object) {
        if (object == null) {
            return new byte[0];
        }
        if (!(object instanceof Message)) {
            throw new IllegalArgumentException(
                    "The obj must be a com.google.protobuf.Message" + object);
        }
        return ((Message) object).toByteArray();
    }

    @Override
    public Object decode(byte[] data) {
        throw new UnsupportedOperationException("Please use decode(Message,byte[]) instead");
    }

    @Override
    public <T> T decode(byte[] data, Class<T> clazz) {
        throw new UnsupportedOperationException("Please use decode(Message,byte[]) instead");
    }

    @Override
    public Object decode(byte[] data, Object instanceCreator) {
        if (data == null || data.length == 0) {
            return null;
        }
        if (!(instanceCreator instanceof Message)) {
            throw new IllegalArgumentException("Except a " + Message.class + " object");
        }
        try {
            return ((Message) instanceCreator).newBuilderForType().mergeFrom(data).build();
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Can't decode for type " + instanceCreator.getClass(), e);
        }
    }
}
