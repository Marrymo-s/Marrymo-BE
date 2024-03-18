package site.marrymo.restapi.smtp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.marrymo.restapi.smtp.dto.request.SmtpRegistRequest;
import site.marrymo.restapi.smtp.dto.request.SmtpVerifyRequest;
import site.marrymo.restapi.smtp.service.SmtpService;

@Slf4j
@RestController
@RequestMapping("/smtp")
@RequiredArgsConstructor
public class SmtpController {
    private final SmtpService smtpService;

    @PostMapping("/send-email")
    public void registMail(@Valid @RequestBody SmtpRegistRequest smtpRegistRequest) {
        smtpService.registMail(smtpRegistRequest.getEmail(), "Marrymo 이메일 인증", "이메일 인증 번호 : ");
    }

    @PostMapping("/verify-email")
    public ResponseEntity verifyMail(@Valid @RequestBody SmtpVerifyRequest smtpVerifyRequest) {

    }
}
