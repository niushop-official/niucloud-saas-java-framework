package com.niu.core.event.member;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.member.Member;
import lombok.Data;

@Data
public class MemberRegisterEvent extends Event {

    private Member member;

}
