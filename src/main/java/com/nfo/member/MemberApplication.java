package com.nfo.member;

import com.nfo.auth.AuthApplication;
import com.nfo.code.CodeApplication;
import com.nfo.core.utils.MongoDBConnection;
import com.nfo.core.utils.enums.ExceptionEnum;
import com.nfo.core.utils.enums.MongodbEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MemberApplication {
    public final MongoDBConnection<Member> mongoDBConnection;
    @Autowired
    public MemberApplication() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_member, Member.class);
    }

    @Autowired
    private CodeApplication codeApplication;
    @Autowired
    private AuthApplication authApplication;

    public Optional<List<Member>> find(Map<String, Object> query) {
        query.put("is_deleted", false);
        return mongoDBConnection.find(query);
    }

    public Optional<Member> add(Member member) throws Exception {
        if (StringUtils.isAnyBlank(member.getName(), member.getEmail())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("is_deleted", false);
        query.put("email", member.getEmail());
        long count = mongoDBConnection.count(query).orElse(0L);
        if (count > 0) {
            throw new Exception(ExceptionEnum.member_exist);
        }
        if (StringUtils.isNotBlank(member.getPhone_number())) {
            Map<String, Object> query1 = new HashMap<>();
            query1.put("is_deleted", false);
            query1.put("phone_number", member.getPhone_number());
            long count1 = mongoDBConnection.count(query1).orElse(0L);
            if (count1 > 0) {
                throw new Exception(ExceptionEnum.phone_number_used);
            }
        }

        String code = codeApplication.generateCodeByType(member.getType());
        member.setCode(code);
        Optional<Member> optional = mongoDBConnection.insert(member);
        if (optional.isPresent()) {
//            optional.get().setAvatar("avatar-" + optional.get().get_id().toHexString() + ".png");
//            mongoDBConnection.update(optional.get().get_id().toHexString(), optional.get());
            authApplication.add(optional.get());
            return optional;
        }
        return Optional.empty();
    }

    public Optional<Member> getByEmail(String email) {
        Map<String, Object> query = new HashMap<>();
        query.put("is_deleted", false);
        query.put("email", email);
        return mongoDBConnection.findOne(query);
    }
}
