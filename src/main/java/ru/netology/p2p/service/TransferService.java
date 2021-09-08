package ru.netology.p2p.service;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.netology.p2p.exceptions.ErrorTransfer;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;
import ru.netology.p2p.repository.TransferRepository;

@Service
public class TransferService {

    private final ApplicationContext appContext;
    private final TransferRepository transferRepository;

    private final static double COMMISSION = 1d;
    private final static String NOT_MONEY = "Not enough money";

    public TransferService(ApplicationContext appContext, TransferRepository transferRepository) {
        this.appContext = appContext;
        this.transferRepository = transferRepository;
    }
    /**
     * Метод производит расчёт комиссии
     * для определения суммы списания с карты отправителя.
     * И сетит сумму списания для отправителя в объект входящего сообщения.
     *
     * @param rqTransferDTO объект входящего сообщения
     */
    public void setTransferSum(RqTransferDTO rqTransferDTO) {
        int commissionSum = (int)(rqTransferDTO.getAmount() * rqTransferDTO.getCommission() / 100);
        rqTransferDTO.setTransferSum(rqTransferDTO.getAmount() + commissionSum);
    }
    /**
     * Метод сетит информация по коммиссии и сумме операции в requestDTO.
     * Если бланс карты отправителя позволяет произвести перевод, формирует расходную операцию.
     * Иначе формирует responseDTO по исключению о нехватке денежных средств.
     *
     * @param rqTransferDTO объект входящего сообщения
     * @return объект исходящего сообщения
     */
    public RsTransferDTO transferOperation(RqTransferDTO rqTransferDTO) {
        RsTransferDTO rsTransferDTO;
        rqTransferDTO.setCommission(COMMISSION);
        setTransferSum(rqTransferDTO);
        if(transferRepository.checkAccountBalance(rqTransferDTO)) {
            rsTransferDTO = appContext.getBean("appResponse", RsTransferDTO.class);
            transferRepository.transfer(rqTransferDTO, rsTransferDTO);
        } else {
            throw new ErrorTransfer(NOT_MONEY);
        }
        return rsTransferDTO;
    }
    /**
     * Метод производит подтверждение расходной операции и перевод денежных средств, если таковая сформирована.
     * Иначе формирует responseDTO по исключению об отсутствии операции перевода.
     *
     * @param rqConfirmDTO объект входящего сообщения
     * @return объект исходящего сообщения
     */
    public RsTransferDTO acceptOperation(RqConfirmDTO rqConfirmDTO) {
        RsTransferDTO rsTransferDTO;
        if(transferRepository.containsOperation(rqConfirmDTO.getOperationId())) {
            rsTransferDTO = appContext.getBean("appResponse", RsTransferDTO.class);
            transferRepository.confirm(rqConfirmDTO, rsTransferDTO);
        } else {
            throw new ErrorTransfer("Incorrect GUID");
        }
        return rsTransferDTO;
    }
}
