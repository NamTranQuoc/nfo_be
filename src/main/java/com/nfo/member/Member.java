package com.nfo.member;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import eu.dozd.mongo.annotation.Embedded;
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
public class Member implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    ObjectId _id;
    private String name;
    private String code;
    @Builder.Default
    private Long create_date = System.currentTimeMillis();
    @Builder.Default
    private String type = MemberType.CUSTOMER;
    private String email;
    private String avatar;
    private String phone_number;
    private String gender;
    @Builder.Default
    private String status = MemberStatus.ACTIVE;

    public static class MemberType {
        public final static String ADMIN = "ADMIN";
        public final static String CUSTOMER = "CUSTOMER";
    }

    public static class MemberStatus {
        public final static String ACTIVE = "ACTIVE";
        public final static String BLOCK = "BLOCK";
    }
}
