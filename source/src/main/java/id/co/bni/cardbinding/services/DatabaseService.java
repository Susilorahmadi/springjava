package id.co.bni.cardbinding.services;

import id.co.bni.cardbinding.entity.BindingCard;
import id.co.bni.cardbinding.exception.DatabaseException;
import id.co.bni.cardbinding.repository.BindCardRespository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class DatabaseService {
    private final BindCardRespository bindCardRespository;
    public DatabaseService(BindCardRespository bindCardRespository) {
        this.bindCardRespository = bindCardRespository;
    }

    public boolean checkConn() {
        try {
            int i = bindCardRespository.checkConn();
            if (i > 0) {
                return true;
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return false;
    }

    @Transactional
    public int save(BindingCard bindingCard) throws DatabaseException {
        try {
            bindCardRespository.save(bindingCard);
            return 1;
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public int updateUnbind(String token) throws DatabaseException {
        try {
            bindCardRespository.updateStatusBinding("unbinding","binding",token);
            return 1;
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public BindingCard finOne(String token, String status, String channel) throws DatabaseException {
        try {
            return bindCardRespository.findByBankCardToken(token, status, channel);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }

    @Transactional
    public BindingCard finOnePhone(String phoneNum, String status) throws DatabaseException {
        try {
            return bindCardRespository.findByPhone(phoneNum, status);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new DatabaseException(e.getMessage());
        }
    }
}
