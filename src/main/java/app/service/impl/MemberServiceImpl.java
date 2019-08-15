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
    MemberRepository repository;

    @Override
    public List<Member> findAllMembers() {
        return repository.findAll();
    }
}
