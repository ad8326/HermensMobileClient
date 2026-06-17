package com.hermens.backend.controller;

import com.hermens.backend.model.HermensLoginRequest;
import com.hermens.backend.model.HermensLoginResponse;
import com.hermens.backend.model.HermensRemoteLoginResponse;
import com.hermens.backend.service.HermensAuthService;
import com.hermens.backend.service.HermensClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final HermensAuthService authService;
    private final HermensClientService hermensClientService;

    public AuthController(HermensAuthService authService, HermensClientService hermensClientService) {
        this.authService = authService;
        this.hermensClientService = hermensClientService;
    }

    @PostMapping("/login")
    public ResponseEntity<HermensLoginResponse> login(@RequestBody HermensLoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body(new HermensLoginResponse("error", "用户名或密码不能为空", null));
        }

        HermensRemoteLoginResponse remoteResponse = hermensClientService.login(request);
        if (remoteResponse == null || !"success".equalsIgnoreCase(remoteResponse.getStatus()) || remoteResponse.getToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new HermensLoginResponse("error", remoteResponse == null ? "Hermens 登录失败" : remoteResponse.getMessage(), null));
        }

        String localToken = authService.createSession(request.getUsername(), remoteResponse.getToken());
        return ResponseEntity.ok(new HermensLoginResponse("success", "登录成功", localToken));
    }
}
