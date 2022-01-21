package com.example.nfo_be.member;

import com.example.nfo_be.core.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController(value = "/member")
public class MemberResource extends ResponseUtils {
    @Autowired
    private MemberService memberService;

    @PostMapping(value = "/member")
    public String add(@RequestBody Member member) {
        try {
            return this.outJson(9999, null, memberService.add(member).orElse(null));
        } catch (Throwable throwable) {
            return this.outJson(-9999, throwable.getMessage(), null);
        }
    }
}
