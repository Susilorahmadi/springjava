package id.co.bni.cardbinding.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.entity.BindingCard;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.exception.DatabaseException;
import id.co.bni.cardbinding.models.cardlink.*;
import id.co.bni.cardbinding.models.microservice.*;
import id.co.bni.cardbinding.utils.HandlerUtil;
import id.co.bni.cardbinding.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class InquiryService {
    @Value("${url.summary.card}")
    private String urlSummaryCard;
    @Value("${url.unbilled.card}")
    private String urlUnbilledCard;
    @Value("${url.billing.statement.card}")
    private String urlBillingStatementCard;
    @Value("${url.billing.list.card}")
    private String urlBillingListCard;

    private final RestService restService;
    private final DatabaseService databaseService;

    public InquiryService(RestService restService, DatabaseService databaseService) {
        this.restService = restService;
        this.databaseService = databaseService;
    }

    ObjectMapper mapper = new ObjectMapper();

    public ResponseEntity<Object> summaryCard(RequestInqCardlinkSummary body) {
        try {
            log.info("summary request: {}", body);
            String token = body.getBankCardToken();
            // select biding_trx_card_bind where bankCardToken and binding_status
            BindingCard respDB = databaseService.finOne(token, MicroserviceEnum.BINDING_STATUS.getMessage(),body.getAdditionalInfo().getChannel());
            if(respDB == null) {
                return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(), null);
            }

            ReqSummary summary = new ReqSummary();
            summary.setCardNumber(respDB.getCardNumber());
            summary.setChannelID(respDB.getChannelId());

            restService.startRequest("POST",urlSummaryCard,summary);
            ResponseModel resp = restService.callAPI(urlSummaryCard, "POST", summary);
            log.info(StringUtil.toString(resp.getPayload()));

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ResSummary respSummary = mapper.convertValue(resp.getPayload(), ResSummary.class);
            if(!respSummary.getResponseCode().equals("00")){
                return HandlerUtil.externalAPIError("Gagal Cek Summary",respSummary);
            }

            ResponseInqSummary inqSummary = new ResponseInqSummary();
            inqSummary.setStatusKartu(respSummary.getData().getStatusKartu());
            inqSummary.setBlockCode(respSummary.getData().getBlockCode());
            inqSummary.setCredentialNo(respDB.getCredentialNumAccount());
            inqSummary.setCreditLimit(respSummary.getData().getCreditLimit());
            inqSummary.setMemoBalance(respSummary.getData().getMemoBalance());
            inqSummary.setAvailableCredit(respSummary.getData().getAvailableCredit());
            inqSummary.setStatementDate(respSummary.getData().getLastStatementDate() );
            inqSummary.setMessage(respSummary.getMessage());
            inqSummary.setResponseMessage(respSummary.getResponseMessage());
            inqSummary.setCardNumber(respSummary.getCardNumber());
            inqSummary.setResponseCode(respSummary.getResponseCode());

            return HandlerUtil.success(inqSummary);
        } catch (DatabaseException e){
            return HandlerUtil.databaseError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> unbilledCard(RequestInqCardlinkSummary body) {
        try {
            log.info("unbilled request: {}", body);
            String token = body.getBankCardToken();
            // select biding_trx_card_bind where bankCardToken and binding_status
            BindingCard respDB = databaseService.finOne(token, MicroserviceEnum.BINDING_STATUS.getMessage(),body.getAdditionalInfo().getChannel());
            if(respDB == null) {
                return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(), null);
            }
            ReqSummary unbilled = new ReqSummary();
            unbilled.setCardNumber(respDB.getCardNumber());
            unbilled.setChannelID(respDB.getChannelId());

            restService.startRequest("POST",urlUnbilledCard,unbilled);
            ResponseModel resp = restService.callAPI(urlUnbilledCard, "POST", unbilled);
            log.info(StringUtil.toString(resp.getPayload()));

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ResUnbilled resUnbilled = mapper.convertValue(resp.getPayload(), ResUnbilled.class);
            if(resUnbilled.getData().isEmpty() || resUnbilled.getResponseCode() == null){
                return HandlerUtil.externalAPIError("Gagal cek unbilled",resUnbilled);
            }

            return HandlerUtil.success(resUnbilled);
        } catch (DatabaseException e){
            return HandlerUtil.databaseError( e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> billingListCard(RequestInqCardlinkSummary body) {
        try {
            log.info("billing list request: {}", body);
            String token = body.getBankCardToken();
            // select biding_trx_card_bind where bankCardToken and binding_status
            BindingCard respDB = databaseService.finOne(token, MicroserviceEnum.BINDING_STATUS.getMessage(),body.getAdditionalInfo().getChannel());
            if(respDB == null) {
                return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(),null);
            }
            ReqBillingList reqBillingList = new ReqBillingList();
            reqBillingList.setCardNumber(respDB.getCardNumber());

            restService.startRequest("POST",urlBillingListCard,reqBillingList);
            ResponseModel resp = restService.callAPI(urlBillingListCard, "POST", reqBillingList);
            log.info(StringUtil.toString(resp.getPayload()));
            return HandlerUtil.success(resp.getPayload());
        } catch (DatabaseException e){
            return HandlerUtil.databaseError( e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage()+ e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }

    public ResponseEntity<Object> billingStatementCard(RequestInqCardlinkStatementDate body) {
        try {
            log.info("billing statement request: {}", body);
            String token = body.getBankCardToken();
            // select biding_trx_card_bind where bankCardToken and binding_status
            BindingCard respDB = databaseService.finOne(token, MicroserviceEnum.BINDING_STATUS.getMessage(),body.getAdditionalInfo().getChannel());
            if(respDB == null) {
                return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(), null);
            }
            ReqBillingStatement reqBillingStatement = new ReqBillingStatement();
            reqBillingStatement.setCardNumberPosting(respDB.getCardNumber());
            reqBillingStatement.setStatementDate(body.getAdditionalInfo().getStatementDate());
            reqBillingStatement.setChannelID(respDB.getChannelId());

            restService.startRequest("POST",urlBillingStatementCard,reqBillingStatement);
            ResponseModel resp = restService.callAPI(urlBillingStatementCard, "POST", reqBillingStatement);
            log.info(StringUtil.toString(resp.getPayload()));
            return HandlerUtil.success(resp.getPayload());
        } catch (DatabaseException e){
            return HandlerUtil.databaseError(e.getMessage());
        } catch (APIException e){
            return HandlerUtil.externalAPIError(MicroserviceEnum.ERROR_THRIDPARTY.getMessage() + e.getPayload(),null);
        } catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }
}
