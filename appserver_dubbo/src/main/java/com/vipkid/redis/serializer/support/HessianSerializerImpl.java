package com.vipkid.redis.serializer.support;


import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.vipkid.redis.serializer.ISerializer;
import com.vipkid.redis.serializer.SerializerType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 类描述
 * User: chenggangxu@sohu-inc.com
 * Date: 2014/12/20
 * Time: 15:37
 */
public class HessianSerializerImpl implements ISerializer {
    private static final Logger log = LoggerFactory.getLogger(HessianSerializerImpl.class);

    private final SerializerType serializerType;

    public HessianSerializerImpl(SerializerType serializerType) {
        this.serializerType = serializerType;
    }

    @Override
    public SerializerType getSerializerType() {
        return this.serializerType;
    }

    @Override
    public byte[] encode(Object object) {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(byteArray);
        try {
            output.writeObject(object);
            output.flush();
            output.close();
            return byteArray.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                log.error("Close output error.", e);
            }
        }
    }

    @Override
    public Object decode(byte[] data) {
        final Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        try {
            return input.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                log.error("Close output error.", e);
            }
        }
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T decode(byte[] data, Class<T> clazz) {
        final Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        try {
            return (T) input.readObject(clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                log.error("Close output error.", e);
            }
        }
    }

    @Override
    public Object decode(byte[] data, Object instanceCreator) {
        throw new UnsupportedOperationException("not support method");
    }
}
