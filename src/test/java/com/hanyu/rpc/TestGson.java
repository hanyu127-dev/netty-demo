package com.hanyu.rpc;

import com.google.gson.*;
import com.hanyu.protocol.Serializer;

import java.lang.reflect.Type;

public class TestGson {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        System.out.println(gson.toJson(String.class));
    }


}
