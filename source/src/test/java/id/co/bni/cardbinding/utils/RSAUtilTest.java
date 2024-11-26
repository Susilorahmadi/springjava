package id.co.bni.cardbinding.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.PublicKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RSAUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(RSAUtilTest.class);

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    @BeforeAll
    public static void setUp() throws Exception {

        String publicKeyStr = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA33t+0edS8W7PCbDerO/AqjMKT+fH/h8Nbauvlgm7ggpQ5RA6+8w4TUOmF7H9N6esk+1DVrhq4vGk4KslQkLZaQ0N9qrNfIzG01jIKrnDJE1wXKmcY5yG+yLSYjW9KlhFLbec5TgftLSAkSzblDA3b6sytU09lJPEI0TEfGRM5ANTNTCY3NKyF+E2MgMVYK4YtK3q8wSYk16eYdsmEL2R89jklGsy7kbbjd9UZVP87N9bC4MRkPznCqaGF1Ev3gXOs67PDqc/Qiw29HN9cpA3V2PLGlUms6PdZIjZEDITxZEorurOajXFOy1ztbMbF8UCMmHfUBKcV73pKWxHtkzE6fbrWlWi1KAhKY9sIX1HZjUqQl/abEM2KupW3GQuAsAZujSa/25D8h2QYQQztGEiJDdcr04gHP6YayvYdRXBxkg9sY7yNT7rF/Ja8rsLeiKidUx7Tx5KjC9UKmsV95RaM17TPTQ/1Wu42awXESfft2zu6EQopHdo6t9G876VuKSQj558yNwfGLJNdd8ijbk+QaE0agB2FwgA2bo9RWM0gC6QX/MrqQrsN1Pq0Uf4bEDSJ8HC9dM83pQpoSakvaEcCfVZ4TBKcjADHxP6a7oovnri0UgsXCjZqeXgpgDEgpJlWlZ7NTVFDSAuIx1B3jdVeYzazgqvkyVwoveAmSx0VdsCAwEAAQ=="; // replace with a valid key
        String privateKeyStr = "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"; // replace with a valid key

        publicKey = RSAUtil.getPublicKeyFromString(publicKeyStr);
        privateKey = RSAUtil.getPrivateKeyFromString(privateKeyStr);

        assertNotNull(publicKey);
        assertNotNull(privateKey);

        logger.info("Public and Private keys are set up successfully.");
    }

    @Test
    public void pubEncryptPrivDecrypt() throws Exception {
        String originalData = "https://lifestyle.bni.co.id/card-binding/map?authCode=987654321";
        logger.info("Original Data : {}", originalData);

        String encryptedData = RSAUtil.encrypt(originalData, publicKey);
        logger.info("Encrypted Data: {}", encryptedData);

        String decryptedData = RSAUtil.decrypt(encryptedData, privateKey);
        logger.info("Decrypted Data: {}", decryptedData);

        assertEquals(originalData, decryptedData);
    }

    @Test
    public void testEncryptWithPrivateKeyAndDecryptWithPublicKey() throws Exception {
        String originalData = "https://lifestyle.bni.co.id/card-binding/map?authCode=987654321";
        logger.info("Original Data: {}", originalData);

        String encryptedData = RSAUtil.encryptWithPrivateKey(originalData, privateKey);
        logger.info("Encrypted Data: {}", encryptedData);

        String decryptedData = RSAUtil.decryptWithPublicKey(encryptedData, publicKey);
        logger.info("Decrypted Data: {}", decryptedData);

        assertEquals(originalData, decryptedData);
    }

    @Test
    public void decryptWithPublic() throws Exception {
        String encData = "d7cDwCmyAkpzRmMo3rajMTzmGr9U0C/GMPbjxTTDmMVEnm1qdJ9NRKRYvbudekiACrpnc3Nhyc85EcWbFv2oyppp29RDZSiCs3J7KwkQ2gPPgVyOZFyJmx2BgAx0ep0v5/hkiRDtNwFhJjqqyjIUj7g4U8zDap8jSv2asEEqWvECNVU3rwpCgxUVtRkvviFOG1UzQl/EYsSchaUmjcE7V4DG83cgC4PPYkLex4T/ZuCAYqlYaFg0vW6t5Lw1c0GRgLd97bP5KW4Ho92w+lokz2ceTqfqkduPSwsluKYAb2cAfdLAfIfB26lOvOjeVVJjsXsuxwFbDEfAO7QK1hoGkEN1N4Yrt4BCzoR28USnlVzNpNkTMyloNJXZF4cWbS0G66HpOcKKIRr4J3tgYxEDBhdD9dUftuo/Wa5mJ6a8lrjX1F5WOSp5R3RaYc9bH3mNpPiz7aNkjTCCyrJrCjASSsEwAGHj3M8LZ9Nv6fA6ePweDAk06R25TJHwXRSxFQqg8NQObSTC1hozwzrpnrPZMQbeA3nxtvNVt1YTWUMx5mqvjWCy67e/fsF6/n6iOFITSDOA/Dg4k702EtKyl6NnYeL/Yg8zf940DXzXiLmQnsxV3sO/wKAA1SjjKNPtYXEo+Xt080bawmD5VOsU4oBFNREtSvz/6MdaEqO4XJ1r1cQ=";
        logger.info("Original Data: {}", encData);

        String decryptedData = RSAUtil.decryptWithPublicKey(encData, publicKey);
        logger.info("Decrypted Data: {}", decryptedData);

    }
}
