package org.example.basicMarket.factory.entity;

import org.example.basicMarket.entity.member.Member;
import org.example.basicMarket.entity.message.Message;

import static org.example.basicMarket.factory.entity.MemberFactory.createMember;

public class MessageFactory {

    public static Message createMessage() {
        return new Message("content", createMember(), createMember());
    }

    public static Message createMessage(Member sender, Member receiver) {
        return new Message("content", sender, receiver);
    }
}
