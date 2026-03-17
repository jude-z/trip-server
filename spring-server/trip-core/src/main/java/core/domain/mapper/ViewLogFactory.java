package core.domain.mapper;

import core.domain.entity.destination.Destination;
import core.domain.entity.member.Member;
import core.domain.entity.viewlog.ViewLog;

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
