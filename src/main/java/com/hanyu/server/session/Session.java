package com.hanyu.server.session;

import io.netty.channel.Channel;

/*
 会话管理接口
 */
public interface Session {
    /**
     * 绑定会话
     * @param channel 哪个channel
     * @param username 绑定的用户
     */
    void bind(Channel channel,String username);

    /**
     * 解除绑定
     * @param channel 解除
     */
    void unbind(Channel channel);

    Object getAttribute(Channel channel, String name);
    void setAttribute(Channel channel,String name,Object value);

    Channel getChannel(String username);
}
