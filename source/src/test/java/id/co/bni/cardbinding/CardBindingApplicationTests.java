//package id.co.bni.cardbinding;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import id.co.bni.cardbinding.models.microservice.RequestCardlink;
//import id.co.bni.cardbinding.models.microservice.ResponseModel;
//import id.co.bni.cardbinding.services.OTPService;
//import id.co.bni.cardbinding.services.RedisService;
//import id.co.bni.cardbinding.services.RestService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//
//class CardBindingApplicationTests {
//
//    @Mock
//    private RedisService redisService;
//
//    @Mock
//    private RestService restService;
//
//    @InjectMocks
//    private OTPService otpService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void resendOtp() throws Exception {
//
//        String mockRequest = """
//                {
//                     "cardNumber": "k35wW1qyPOOztcSn6pzNB+R4qaANOV0mZ930s5CHCrM=",
//                     "additionalInfo": {
//                         "channel": "MAPC",
//                         "statementDate":"",
//                         "authCode": "2fd7577c-1507-4448-b8d3-781d60b3255b",
//                         "dob": "",
//                         "expiryDate": "",
//                         "otpSender": "WHATSAPP",
//                         "pin": "",
//                         "otpCode": ""
//                     }
//                 }
//                """;
//
//        String mockResRedis = """
//                {
//                    "partnerReferenceNo": "2020102900000000000001",
//                    "accountName": "Putra Negara",
//                    "cardData": "",
//                    "custIdMerchant": "8a95f0026d2860f301",
//                    "isBindAndPay": "N",
//                    "merchantId": "00007100010926 ",
//                    "terminalId": "72001126",
//                    "journeyId": "20190329175623MTISTORE",
//                    "subMerchantId": "310928924949487",
//                    "externalStoreId": "000183004658",
//                    "limit": "1000000",
//                    "merchantLogoUrl": "https://bilba.test.com/dist/img/merchant-logo.png",
//                    "phoneNo": "085758106165",
//                    "sendOtpFlag": "YES",
//                    "type": "subcribe",
//                    "additionalInfo": {
//                        "deviceId": "12345679237",
//                        "channel": "MAP",
//                        "urlCallbackSuccess": "https://www.mapclub.com/member/page/success-binding"
//                    }
//                }
//                """;
//
//        String mockResOTP = """
//                {
//                    "isSuccess": true,
//                    "payload": {
//                        "channel": "MAPC",
//                        "phone_number": "6285758106165",
//                        "country_code": "62",
//                        "otp_sender": "WHATSAPP",
//                        "product_code": null,
//                        "otp_req_attempt": "1",
//                        "error_code": "",
//                        "error_message": "",
//                        "status": "SUCCESS"
//                    },
//                    "responseCode": "00",
//                    "responseMessage": "Success"
//                }
//                """;
//
//        ObjectMapper mapper = new ObjectMapper();
//        RequestCardlink req = mapper.readValue(mockRequest, RequestCardlink.class);
//        ResponseModel responseModel = mapper.readValue(mockResOTP, ResponseModel.class);
//
//        when(redisService.get(anyString())).thenReturn(mockResRedis);
//        when(restService.callAPI(anyString(),anyString(),anyString())).thenReturn(responseModel);
//
//        ResponseEntity<Object> objectResponseEntity = otpService.requestOtp(req);
//        System.out.println(objectResponseEntity);
//
//    }
//
//}
