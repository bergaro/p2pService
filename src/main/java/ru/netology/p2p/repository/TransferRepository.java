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
    /**
     * Метод проверяет наличие сформированной но не подтверждённой операции перевода.
     *
     * @param guid глабальный идентификатор операции
     * @return true - если операция существует | false - если операция не сформирована
     */
    public boolean containsOperation(String guid) {
        return unconfirmedRequests.containsKey(guid);
    }
    /**
     * Метод проверяет, хватит ли денежных средств для совершения перевода.
     *
     * @param rqTransferDTO объект входящего сообщения
     * @return true - если денежных средств достаточно для орагнизации перевода | false -
     * денежных средств не достаточно для организации перевода
     */
    public boolean checkAccountBalance(RqTransferDTO rqTransferDTO) {
        return accountInfo.get(rqTransferDTO.getCardFromNumber()) >= rqTransferDTO.getTransferSum();
    }
    /**
     * Метод подтверждает операцию перервода и организует перевод денежных средств
     * с карты отправителя на карту получателя.
     *
     * @param rqConfirmDTO объект сообщения с акцептом операции
     * @param rsTransferDTO объект исходящего сообщения
     */
    public void confirm(RqConfirmDTO rqConfirmDTO, RsTransferDTO rsTransferDTO) {
        RqTransferDTO rqTransferDTO = unconfirmedRequests.remove(rqConfirmDTO.getOperationId());
        if(checkAccountBalance(rqTransferDTO)) {
            p2pOperation(rqTransferDTO);
            rsTransferDTO.setOperationId(rqConfirmDTO.getOperationId());
        }
    }
    /**
     * Метод формирует операцию перевода денежных средств и присвоивает ей GUID.
     *
     * @param rqTransferDTO объект входящего сообщения
     * @param rsTransferDTO объект исходящего сообщения
     */
    public void transfer(RqTransferDTO rqTransferDTO, RsTransferDTO rsTransferDTO) {
        UUID rqGUID = Generators.timeBasedGenerator().generate();
        rsTransferDTO.setOperationId(rqGUID.toString());
        unconfirmedRequests.put(rsTransferDTO.getOperationId(), rqTransferDTO);
    }
    /**
     * Синхронизированный метод. Производит списание денежных средств с карты отправителя с
     * последующем зачислением на карту получателя.
     *
     * (Комиссия списанная с отправителя не начисляется получателю ДС!)
     *
     * @param rqTransferDTO обхект входящего сообщения
     */
    private synchronized void p2pOperation(RqTransferDTO rqTransferDTO) {
        String accSender = rqTransferDTO.getCardFromNumber();
        String accRecipient = rqTransferDTO.getCardToNumber();

        int senderAmount = accountInfo.get(accSender) - rqTransferDTO.getTransferSum();
        int recipientAmount = accountInfo.get(accRecipient) + rqTransferDTO.getAmount();

        accountInfo.replace(accSender, senderAmount);
        accountInfo.replace(accRecipient, recipientAmount);
    }
}
