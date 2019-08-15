package app.service;

import app.model.Member;

import java.util.List;

public interface MemberService {
    List<Member> findAllMembers();
}
