package id.co.bni.cardbinding.controllers;

import id.co.bni.cardbinding.models.microservice.RequestCardlink;
import id.co.bni.cardbinding.services.OTPService;
import id.co.bni.cardbinding.services.PINservice;
import id.co.bni.cardbinding.services.RegistrationService;
import id.co.bni.cardbinding.utils.HandlerUtil;
import id.co.bni.cardbinding.utils.ServiceUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secure/credit-card/v1.0")
@RestController
@Slf4j
public class SecureController {
    private final OTPService otpService;
    private final RegistrationService registrationService;
    private final PINservice pinService;
    private final ServiceUtil serviceUtil;

    public SecureController(OTPService otpService, RegistrationService registrationService, PINservice pinService, ServiceUtil serviceUtil) {
        this.otpService = otpService;
        this.registrationService = registrationService;
        this.pinService = pinService;
        this.serviceUtil = serviceUtil;
    }

    @PostMapping(path = "/confirm-card-bind")
    public ResponseEntity<Object> confirmCardBind(@Valid @RequestBody RequestCardlink body) {
        log.info("secure-confirm-card-bind");
        //decrypy body dan encypt cardNum
        RequestCardlink bodyInject = serviceUtil.changeSecurebody(body);
        if (bodyInject == null) {
            return HandlerUtil.generalError(null);
        }
        return registrationService.confirmBind(bodyInject);
    }

    @PostMapping(path = "/activate-card-bind")
    public ResponseEntity<Object> activateCardBind(@Valid @RequestBody RequestCardlink body) {
        log.info("secure-activate-card-bind");
        //decrypy body dan encypt cardNum
        RequestCardlink bodyInject = serviceUtil.changeSecurebody(body);
        if (bodyInject == null) {
            return HandlerUtil.generalError(null);
        }
        return registrationService.activateCard(bodyInject);
    }

    @PostMapping(path = "/resend-otp-card-bind")
    public ResponseEntity<Object> otpResend(@RequestBody RequestCardlink body){
        log.info("secure-resend-card-bind");
        //decrypy body dan encypt cardNum
        RequestCardlink bodyInject = serviceUtil.changeSecurebody(body);
        if (bodyInject == null) {
            return HandlerUtil.generalError(null);
        }
        return otpService.requestOtp(bodyInject);
    }

    @PostMapping(path = "/validate-otp-card-bind")
    public  ResponseEntity<Object> otpValidate(@RequestBody RequestCardlink body){
        log.info("secure-validate-otp-card-bind");
        //decrypy body dan encypt cardNum
        RequestCardlink bodyInject = serviceUtil.changeSecurebody(body);
        if (bodyInject == null) {
            return HandlerUtil.generalError(null);
        }
        return otpService.validateOtp(bodyInject);
    }

    @PostMapping(path = "/validate-pin-card-bind")
    public ResponseEntity<Object> validatePinCard(@RequestBody RequestCardlink body){
        log.info("secure-validate-pin-card-bind");
        //decrypy body dan encypt cardNum and pin
        RequestCardlink bodyInject = serviceUtil.changeSecurebody(body);
        if (bodyInject == null) {
            return HandlerUtil.generalError(null);
        }
        return pinService.validatePin(bodyInject);
    }
}
