package core.domain.mapper;

import core.domain.entity.member.Member;
import core.domain.entity.searchlog.SearchLog;

import java.time.LocalDateTime;

public class SearchLogFactory {
    public static SearchLog from(String keyword, Member member){
        SearchLog searchLog = new SearchLog();
        searchLog.setKeyword(keyword);
        searchLog.setMember(member);
        searchLog.setSearchDate(LocalDateTime.now());
        return searchLog;
    }
}
