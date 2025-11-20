package com.beishuo.controller;

import com.beishuo.common.JwtUtil;
import com.beishuo.common.Result;
import com.beishuo.common.ResultCode;
import com.beishuo.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String username = request.get("username");

        if (email == null || password == null || username == null) {
            return Result.error(ResultCode.BAD_REQUEST, "邮箱、密码和用户名不能为空");
        }

        // 调用AuthService进行注册
        Map<String, Object> user = authService.register(email, password, username);

        Long userId = ((Number) user.get("id")).longValue();
        String usernameFromUser = (String) user.get("username");

        // 生成Token
        String token = jwtUtil.generateToken(userId, usernameFromUser);
        String refreshToken = jwtUtil.generateRefreshToken(userId, usernameFromUser);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("userInfo", user);

        return Result.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return Result.error(ResultCode.BAD_REQUEST, "邮箱和密码不能为空");
        }

        // 调用AuthService进行登录验证
        Map<String, Object> user = authService.login(email, password);

        Long userId = ((Number) user.get("id")).longValue();
        String username = (String) user.get("username");

        // 生成Token
        String token = jwtUtil.generateToken(userId, username);
        String refreshToken = jwtUtil.generateRefreshToken(userId, username);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("userInfo", user);

        return Result.success(result);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // TODO: 实现登出逻辑（如将Token加入黑名单等）
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getCurrentUserInfo(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null) {
            return Result.error(ResultCode.UNAUTHORIZED);
        }

        String token = jwtUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtil.validateToken(token)) {
            return Result.error(ResultCode.UNAUTHORIZED);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);

        // 调用AuthService获取用户详细信息
        Map<String, Object> user = authService.getUserById(userId);

        return Result.success(user);
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return Result.error(ResultCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // 生成新的Token
        String newToken = jwtUtil.generateToken(userId, username);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, username);

        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);
        result.put("refreshToken", newRefreshToken);

        return Result.success(result);
    }
}

