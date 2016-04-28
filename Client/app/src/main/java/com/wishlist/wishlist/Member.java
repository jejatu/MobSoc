package com.wishlist.wishlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by honey on 4/28/2016.
 */
public class Member {
    private String memberName;
    private String memberId;

    public Member(String memberName, String memberId) {
        this.memberName = memberName;
        this.memberId = memberId;
    }

    public static List<Member> getDummyMembers(){

        List<Member> memberList= new ArrayList<Member>();
        Member member=new Member("Antti", "1");
        Member member1=new Member("Ali", "2");
        Member member2=new Member("honey", "3");
        Member member3=new Member("Jere", "4");
        memberList.add(member);
        memberList.add(member1);
        memberList.add(member2);
        memberList.add(member3);
        return memberList;
    }
    public Member() {
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
