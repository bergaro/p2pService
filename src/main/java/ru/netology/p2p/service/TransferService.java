package ru.netology.p2p.service;

import org.springframework.stereotype.Service;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;
import ru.netology.p2p.exceptions.ErrorTransfer;
import ru.netology.p2p.repository.TransferRepository;


@Service
public class TransferService {

    private RsTransferDTO operationRs;
    private final TransferRepository transferRepository;

    private final static double COMMISSION = 1;
    private final static String NOT_MONEY = "Not enough money";
    private final static String TRANSACTION_ERROR = "Transaction error";

    public TransferService(RsTransferDTO operationRs, TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
        this.operationRs = operationRs;
    }
    /**
     * Рассчёт комиссии по операции и добавление полной суммы в объект rqTransferDTO
     * @param rqTransferDTO
     */
    private void setTransferSum(RqTransferDTO rqTransferDTO) {
        int commissionSum = (int)(rqTransferDTO.getAmount() * rqTransferDTO.getCommission() / 100);
        rqTransferDTO.setTransferSum(rqTransferDTO.getAmount() + commissionSum);
    }

    public RsTransferDTO transferOperation(RqTransferDTO rqTransferDTO) {
        rqTransferDTO.setCommission(COMMISSION);
        setTransferSum(rqTransferDTO);
        if(transferRepository.checkAccountBalance(rqTransferDTO)) {
            transferRepository.transfer(rqTransferDTO, operationRs);
        } else {
            throw new ErrorTransfer(NOT_MONEY);
        }
        return operationRs;
    }

    public RsTransferDTO acceptOperation(RqConfirmDTO rqConfirmDTO) {
        if(transferRepository.containsUnconfirmedOperation(rqConfirmDTO.getOperationId())) {
            confirm(rqConfirmDTO, operationRs);
        } else {
            throw new ErrorTransfer("Incorrect GUID");
        }
        return operationRs;
    }

    public void confirm(RqConfirmDTO rqConfirmDTO, RsTransferDTO rsTransferDTO) {
        RqTransferDTO rqTransferDTO = transferRepository.removeFromUnconfirmedRequests(rqConfirmDTO);
        if(transferRepository.checkAccountBalance(rqTransferDTO)) {
            if(!p2pOperation(rqTransferDTO)) {
                throw new ErrorTransfer(TRANSACTION_ERROR);
            }
            rsTransferDTO.setOperationId(rqConfirmDTO.getOperationId());
        }
    }

    private synchronized boolean p2pOperation(RqTransferDTO rqTransferDTO) {
        String accSender = rqTransferDTO.getCardFromNumber();
        String accRecipient = rqTransferDTO.getCardToNumber();

        int transferAmountWithCommission = rqTransferDTO.getTransferSum();
        int transferAmountWithoutCommission = rqTransferDTO.getAmount();

        int senderAmount =  transferRepository.getAccountsBalance(accSender) -  transferAmountWithCommission;
        int recipientAmount = transferRepository.getAccountsBalance(accRecipient) - transferAmountWithoutCommission;

        boolean senderBalanceChanged = transferRepository.updateAccountBalance(accSender, senderAmount);
        boolean recipientBalanceChanged = transferRepository.updateAccountBalance(accRecipient, recipientAmount);

        return senderBalanceChanged && recipientBalanceChanged;
    }
}