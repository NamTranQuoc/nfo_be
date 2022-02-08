package com.nfo.member;

import com.nfo.core.utils.ResponseUtils;
import com.nfo.member.command.UpdateMemberCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/member/get_by_id/{id}")
    public String getById(@PathVariable String id) {
        try {
            return this.outJson(9999, null, userApplication.mongoDBConnection.getById(id).orElse(null));
        } catch (Throwable throwable) {
            return this.outJson(-9999, throwable.getMessage(), null);
        }
    }

    @PutMapping(value = "/member/update")
    public String update(UpdateMemberCommand command) {
        try {
            return this.outJson(9999, null, userApplication.update(command).orElse(null));
        } catch (Throwable throwable) {
            return this.outJson(-9999, throwable.getMessage(), null);
        }
    }
}
