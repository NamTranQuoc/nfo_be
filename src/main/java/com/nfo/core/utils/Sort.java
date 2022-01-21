package com.nfo.core.utils;

import eu.dozd.mongo.annotation.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sort {
    private String field;
    private Boolean is_asc;
}
