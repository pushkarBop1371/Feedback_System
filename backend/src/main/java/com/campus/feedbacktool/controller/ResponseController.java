package com.campus.feedbacktool.controller;

import com.campus.feedbacktool.dto.PageResponse;
import com.campus.feedbacktool.dto.ResponseDto;
import com.campus.feedbacktool.dto.ResponseRequest;
import com.campus.feedbacktool.service.ResponseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responses")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    // GET /api/responses?page=0&size=10
    @GetMapping
    public PageResponse<ResponseDto> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ResponseDto> result = responseService.list(pageable);
        return new PageResponse<>(result.getContent(), result);
    }

    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) {
        return responseService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto create(@Valid @RequestBody ResponseRequest request) {
        return responseService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @Valid @RequestBody ResponseRequest request) {
        return responseService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        responseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
