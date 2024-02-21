package com.hanyu.server.handler;

import com.hanyu.message.RpcRequestMessage;
import com.hanyu.message.RpcResponseMessage;
import com.hanyu.server.service.HelloService;
import com.hanyu.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        System.out.println("=============");
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());

        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());

            response.setReturnValue(invoke);
        } catch (Exception e){
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错"+e.getCause().getMessage()));
        }

        log.debug("{}",response);

        ctx.writeAndFlush(response);

    }


}
