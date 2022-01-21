package com.nfo.core.security;

import com.nfo.auth.AuthApplication;
import com.nfo.auth.command.CommandJwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String sAuth) throws UsernameNotFoundException {
        AuthApplication authApplication = new AuthApplication();
        CommandJwt commandJwt = authApplication.decodeJwt(sAuth).get();

        return org.springframework.security.core.userdetails.User
                .withUsername(commandJwt.getUsername())
                .password(commandJwt.getPw())
                .authorities(commandJwt.getRole())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
