package com.tripplannerai.service.keep;

import com.tripplannerai.common.exception.destination.NotFoundDDestinationException;
import com.tripplannerai.common.exception.keep.NotFoundKeepException;
import com.tripplannerai.common.exception.member.NotFoundMemberException;
import com.tripplannerai.dto.response.keep.*;
import com.tripplannerai.entity.destination.Destination;
import com.tripplannerai.entity.keep.Keep;
import com.tripplannerai.entity.member.Member;
import com.tripplannerai.repository.destination.DestinationRepository;
import com.tripplannerai.repository.keep.KeepRepository;
import com.tripplannerai.repository.member.MemberRepository;
import com.tripplannerai.util.ConstClass;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tripplannerai.util.ConstClass.*;

@Service
@RequiredArgsConstructor
public class KeepService {
    private final MemberRepository memberRepository;
    private final DestinationRepository destinationRepository;
    private final KeepRepository keepRepository;

    public CreateKeepResponse createKeep(String contentId, Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Destination destination = destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new NotFoundDDestinationException("not found Destination!!"));
        Keep keep = Keep.of(destination,member);
        keepRepository.save(keep);
        return CreateKeepResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public DeleteKeepResponse deleteKeep(String contentId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new NotFoundDDestinationException("not found Destination!!"));
        Keep keep = keepRepository.findByMemberAndDestination(contentId,id)
                .orElseThrow(()-> new NotFoundKeepException("Not Found Keep!!"));
        keepRepository.delete(keep);
        return DeleteKeepResponse.of(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public KeepsResponse keeps(Long id,Integer pageNum, Integer pageSize) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        Pageable pageable = PageRequest.of(pageNum-1,pageSize);
        Page<KeepElement> page = keepRepository.fetchKeeps(id,pageable);
        List<KeepElement> keeps = page.getContent();
        boolean hasNext = page.hasNext();
        return new KeepsResponse(SUCCESS_CODE,SUCCESS_MESSAGE,keeps,hasNext);
    }

    public DetailKeepResponse detailKeep(String contentId, Long id) {
        memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("not found Member!!"));
        destinationRepository.findByContentId(contentId)
                .orElseThrow(() -> new NotFoundDDestinationException("not found Destination!!"));
        KeepElement keepElement = keepRepository.fetchKeep(contentId,id);
        return DetailKeepResponse.of(SUCCESS_CODE,SUCCESS_MESSAGE,keepElement);
    }
}
