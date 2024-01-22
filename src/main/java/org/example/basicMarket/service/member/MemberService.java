package org.example.basicMarket.service.member;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.basicMarket.dto.member.MemberDto;
import org.example.basicMarket.exception.MemberNotFoundException;
import org.example.basicMarket.repository.member.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto read(Long id){
        return MemberDto.toDto(memberRepository.findById(id).orElseThrow(MemberNotFoundException::new));
    }

    @Transactional
    public void delete(Long id){
        if(notExistsMember(id)) throw new MemberNotFoundException();
        memberRepository.deleteById(id); // 내부적으로 findByid(id) 실행후 delete(id)가 실행된다.
    }

    private boolean notExistsMember(Long id){
        return !memberRepository.existsById(id);
    }
}
