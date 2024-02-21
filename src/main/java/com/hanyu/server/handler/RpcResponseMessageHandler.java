package com.hanyu.server.handler;

import com.hanyu.message.RpcResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    // 序号， 结果
    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}",msg);
        int sequenceId = msg.getSequenceId();
        Promise<Object> promise =  PROMISE_MAP.remove(sequenceId);

        if (promise!=null){
            Object returnValue = msg.getReturnValue();
            Exception exceptionValue = msg.getExceptionValue();
            if (exceptionValue!=null){
                promise.setFailure(exceptionValue);
            }else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
