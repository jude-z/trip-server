package com.tripplannerai.mapper;

import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.viewlog.ViewLog;

import java.time.LocalDateTime;

public class ViewLogFactory {
    public static ViewLog from(Destination destination, Member member) {
        ViewLog viewLog = new ViewLog();
        viewLog.setDestination(destination);
        viewLog.setMember(member);
        viewLog.setViewLogDate(LocalDateTime.now());
        return viewLog;
    }
}
