package com.hanyu.server.handler;

import com.hanyu.message.GroupChatRequestMessage;
import com.hanyu.message.GroupChatResponseMessage;
import com.hanyu.server.session.GroupSessionFactory;
import com.hanyu.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String content = msg.getContent();
        String groupName = msg.getGroupName();
        Set<String> members = GroupSessionFactory.getGroupSession().getMembers(groupName);
        for (String member : members) {
            Channel channel = SessionFactory.getSession().getChannel(member);
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(),content));
        }
    }
}
