package com.vipkid.redis.serializer.support;


import com.vipkid.redis.serializer.ISerializer;
import com.vipkid.redis.serializer.SerializerType;

import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * 类描述
 * Date: 2014/12/20
 * Time: 15:36
 */
public class JavaSerializerImpl implements ISerializer {

    private final SerializerType serializerType;

    public JavaSerializerImpl(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    public SerializerType getSerializerType() {
        return this.serializerType;
    }

    @Override
    public byte[] encode(Object object) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(byteArray);
            output.writeObject(object);
            output.flush();
            return byteArray.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    @Override
    public Object decode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(new ByteArrayInputStream(data));
            return objectIn.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(objectIn);
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T decode(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        ObjectInputStream objectIn = null;
        try {
            objectIn = new ObjectInputStream(new ByteArrayInputStream(data));
            return (T) objectIn.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(objectIn);
        }
    }

    @Override
    public Object decode(byte[] data, Object instanceCreator) {
        throw new UnsupportedOperationException("not support method");
    }
}
