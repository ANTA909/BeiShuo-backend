package com.beishuo.controller;

import com.beishuo.common.JwtUtil;
import com.beishuo.common.PageResult;
import com.beishuo.common.Result;
import com.beishuo.common.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/knowledge")
public class KnowledgeController {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeController.class);

    @Autowired
    private JwtUtil jwtUtil;

    // TODO: 注入KnowledgeService
    // @Autowired
    // private KnowledgeService knowledgeService;

    /**
     * 获取知识库列表
     */
    @GetMapping("/list")
    public Result<PageResult<Map<String, Object>>> getKnowledgeList(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String category) {

        // TODO: 调用KnowledgeService获取列表
        // PageResult<Map<String, Object>> result = knowledgeService.getKnowledgeList(
        //     page, size, keyword, dynasty, category);
        // return Result.success(result);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "列表查询功能待实现");
    }

    /**
     * 获取知识库详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getKnowledgeDetail(@PathVariable Long id) {
        // TODO: 调用KnowledgeService获取详情
        // Map<String, Object> detail = knowledgeService.getKnowledgeDetail(id);
        // return Result.success(detail);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "详情查询功能待实现");
    }

    /**
     * 搜索知识库
     */
    @GetMapping("/search")
    public Result<List<Map<String, Object>>> searchKnowledge(
            @RequestParam String keyword,
            @RequestParam(required = false) String dynasty,
            @RequestParam(required = false) String tags,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // TODO: 调用KnowledgeService搜索知识库
        // List<Map<String, Object>> results = knowledgeService.searchKnowledge(
        //     keyword, dynasty, tags, page, size);
        // return Result.success(results);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "搜索功能待实现");
    }

    /**
     * 收藏知识库
     */
    @PostMapping("/{id}/favorite")
    public Result<?> favoriteKnowledge(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error(ResultCode.UNAUTHORIZED);
        }

        // TODO: 调用KnowledgeService收藏知识库
        // knowledgeService.favoriteKnowledge(id, userId);
        // return Result.success();

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "收藏功能待实现");
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{id}/favorite")
    public Result<?> unfavoriteKnowledge(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            return Result.error(ResultCode.UNAUTHORIZED);
        }

        // TODO: 调用KnowledgeService取消收藏
        // knowledgeService.unfavoriteKnowledge(id, userId);
        // return Result.success();

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "取消收藏功能待实现");
    }

    /**
     * 推荐知识库
     */
    @GetMapping("/recommend")
    public Result<List<Map<String, Object>>> getRecommendKnowledge(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // TODO: 调用KnowledgeService获取推荐
        // List<Map<String, Object>> results = knowledgeService.getRecommendKnowledge(page, size);
        // return Result.success(results);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "推荐功能待实现");
    }

    /**
     * 最新收录
     */
    @GetMapping("/latest")
    public Result<List<Map<String, Object>>> getLatestKnowledge(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        // TODO: 调用KnowledgeService获取最新收录
        // List<Map<String, Object>> results = knowledgeService.getLatestKnowledge(page, size);
        // return Result.success(results);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "最新收录功能待实现");
    }

    /**
     * 从Token中获取用户ID
     */
    private Long getUserIdFromToken(String authHeader) {
        if (authHeader == null) {
            return null;
        }
        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.validateToken(token)) {
            return null;
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}

