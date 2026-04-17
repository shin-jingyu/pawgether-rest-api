package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.*;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.usecase.post.CountPostsUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/petfairs")
public class PetFairQueryApi {

    private final ReadPostByIdUseCase readPostByIdUseCase;
    private final CountPostsUseCase countPostsUseCase;
    private final ReadPostsUseCase readPostsUseCase;

    @GetMapping("/{petfairId}")
    public DetailPetFairResponse readDetailPetFair(@PathVariable("petfairId")Long petFairId) {
        return readPostByIdUseCase.readDetailPetFair(petFairId);
    }

    @GetMapping("/counts/{filterStatus}")
    public PetFairCountByStatusResponse countPetFairByFilterStatus(@PathVariable("filterStatus") PetFairFilterStatus filterStatus) {
        return countPostsUseCase.countActiveByFilterStatus(filterStatus);
    }

    @GetMapping
    public SummaryPetFairWithCursorResponse findPetFairs(@RequestParam(required = false) PetFairFilterStatus filterStatus,
                                                         @RequestParam(required = false) String keyword,
                                                         @ModelAttribute Cursor cursor) {
        if (keyword != null && !keyword.isBlank()) {
            return readPostsUseCase.findPetFairsByCondition(new ConditionRequest(keyword, cursor));
        }
        if (filterStatus != null) {
            return readPostsUseCase.findPetFairsByFilterStatus(filterStatus, cursor);
        }
        return readPostsUseCase.findAllPetFairs(cursor);
    }
}
