package org.example.basicMarket.repository.message;

import org.example.basicMarket.dto.message.MessageSimpleDto;
import org.example.basicMarket.entity.message.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {

    @Query("select m from Message m left join fetch m.sender left join fetch m.receiver where m.id = :id")
    Optional<Message> findWithSenderAndReceiverById(@Param("id") Long id); // 1

    // Slice : 페이징 처리 결과에 대한 다양한 정보를 포함
    // 별도의 카운트 쿼리가 수행되지 않고, (Pageable의 지정된 크기 + 1)로 limit 절을 만들어주기 때문에, 다음 페이지가 아직 남아있는지 손쉽게 확인할 수 있습니다
    // 지정된 크기만큼 조회하는 것이 아니라 1건을 더 조회하기 때문에, 지정했던 크기와 조회 크기가 다르다면, 다음 페이지가 없다는 것을 바로 알 수 있습니다.
    @Query("select new org.example.basicMarket.dto.message.MessageSimpleDto(m.id, m.content, m.receiver.nickname, m.createdAt) " +
            "from Message m left join m.receiver " +
            "where m.sender.id = :senderId and m.id < :lastMessageId and m.deletedBySender = false order by m.id desc")
    Slice<MessageSimpleDto> findAllBySenderIdOrderByMessageIdDesc(@Param("senderId")Long senderId,@Param("lastMessageId")Long lastMessageId, Pageable pageable);

    // 2
    @Query("select new org.example.basicMarket.dto.message.MessageSimpleDto(m.id, m.content, m.sender.nickname, m.createdAt) " +
            "from Message m left join m.sender " +
            "where m.receiver.id = :receiverId and m.id < :lastMessageId and m.deletedByReceiver = false order by m.id desc")
    Slice<MessageSimpleDto> findAllByReceiverIdOrderByMessageIdDesc(@Param("receiverId") Long receiverId,@Param("lastMessageId") Long lastMessageId, Pageable pageable);
}
