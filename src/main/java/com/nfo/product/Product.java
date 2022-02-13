package com.nfo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import eu.dozd.mongo.annotation.Entity;
import eu.dozd.mongo.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    ObjectId _id;
    private String name;
    private List<String> images;
    private String category_id;
    private String description;
    @Builder.Default
    private String status = MemberStatus.ACTIVE;

    public static class MemberStatus {
        public final static String INACTIVE = "INACTIVE";
        public final static String ACTIVE = "ACTIVE";
        public final static String BLOCK = "BLOCK";
    }
}
