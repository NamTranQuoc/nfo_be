package com.nfo.member;

import com.nfo.core.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController(value = "/member")
public class MemberController extends ResponseUtils {
    @Autowired
    private MemberApplication userApplication;

    @PostMapping(value = "/member/add")
    public String add(@RequestBody Member member) {
        try {
            return this.outJson(9999, null, userApplication.add(member).orElse(null));
        } catch (Throwable throwable) {
            return this.outJson(-9999, throwable.getMessage(), null);
        }
    }
}
