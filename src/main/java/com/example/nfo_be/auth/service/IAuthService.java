package com.example.nfo_be.auth.service;

import com.example.nfo_be.auth.Auth;
import com.example.nfo_be.auth.command.CommandChangePassword;
import com.example.nfo_be.auth.command.CommandJwt;
import com.example.nfo_be.auth.command.CommandLogin;
import com.example.nfo_be.auth.command.CommandSignInWithGoogle;
import com.example.nfo_be.member.Member;

import java.util.Optional;

public interface IAuthService {
    Optional<Auth> add(Member member) throws Exception;

    Boolean checkJwt(String jwt);

    Optional<String> login(CommandLogin command) throws Exception;

    Optional<String> signInWithGoogle(CommandSignInWithGoogle command) throws Exception;

    Optional<CommandJwt> decodeJwt(String jwt);

    Optional<Boolean> resetPassword(CommandChangePassword command) throws Exception;

    Optional<Boolean> requestForgetPassword(String email) throws Exception;

    Optional<Boolean> forgetPassword(CommandChangePassword command) throws Exception;

    Optional<Boolean> changePassword(CommandChangePassword command) throws Exception;
}
