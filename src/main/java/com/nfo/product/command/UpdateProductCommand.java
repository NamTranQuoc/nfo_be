package com.nfo.product.command;

import com.nfo.product.ProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductCommand {
    public String id;
    public String name;
    public String description;
    public String status;
    private List<String> images;
    private List<String> categories;
    private List<ProductType> product_types;
    private String address;
    private String phone;
}
