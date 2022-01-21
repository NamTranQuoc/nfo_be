package com.example.nfo_be.member;

import com.example.nfo_be.auth.service.AuthService;
import com.example.nfo_be.auth.service.IAuthService;
import com.example.nfo_be.core.utils.MongoDBConnection;
import com.example.nfo_be.core.utils.enums.MongodbEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberService {
    public final MongoDBConnection<Member> mongoDBConnection;

    @Autowired
    public MemberService() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_member, Member.class);
    }

//    @Autowired
//    private IAuthService authService;

    public Optional<Member> add(Member member) throws Exception {
        Optional<Member> memberOptional = mongoDBConnection.insert(member);
        if (memberOptional.isPresent()) {
//            authService.add(memberOptional.get());
            return memberOptional;
        }
        return Optional.empty();
    }

    public Optional<Member> getByEmail(String email) {
        Map<String, Object> query = new HashMap<>();
        query.put("email", email);
        return mongoDBConnection.findOne(query);
    }
}
