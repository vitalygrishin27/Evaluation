package app.service;

import app.model.Member;

import java.util.List;

public interface MemberService {
    List<Member> findAllMembers();

    void save(Member member);

   void delete(Member member);

   Member findMemberById(long id);

   void update(Member member);
}
