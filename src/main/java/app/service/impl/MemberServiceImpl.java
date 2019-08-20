package app.service.impl;

import app.model.Member;
import app.repository.MemberRepository;
import app.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository repository;

    @Override
    public List<Member> findAllMembers() {
        return repository.findAll();
    }

    @Override
    public void save(Member member) {
        repository.saveAndFlush(member);
    }

    @Override
    public void delete(Member member) {
        repository.delete(member);
    }

    @Override
    public Member findMemberById(long id) {
        return repository.findMemberById(id);
    }

    @Override
    public void update(Member member) {
       repository.update(member.getId(),member.getLastName(),member.getName(),member.getSecondName(),member.getOffice(),member.getBoss(),member.getCategory());
    }
}
