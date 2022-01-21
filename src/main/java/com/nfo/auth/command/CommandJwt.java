package com.nfo.auth.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandJwt {
    private String member_id;
    private String username;
    private String pw;
    private String role;
    private Long create_date;
    private Long expiration_date;
}
