package org.example.basicMarket.entity.message;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.basicMarket.entity.common.EntityDate;
import org.example.basicMarket.entity.member.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends EntityDate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Lob
    private String content;

    @Column(nullable = false) // 1
    private boolean deletedBySender;

    @Column(nullable = false) // 1
    private boolean deletedByReceiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;

    public Message(String content, Member sender, Member receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        this.deletedBySender = this.deletedByReceiver = false;
    }

    public void deleteBySender() { // 2
        this.deletedBySender = true;
    }

    public void deleteByReceiver() { // 2
        this.deletedByReceiver = true;
    }

    public boolean isDeletable() { // 3
        return isDeletedBySender() && isDeletedByReceiver();
    }
}
