package com.example.idcard.controller;

import com.example.idcard.common.ApiResponse;
import com.example.idcard.dto.IdCardRequest;
import com.example.idcard.dto.IdCardResponse;
import com.example.idcard.dto.PageResponse;
import com.example.idcard.dto.StatsResponse;
import com.example.idcard.service.IdCardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 身份证管理接口，统一前缀 {@code /api/idcards}。
 */
@RestController
@RequestMapping("/api/idcards")
@Validated
public class IdCardController {

    private final IdCardService service;

    public IdCardController(IdCardService service) {
        this.service = service;
    }

    /** 分页 + 按姓名/身份证号模糊搜索。 */
    @GetMapping
    public ApiResponse<PageResponse<IdCardResponse>> list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") @Min(value = 1, message = "page 最小为 1") int page,
            @RequestParam(name = "size", defaultValue = "10")
            @Min(value = 1, message = "size 最小为 1")
            @Max(value = 100, message = "size 最大为 100") int size) {
        return ApiResponse.ok(service.page(keyword, page, size));
    }

    /** 统计：总数、男女数量与占比、年龄段分布。 */
    @GetMapping("/stats")
    public ApiResponse<StatsResponse> stats() {
        return ApiResponse.ok(service.stats());
    }

    /** 详情。 */
    @GetMapping("/{id}")
    public ApiResponse<IdCardResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(service.get(id));
    }

    /** 新增。 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<IdCardResponse> create(@Valid @RequestBody IdCardRequest request) {
        return ApiResponse.ok(service.create(request));
    }

    /** 修改。 */
    @PutMapping("/{id}")
    public ApiResponse<IdCardResponse> update(@PathVariable("id") Long id,
                                              @Valid @RequestBody IdCardRequest request) {
        return ApiResponse.ok(service.update(id, request));
    }

    /** 删除。 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ApiResponse.ok();
    }
}
