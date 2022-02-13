package com.nfo.category;

import com.nfo.core.firebase.FirebaseFileService;
import com.nfo.core.utils.MongoDBConnection;
import com.nfo.core.utils.enums.ExceptionEnum;
import com.nfo.core.utils.enums.MongodbEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CategoryApplication {
    public final MongoDBConnection<Category> mongoDBConnection;

    @Autowired
    public CategoryApplication() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_category, Category.class);
    }

    @Autowired
    private FirebaseFileService firebaseFileService;

    public Optional<Category> add(Category category) throws Exception {
        if (StringUtils.isBlank(category.getName())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }

        Map<String, Object> query = new HashMap<>();
        query.put("name", category.getName());
        long count = mongoDBConnection.count(query).orElse(0L);
        if (count > 0) {
            throw new Exception(ExceptionEnum.category_exist);
        }

        Optional<Category> insert = mongoDBConnection.insert(category);
        if (insert.isPresent()) {
            String id = insert.get().get_id().toHexString();
            insert.get().setImage(firebaseFileService.getDownloadUrl(id + ".png", "categories"));
            return mongoDBConnection.update(id, insert.get());
        }
        return Optional.empty();
    }
}
