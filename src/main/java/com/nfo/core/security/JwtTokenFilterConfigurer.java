package com.nfo.core.security;

import com.nfo.auth.AuthApplication;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    public JwtTokenFilterConfigurer() {
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthApplication authApplication = new AuthApplication();
        JwtTokenFilter customFilter = new JwtTokenFilter(authApplication);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
