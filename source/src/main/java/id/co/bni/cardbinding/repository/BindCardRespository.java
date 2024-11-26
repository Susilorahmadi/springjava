package id.co.bni.cardbinding.repository;

import id.co.bni.cardbinding.entity.BindingCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BindCardRespository extends JpaRepository<BindingCard, String>{

    @Query("SELECT 1 FROM BindingCard ")
    int checkConn();

    @Query("SELECT bind FROM BindingCard bind WHERE bind.bankCardToken= :token AND bind.statusBinding= :status AND bind.channelId= :channel")
    BindingCard findByBankCardToken(@Param("token") String token, @Param("status") String status, @Param("channel") String channel);

    @Query("SELECT a FROM BindingCard a WHERE a.phoneNumber= :phoneNum AND a.statusBinding= :status")
    BindingCard findByPhone(@Param("phoneNum") String phoneNum, @Param("status") String status);

    @Modifying
    @Query("UPDATE BindingCard as b set b.statusBinding= :statusUnbind WHERE b.statusBinding= :statusBind and b.bankCardToken= :bankCardToken")
    int updateStatusBinding(@Param("statusUnbind") String statusUnbind, @Param("statusBind") String statusBind, @Param("bankCardToken") String bankCardToken);
}
