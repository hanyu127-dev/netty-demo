package com.hanyu.protocol;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 序列化与反序列化接口
 */
public interface Serializer {
    // 反序列化方法
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    // 序列化方法
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer{
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败",e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败",e);
                }
            }
        },
        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String josn = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(josn,clazz);
            }
            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }

    }

    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                String str = jsonElement.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
            // class to json
            return new JsonPrimitive(aClass.getName());
        }
    }
}
