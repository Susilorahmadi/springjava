package id.co.bni.cardbinding.services;

import id.co.bni.cardbinding.configs.MicroserviceEnum;
import id.co.bni.cardbinding.entity.BindingCard;
import id.co.bni.cardbinding.exception.APIException;
import id.co.bni.cardbinding.exception.DatabaseException;
import id.co.bni.cardbinding.models.microservice.RequestInqCardlink;
import id.co.bni.cardbinding.models.microservice.RequestInqCardlinkSummary;
import id.co.bni.cardbinding.utils.HandlerUtil;
import id.co.bni.cardbinding.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UnRegisterService {
    private final DatabaseService databaseService;

    public UnRegisterService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public ResponseEntity<Object> unbinding(RequestInqCardlinkSummary body){
        try {
            log.info("Un Register request: {}", body);
            String token = body.getBankCardToken();
            // select biding_trx_card_bind where bankCardToken and binding_status
            BindingCard respDB = databaseService.finOne(token, MicroserviceEnum.BINDING_STATUS.getMessage(),body.getAdditionalInfo().getChannel());
            if(respDB == null) {
                return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(), null);
            }

            int accepted = databaseService.updateUnbind(body.getBankCardToken());
            if(accepted == 1){
                log.info("1 - Accepted");
                return HandlerUtil.success("bankCardToken: "+token);
            }

            return HandlerUtil.unAuthorized(MicroserviceEnum.INVALID_TOKEN.getMessage(), null);
        } catch (DatabaseException e){
            return HandlerUtil.databaseError(e.getMessage());
        }  catch (Exception ex){
            return HandlerUtil.generalError(ex.getMessage());
        }
    }
}
