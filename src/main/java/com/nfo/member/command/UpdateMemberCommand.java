package com.nfo.member.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemberCommand {
    private String id;
    private String name;
    private String gender;
    private String phone_number;
    private String avatar;
}
