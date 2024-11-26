package id.co.bni.cardbinding.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.models.cardlink.ReqEncryptCard;
import id.co.bni.cardbinding.models.cardlink.ResEncryptCard;
import id.co.bni.cardbinding.models.microservice.RequestCardlink;
import id.co.bni.cardbinding.models.microservice.ResponseModel;
import id.co.bni.cardbinding.services.RestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Component
@Slf4j
public class ServiceUtil {
    @Value("${url.encrypt.card}")
    private String urlEncryptCard;
    @Value("${rps.microgateway.encrypt.decrypt.algoritm}")
    private String encryptDecryptAlgoritm;
    @Value("${rps.microgateway.key.generator.algoritm}")
    private String keyGenerator;
    @Value("${file.path}")
    private String filePath;

    private final RestService restService;

    public ServiceUtil(RestService restService) {
        this.restService = restService;
    }

    ObjectMapper mapper = new ObjectMapper();

    public RequestCardlink changeSecurebody(RequestCardlink body){
        try{
            // decrypt body
//            String encString = encryptData(body);
            String reqBody = decryptData(body.getData());
            if(reqBody == null || reqBody.isEmpty()){
                return null;
            }

            // encrypt card Num
            RequestCardlink resDecyptData = mapper.readValue(reqBody, RequestCardlink.class);
            String cardNumEnc = encryptToCardlink(resDecyptData.getCardNumber());
            String pinEnc = encryptToCardlink(resDecyptData.getAdditionalInfo().getPin());

            RequestCardlink requestCardlink = new RequestCardlink();
            RequestCardlink.FieldAdditionalInfo fieldAdditionalInfo = new RequestCardlink.FieldAdditionalInfo();
            requestCardlink.setAdditionalInfo(fieldAdditionalInfo);
            requestCardlink.setCardNumber(cardNumEnc);
            fieldAdditionalInfo.setChannel(resDecyptData.getAdditionalInfo().getChannel());
            fieldAdditionalInfo.setStatementDate(resDecyptData.getAdditionalInfo().getStatementDate());
            fieldAdditionalInfo.setAuthCode(resDecyptData.getAdditionalInfo().getAuthCode());
            fieldAdditionalInfo.setDob(resDecyptData.getAdditionalInfo().getDob());
            fieldAdditionalInfo.setExpiryDate(resDecyptData.getAdditionalInfo().getExpiryDate());
            fieldAdditionalInfo.setOtpSender(resDecyptData.getAdditionalInfo().getOtpSender());
            fieldAdditionalInfo.setPin(pinEnc);
            fieldAdditionalInfo.setOtpCode(resDecyptData.getAdditionalInfo().getOtpCode());

            return requestCardlink;

        } catch (Exception ex){
            log.warn(ex.getMessage());
            return null;
        }
    }

    public String encryptToCardlink(String text){
        try {
            ReqEncryptCard reqEncryptCard = new ReqEncryptCard();
            reqEncryptCard.setText(text);
            ResponseModel respDecp = restService.callAPI(urlEncryptCard, "POST", reqEncryptCard);
            ResEncryptCard resEncryptCard = mapper.convertValue(respDecp.getPayload(), ResEncryptCard.class);
            log.info(resEncryptCard.toString());
            return resEncryptCard.getData();
        } catch (APIException e){
            log.warn(e.getMessage()+" , "+e.getPayload());
            return null;
        } catch (Exception ex){
            log.warn(ex.getMessage());
            return null;
        }
    }

    public String decryptData(String encryptedText) {
        String result;
        try {
            String privateKey = readFile();
            Cipher cipher = Cipher.getInstance(encryptDecryptAlgoritm);
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privateKey));
            result = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
        return result;
    }

    public PrivateKey loadPrivateKey(String privateKey) throws GeneralSecurityException {
        byte[] clear = Base64.getDecoder().decode((privateKey.getBytes()));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance(keyGenerator);
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    public String readFile() {
        ClassPathResource classPathResource = new ClassPathResource(filePath);
        try (InputStream inputStream = classPathResource.getInputStream()) {
            byte[] keyBytes = Files.readAllBytes(Paths.get(classPathResource.getURI()));
            return new String(keyBytes);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return "";
        }
    }



    // Tes encrypt publicKey
    // dev only
    public String encryptData(Object object) {
        String result;
        try {
            String jsonString = mapper.writeValueAsString(object);
            byte[] plainText = jsonString.getBytes();

            Cipher cipher = Cipher.getInstance(encryptDecryptAlgoritm);
            PublicKey publicKey = getPublicKeyFromString("MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA33t+0edS8W7PCbDerO/AqjMKT+fH/h8Nbauvlgm7ggpQ5RA6+8w4TUOmF7H9N6esk+1DVrhq4vGk4KslQkLZaQ0N9qrNfIzG01jIKrnDJE1wXKmcY5yG+yLSYjW9KlhFLbec5TgftLSAkSzblDA3b6sytU09lJPEI0TEfGRM5ANTNTCY3NKyF+E2MgMVYK4YtK3q8wSYk16eYdsmEL2R89jklGsy7kbbjd9UZVP87N9bC4MRkPznCqaGF1Ev3gXOs67PDqc/Qiw29HN9cpA3V2PLGlUms6PdZIjZEDITxZEorurOajXFOy1ztbMbF8UCMmHfUBKcV73pKWxHtkzE6fbrWlWi1KAhKY9sIX1HZjUqQl/abEM2KupW3GQuAsAZujSa/25D8h2QYQQztGEiJDdcr04gHP6YayvYdRXBxkg9sY7yNT7rF/Ja8rsLeiKidUx7Tx5KjC9UKmsV95RaM17TPTQ/1Wu42awXESfft2zu6EQopHdo6t9G876VuKSQj558yNwfGLJNdd8ijbk+QaE0agB2FwgA2bo9RWM0gC6QX/MrqQrsN1Pq0Uf4bEDSJ8HC9dM83pQpoSakvaEcCfVZ4TBKcjADHxP6a7oovnri0UgsXCjZqeXgpgDEgpJlWlZ7NTVFDSAuIx1B3jdVeYzazgqvkyVwoveAmSx0VdsCAwEAAQ==");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            result = Base64.getEncoder().encodeToString(cipher.doFinal(plainText));
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
        return result;
    }

    // Get public key from string
    private static PublicKey getPublicKeyFromString(String key) {
        try {
            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
