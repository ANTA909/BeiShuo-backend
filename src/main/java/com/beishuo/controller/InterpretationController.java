package com.beishuo.controller;

import com.beishuo.common.JwtUtil;
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
@RequestMapping("/interpretation")
public class InterpretationController {

    private static final Logger logger = LoggerFactory.getLogger(InterpretationController.class);

    @Autowired
    private JwtUtil jwtUtil;

    // TODO: 注入InterpretationService
    // @Autowired
    // private InterpretationService interpretationService;

    /**
     * 生成AI阐释
     */
    @PostMapping("/generate")
    public Result<Map<String, Object>> generateInterpretation(@RequestBody Map<String, Object> request) {
        Long inscriptionId = request.get("inscriptionId") != null ?
                ((Number) request.get("inscriptionId")).longValue() : null;
        String text = (String) request.get("text");
        String dynasty = (String) request.get("dynasty");

        if (text == null || text.isEmpty()) {
            return Result.error(ResultCode.BAD_REQUEST, "碑文内容不能为空");
        }

        // TODO: 调用InterpretationService生成阐释
        // Map<String, Object> interpretation = interpretationService.generateInterpretation(
        //     inscriptionId, text, dynasty);
        // return Result.success(interpretation);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "AI阐释功能待实现");
    }

    /**
     * AI对话
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        Long inscriptionId = request.get("inscriptionId") != null ?
                ((Number) request.get("inscriptionId")).longValue() : null;
        String question = (String) request.get("question");
        List<String> context = (List<String>) request.get("context");

        if (question == null || question.isEmpty()) {
            return Result.error(ResultCode.BAD_REQUEST, "问题不能为空");
        }

        // TODO: 调用InterpretationService进行对话
        // String answer = interpretationService.chat(inscriptionId, question, context);
        // Map<String, Object> result = new HashMap<>();
        // result.put("answer", answer);
        // return Result.success(result);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "AI对话功能待实现");
    }

    /**
     * 获取阐释详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getInterpretationDetail(@PathVariable Long id) {
        // TODO: 调用InterpretationService获取阐释详情
        // Map<String, Object> detail = interpretationService.getInterpretationDetail(id);
        // return Result.success(detail);

        return Result.error(ResultCode.INTERNAL_SERVER_ERROR, "详情查询功能待实现");
    }
}

