package com.nfo.product;

import com.nfo.category.Category;
import com.nfo.core.firebase.FirebaseFileService;
import com.nfo.core.utils.MongoDBConnection;
import com.nfo.core.utils.enums.ExceptionEnum;
import com.nfo.core.utils.enums.MongodbEnum;
import com.nfo.product.command.UpdateProductCommand;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class ProductApplication {
    public final MongoDBConnection<Product> mongoDBConnection;

    @Autowired
    public ProductApplication() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_product, Product.class);
    }

    @Autowired
    private FirebaseFileService firebaseFileService;

    public Optional<Product> add(Product product) throws Exception {
        if (StringUtils.isBlank(product.getName())
            || CollectionUtils.isEmpty(product.getProduct_types())
            || CollectionUtils.isEmpty(product.getCategories())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }

        for (ProductType productType: product.getProduct_types()) {
            if (StringUtils.isBlank(productType.getName()) || productType.getPrice() == null || productType.getQuantity() == null) {
                throw new Exception(ExceptionEnum.param_not_null);
            }
            if (productType.getQuantity() <= 0 || productType.getPrice() <= 0) {
                throw new Exception(ExceptionEnum.only_positive_numbers_are_allowed);
            }
        }
        return mongoDBConnection.insert(product);
    }

    public Optional<Product> update(UpdateProductCommand command) throws Exception {
        return Optional.empty();
    }
}
