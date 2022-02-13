package com.nfo.category;

import com.nfo.core.utils.ResponseUtils;
import com.nfo.member.Member;
import com.nfo.member.MemberApplication;
import com.nfo.member.command.UpdateMemberCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController(value = "/category")
public class CategoryController extends ResponseUtils {
    @Autowired
    private CategoryApplication categoryApplication;

    @PostMapping(value = "/category/add")
    public String add(@RequestBody Category category) {
        try {
            return this.outJson(9999, null, categoryApplication.add(category).orElse(null));
        } catch (Throwable throwable) {
            return this.outJson(-9999, throwable.getMessage(), null);
        }
    }
}
