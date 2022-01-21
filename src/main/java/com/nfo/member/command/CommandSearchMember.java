package com.nfo.member.command;

import com.nfo.core.utils.Sort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommandSearchMember {
    private String member_type;
    private Integer size;
    private Integer page;
    private String keyword;
    private Long from_date;
    private Long to_date;
    private Sort sort;
}
