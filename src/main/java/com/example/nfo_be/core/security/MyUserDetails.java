package com.example.nfo_be.core.security;

import com.example.nfo_be.auth.service.AuthService;
import com.example.nfo_be.auth.service.IAuthService;
import com.example.nfo_be.auth.command.CommandJwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String sAuth) throws UsernameNotFoundException {
        IAuthService authApplication = new AuthService();
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
