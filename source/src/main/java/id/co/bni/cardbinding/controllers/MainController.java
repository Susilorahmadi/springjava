package id.co.bni.cardbinding.controllers;

import id.co.bni.cardbinding.models.microservice.*;
import id.co.bni.cardbinding.services.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


@RequestMapping("/credit-card/v1.0")
@RestController
@Slf4j
public class MainController {

    private final OTPService otpService;
    private final RegistrationService registrationService;
    private final PageService pageService;
    private final PINservice pinService;
    private final InquiryService inquiryService;
    private final UnRegisterService unRegisterService;

    public MainController(final OTPService otpService, 
                          final RegistrationService registrationService, 
                          final PageService pageService, 
                          final PINservice pinService,
                          final InquiryService inquiryService,
                          final UnRegisterService unRegisterService) {
        this.otpService = otpService;
        this.registrationService = registrationService;
        this.pageService = pageService;
        this.pinService = pinService;
        this.inquiryService = inquiryService;
        this.unRegisterService = unRegisterService;
    }

    @GetMapping(path = "/page")
    public ResponseEntity<Object> getPage(@RequestParam(value = "form") String form){
        log.info("Type {}", form);
        return pageService.helperPage(form);
    }

    @PostMapping(path = "/registration-card-bind")
    public ResponseEntity<Object> registrationCardBind(@Valid @RequestBody RequestRegistration body) {
        log.info("registration-card-bind");
        return registrationService.register(body);
    }

    @GetMapping(path = "/get-auth-code")
    public ResponseEntity<Object> requestAuthCode(@RequestParam("authCode") String authCode, @RequestParam("state") String state) {
        log.info("request-auth-code");
        return registrationService.getAuthcode(authCode,state);
    }

    @PostMapping(path = "/confirm-card-bind")
    public ResponseEntity<Object> confirmCardBind(@Valid @RequestBody RequestCardlink body) {
        log.info("confirm-card-bind");
        return registrationService.confirmBind(body);
    }

    @PostMapping(path = "/activate-card-bind")
    public ResponseEntity<Object> activateCardBind(@Valid @RequestBody RequestCardlink body) {
        log.info("activate-card-bind");
        return registrationService.activateCard(body);
    }

    @PostMapping(path = "/resend-otp-card-bind")
    public ResponseEntity<Object> otpResend(@RequestBody RequestCardlink body){
        log.info("resend-card-bind");
        return otpService.requestOtp(body);
    }

    @PostMapping(path = "/validate-otp-card-bind")
    public  ResponseEntity<Object> otpValidate(@RequestBody RequestCardlink body){
        log.info("validate-otp-card-bind");
        return otpService.validateOtp(body);
    }

    @PostMapping(path = "/validate-pin-card-bind")
    public ResponseEntity<Object> validatePinCard(@RequestBody RequestCardlink body){
        log.info("validate-pin-card-bind");
        return pinService.validatePin(body);
    }

    @PostMapping(path = "/summary-card-bind")
    public ResponseEntity<Object> summaryCardBind(@Valid @RequestBody RequestInqCardlinkSummary body){
        log.info("summary-card-bind");
        return inquiryService.summaryCard(body);
    }
    
    @PostMapping(path = "/unbilled-card-bind")
    public ResponseEntity<Object> unbilledCardBind(@Valid @RequestBody RequestInqCardlinkSummary body){
        log.info("unbilled-card-bind");
        return inquiryService.unbilledCard(body);
    }

    @PostMapping(path = "/billing-list-card-bind")
    public ResponseEntity<Object> billingListCardBind(@Valid @RequestBody RequestInqCardlinkSummary body){
        log.info("billing-list-card-bind");
        return inquiryService.billingListCard(body);
    }
    
    @PostMapping(path = "/billing-statement-card-bind")
    public ResponseEntity<Object> billingStatementCardBind(@Valid @RequestBody RequestInqCardlinkStatementDate body){
        log.info("billing-statement-card-bind");
        return inquiryService.billingStatementCard(body);
    }

    @PostMapping(path = "/unbinding-card-bind")
    public ResponseEntity<Object> unBindingCardBind(@Valid @RequestBody RequestInqCardlinkSummary body){
        log.info("unbiniding-card-bind");
        return unRegisterService.unbinding(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("isSuccess", false);
        body.put("payload", errors);
        body.put("responseCode", "9009");
        body.put("responseMessage", "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
