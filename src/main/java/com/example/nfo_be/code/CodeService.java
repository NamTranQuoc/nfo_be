package com.example.nfo_be.code;

import com.example.nfo_be.core.utils.MongoDBConnection;
import com.example.nfo_be.core.utils.enums.MongodbEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CodeService {
    public final MongoDBConnection<Code> mongoDBConnection;

    @Autowired
    public CodeService() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_code, Code.class);
    }

    public String generateCodeByType(String type) {
        Map<String, Object> query = new HashMap<>();
        query.put("name", type);
        Optional<Code> optional = mongoDBConnection.findOne(query);
        Code code;
        if (optional.isPresent()) {
            code = optional.get();
            code.setCurrent_number(code.getCurrent_number() + 1L);
            mongoDBConnection.update(code.get_id().toHexString(), code);
        } else {
            code = Code.builder()
                    .name(type)
                    .prefix(String.format("%s-", type))
                    .build();
            mongoDBConnection.insert(code);
        }
        return String.format("%s%08d", code.getPrefix(), code.getCurrent_number());
    }
}
