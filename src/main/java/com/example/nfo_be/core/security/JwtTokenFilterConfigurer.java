package com.example.nfo_be.core.security;

import com.example.nfo_be.auth.service.AuthService;
import com.example.nfo_be.auth.service.IAuthService;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    public JwtTokenFilterConfigurer() {
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        IAuthService authApplication = new AuthService();
        JwtTokenFilter customFilter = new JwtTokenFilter(authApplication);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
