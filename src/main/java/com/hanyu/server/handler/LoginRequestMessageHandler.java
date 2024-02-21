package com.hanyu.server.handler;

import com.hanyu.message.LoginRequestMessage;
import com.hanyu.message.LoginResponseMessage;
import com.hanyu.server.service.UserServiceFactory;
import com.hanyu.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        log.debug("{},{}",username,password);
        boolean login = UserServiceFactory.getUserService().login(username, password);

        LoginResponseMessage message;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登陆成功");

        } else {
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(message);
    }
}
