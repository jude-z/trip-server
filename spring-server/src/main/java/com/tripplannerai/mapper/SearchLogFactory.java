package com.tripplannerai.mapper;

import com.tripplannerai.entity.member.Member;
import com.tripplannerai.entity.searchlog.SearchLog;

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
