package core.service.keep;

import core.api.response.ApiResponse;
import core.api.response.ApiStatusResponse;
import core.api.response.ApiDataResponse;
import core.common.Status;
import core.common.exception.CommonException;
import core.domain.entity.destination.Destination;
import core.domain.entity.keep.Keep;
import core.domain.entity.member.Member;
import core.infra.jpa.destination.DestinationRepository;
import core.infra.jpa.keep.KeepRepository;
import core.infra.jpa.member.MemberRepository;
import core.infra.projection.keep.KeepElement;
import core.infra.querydsl.keep.QueryDslKeepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeepService {
    private final MemberRepository memberRepository;
    private final DestinationRepository destinationRepository;
    private final KeepRepository keepRepository;
    private final QueryDslKeepRepository queryDslKeepRepository;

    public ApiResponse createKeep(String contentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Destination destination = destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_DESTINATION));
        Keep keep = Keep.of(destination,member);
        keepRepository.save(keep);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse deleteKeep(String contentId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_DESTINATION));
        Keep keep = queryDslKeepRepository.findByMemberAndDestination(contentId,id)
                .orElseThrow(()-> new CommonException(Status.NOT_FOUND_KEEP));
        keepRepository.delete(keep);
        return ApiStatusResponse.of(Status.SUCCESS);
    }

    public ApiResponse keeps(Long id, Integer pageNum, Integer pageSize) {
        memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);
        Page<KeepElement> page = queryDslKeepRepository.fetchKeeps(id,pageable);
        List<KeepElement> keeps = page.getContent();
        boolean hasNext = page.hasNext();
        return ApiDataResponse.of(Map.of("keeps", keeps, "hasNext", hasNext), Status.SUCCESS);
    }

    public ApiResponse detailKeep(String contentId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_MEMBER));
        destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new CommonException(Status.NOT_FOUND_DESTINATION));
        KeepElement keepElement = queryDslKeepRepository.fetchKeep(contentId,id);
        return ApiDataResponse.of(keepElement, Status.SUCCESS);
    }
}
