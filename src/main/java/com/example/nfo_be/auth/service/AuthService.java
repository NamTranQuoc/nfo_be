package com.example.nfo_be.auth.service;


import com.example.nfo_be.auth.Auth;
import com.example.nfo_be.auth.command.CommandChangePassword;
import com.example.nfo_be.auth.command.CommandJwt;
import com.example.nfo_be.auth.command.CommandLogin;
import com.example.nfo_be.auth.command.CommandSignInWithGoogle;
import com.example.nfo_be.code.CodeService;
import com.example.nfo_be.core.firebase.FirebaseFileService;
import com.example.nfo_be.core.mail.Mail;
import com.example.nfo_be.core.mail.MailService;
import com.example.nfo_be.core.thymeleaf.ThymeleafService;
import com.example.nfo_be.core.utils.Generate;
import com.example.nfo_be.core.utils.HashUtils;
import com.example.nfo_be.core.utils.MongoDBConnection;
import com.example.nfo_be.core.utils.enums.ExceptionEnum;
import com.example.nfo_be.core.utils.enums.MongodbEnum;
import com.example.nfo_be.member.Member;
import com.example.nfo_be.member.MemberService;
import com.example.nfo_be.member.enums.MemberStatus;
import com.example.nfo_be.member.enums.MemberType;
import com.google.gson.Gson;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuthService implements IAuthService {
    public final MongoDBConnection<Auth> mongoDBConnection;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ThymeleafService thymeleafService;
    @Autowired
    private CodeService codeService;
    @Autowired
    private FirebaseFileService firebaseFileService;
    @Autowired
    private MailService mailService;

    private final String JWT_SECRET = "UUhuhdadyh9*&^777687";
    private final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;

    @Autowired
    public AuthService() {
        mongoDBConnection = new MongoDBConnection<>(MongodbEnum.collection_auth, Auth.class);
    }

    @Override
    public Optional<Auth> add(Member member) throws Exception {
        String password = Generate.generateCommonLangPassword();
        Auth auth = Auth.builder()
                .member_id(member.get_id().toHexString())
                .password(HashUtils.getPasswordMD5(password))
                .username(member.getEmail())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("name", member.getName());
        data.put("username", member.getEmail());
        data.put("password", password);
        mailService.send(Mail.builder()
                .mail_to(member.getEmail())
                .mail_subject("Thư chào mừng!")
                .mail_content(thymeleafService.getContent("mailNewMember", data))
                .build());
        return mongoDBConnection.insert(auth);
    }

    @Override
    public Boolean checkJwt(String jwt) {
        return this.decodeJwt(jwt).isPresent();
    }

    private Optional<String> generateToken(Auth auth) throws Exception {
        Optional<Member> optional = memberService.mongoDBConnection.getById(auth.getMember_id());
        if (!optional.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        Member member = optional.get();
        long now = System.currentTimeMillis();
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        CommandJwt commandJwt = CommandJwt.builder()
                .create_date(now)
                .expiration_date(now + JWT_EXPIRATION)
                .member_id(member.get_id().toHexString())
                .role(member.getType())
                .pw(HashUtils.getPasswordMD5(auth.getPassword()))
                .username(auth.getUsername())
                .build();
        return Optional.of(Jwts.builder()
                .setHeader(header)
                .setPayload(new Gson().toJson(commandJwt))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact());
    }

    @Override
    public Optional<String> login(CommandLogin command) throws Exception {
        if (StringUtils.isAnyBlank(command.getUsername(), command.getPassword())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("$or", Arrays.asList(
                new Document("email", command.getUsername()),
                new Document("code", command.getUsername()),
                new Document("phone_number", command.getUsername())
        ));
        query.put("status", MemberStatus.ACTIVE);
        Optional<Member> member = memberService.mongoDBConnection.findOne(query);
        if (!member.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        Map<String, Object> queryAuth = new HashMap<>();
        queryAuth.put("username", member.get().getEmail());
        Optional<Auth> auth = mongoDBConnection.findOne(queryAuth);
        if (!auth.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        String hashPass = HashUtils.getPasswordMD5(command.getPassword());
        if (!hashPass.equals(auth.get().getPassword())) {
            throw new Exception(ExceptionEnum.password_incorrect);
        }
        return this.generateToken(auth.get());
    }

    @Override
    public Optional<String> signInWithGoogle(CommandSignInWithGoogle command) throws Exception {
        if (StringUtils.isAnyBlank(command.getEmail(), command.getName())) {
            throw new Exception(ExceptionEnum.cannot_connect);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("email", command.getEmail());
        query.put("status", MemberStatus.ACTIVE);
        Optional<Member> optional = memberService.mongoDBConnection.findOne(query);
        Member member = null;
        if (!optional.isPresent()) {
            member = Member.builder()
                    .name(command.getName())
                    .email(command.getEmail())
                    .build();

            String code = codeService.generateCodeByType(member.getType());
            member.setCode(code);
            Optional<Member> insert = memberService.mongoDBConnection.insert(member);
            if (!insert.isPresent()) {
                throw new Exception(ExceptionEnum.cannot_connect);
            }
            insert.get().setAvatar("avatar-" + insert.get().get_id().toHexString() + ".png");
            memberService.mongoDBConnection.update(insert.get().get_id().toHexString(), insert.get());
            this.add(insert.get());
            if (StringUtils.isNotBlank(command.getAvatar())) {
                try {
                    firebaseFileService.saveFromUrl(command.getAvatar(), "images/" + insert.get().getAvatar());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            member = optional.get();
        }
        Map<String, Object> queryAuth = new HashMap<>();
        queryAuth.put("username", member.getEmail());
        Optional<Auth> auth = mongoDBConnection.findOne(queryAuth);
        if (!auth.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        return this.generateToken(auth.get());
    }

    @Override
    public Optional<CommandJwt> decodeJwt(String jwt) {
        try {
            CommandJwt commandJwt = new Gson().fromJson(Jwts.parser().setSigningKey(JWT_SECRET).parse(jwt).getBody().toString(), CommandJwt.class);
            return Optional.of(commandJwt);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> resetPassword(CommandChangePassword command) throws Exception{
        if (!MemberType.ADMIN.equals(command.getRole())) {
            throw new Exception(ExceptionEnum.member_type_deny);
        }
        if (StringUtils.isAnyBlank(command.getUsername(), command.getNew_password())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        return mongoDBConnection.update(
                new Document("username", command.getUsername()),
                new Document("$set", new Document("password", HashUtils.getPasswordMD5(command.getNew_password()))));
    }

    @Override
    public Optional<Boolean> requestForgetPassword(String email) throws Exception {
        if (StringUtils.isBlank(email)) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        Optional<Member> optional = memberService.getByEmail(email);
        if (!optional.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        Member member = optional.get();
        long now = System.currentTimeMillis();
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        CommandJwt commandJwt = CommandJwt.builder()
                .create_date(now)
                .expiration_date(now + 900000)
                .member_id(member.get_id().toHexString())
                .role(member.getType())
                .pw(HashUtils.getPasswordMD5(Generate.generateCommonLangPassword()))
                .username(email)
                .build();
        String token = Jwts.builder()
                .setHeader(header)
                .setPayload(new Gson().toJson(commandJwt))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        Map<String, Object> data = new HashMap<>();
        data.put("name", member.getName());
        data.put("url", String.format("%s%s", "https://englishcenter-2021.web.app/forget_password/", token));
        mailService.send(Mail.builder()
                .mail_to(email)
                .mail_subject("Khôi phục mật khẩu!")
                .mail_content(thymeleafService.getContent("mailForgetPassword", data))
                .build());
        return Optional.of(Boolean.TRUE);
    }

    @Override
    public Optional<Boolean> forgetPassword(CommandChangePassword command) throws Exception {
        if (StringUtils.isAnyBlank(command.getCurrent_id(), command.getConfirm_password(), command.getNew_password())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        if (!command.getNew_password().equals(command.getConfirm_password())) {
            throw new Exception(ExceptionEnum.confirm_password_incorrect);
        }
        Map<String, Object> query = new HashMap<>();
        query.put("member_id", command.getCurrent_id());
        return mongoDBConnection.update(query, new Document("$set", new Document("password", HashUtils.getPasswordMD5(command.getNew_password()))));
    }

    @Override
    public Optional<Boolean> changePassword(CommandChangePassword command) throws Exception {
        if (StringUtils.isAnyBlank(command.getCurrent_id(), command.getConfirm_password(), command.getNew_password(), command.getOld_password())) {
            throw new Exception(ExceptionEnum.param_not_null);
        }
        Optional<Auth> optional = mongoDBConnection.findOne(new Document("member_id", command.getCurrent_id()));
        if (!optional.isPresent()) {
            throw new Exception(ExceptionEnum.member_not_exist);
        }
        Auth auth = optional.get();
        if (!HashUtils.getPasswordMD5(command.getOld_password()).equals(auth.getPassword())) {
            throw new Exception(ExceptionEnum.password_incorrect);
        }
        if (!command.getNew_password().equals(command.getConfirm_password())) {
            throw new Exception(ExceptionEnum.confirm_password_incorrect);
        }
        auth.setPassword(HashUtils.getPasswordMD5(command.getNew_password()));
        return Optional.of(mongoDBConnection.update(auth.get_id().toHexString(), auth).isPresent());
    }
}
