package com.hanyu.server.handler;

import com.hanyu.message.GroupCreateRequestMessage;
import com.hanyu.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
@ChannelHandler.Sharable
@Slf4j
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        log.debug("{}",groupName);

        GroupSessionFactory.getGroupSession().createGroup(groupName,members);
    }
}
