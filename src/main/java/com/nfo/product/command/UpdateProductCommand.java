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
    public String id;
    public String name;
    public String gender;
    public String phone_number;
    public String avatar;
}
