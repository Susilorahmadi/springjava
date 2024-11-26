package id.co.bni.cardbinding.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "BINDING_TRX_CREDIT_CARD")
public class BindingCard {
    @Id
    @Column(name = "BINDING_TRX_ID")
    private String bindingTrxId;
    @Column(name = "CREATE_AT")
    private Timestamp createAt;
    @Column(name = "AUTH_CODE")
    private String authCode;
    @Column(name = "STATE")
    private String state;
    @Column(name = "PATNER_REF_NUM")
    private String patnerRefNum;
    @Column(name = "ACCOUNT_NAME")
    private String accountName;
    @Column(name = "CARD_DATA")
    private String cardData;
    @Column(name = "CUS_ID_MERCHANT")
    private String cusId;
    @Column(name = "IS_BIND_IN_PAY")
    private String isBindInPay;
    @Column(name = "MERCHANT_ID")
    private String merchantId;
    @Column(name = "TERMINAL_ID")
    private String terminalId;
    @Column(name = "JOURNEY_ID")
    private String journeyId;
    @Column(name = "SUB_MERCAHNT_ID")
    private String subMercahntId;
    @Column(name = "EXTERNAL_STORE_ID")
    private String externalStoreId;
    @Column(name = "LIMIT")
    private String limit;
    @Column(name = "MERCHANT_LOGO_URL")
    private String merchantLogoUrl;
    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;
    @Column(name = "SEND_OTP_FLAG")
    private String sendOtpFlag;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "DEVICE_ID")
    private String deviceId;
    @Column(name = "CHANNEL_ID")
    private String channelId;
    @Column(name = "URL_CALLBACK")
    private String urlCallback;
    @Column(name = "BLOCK_CODE")
    private String blockCode;
    @Column(name = "CARD_NUMBER")
    private String cardNumber;
    @Column(name = "BANK_CARD_TOKEN")
    private String bankCardToken;
    @Column(name = "STATUS_BINDING")
    private String statusBinding;
    @Column(name = "CREDENTIAL_NUM_ACCOUNT")
    private String credentialNumAccount;
}
