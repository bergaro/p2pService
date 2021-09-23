package ru.netology.p2p.repository;

import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Repository;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TransferRepository {

    private final static Map<String, Integer> accountInfo = new ConcurrentHashMap<>();
    private final static Map<String, RqTransferDTO> unconfirmedRequests = new ConcurrentHashMap<>();

    @PostConstruct
    private void repoInitAccTest(){
        accountInfo.put("1111111111111111", 100000);
        accountInfo.put("2222222222222222", 10000);
    }

    public boolean containsUnconfirmedOperation(String guid) {
        return unconfirmedRequests.containsKey(guid);
    }

    public RqTransferDTO removeFromUnconfirmedRequests(RqConfirmDTO confirmDTO) {
        return unconfirmedRequests.remove(confirmDTO.getOperationId());
    }

    public int getAccountsBalance(String account) {
        return accountInfo.get(account);
    }

    public boolean checkAccountBalance(RqTransferDTO rqTransferDTO) {
        return accountInfo.get(rqTransferDTO.getCardFromNumber()) >= rqTransferDTO.getTransferSum();
    }

    public boolean updateAccountBalance(String account, int balance) {
        return accountInfo.replace(account, balance) != null;
    }

    public void transfer(RqTransferDTO rqTransferDTO, RsTransferDTO rsTransferDTO) {
        UUID rqGUID = Generators.timeBasedGenerator().generate();
        rsTransferDTO.setOperationId(rqGUID.toString());
        unconfirmedRequests.put(rsTransferDTO.getOperationId(), rqTransferDTO);
    }
}
