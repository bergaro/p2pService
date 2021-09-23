package ru.netology.p2p.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.exceptions.ErrorInputData;
import ru.netology.p2p.service.TransferService;
import javax.validation.Valid;

@CrossOrigin
@RestController("/")
public class AppMainController {

    private final TransferService transferService;
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final static String INVALID_INPUT_DATA = "Invalid input data";

    public AppMainController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("transfer")
    public RsTransferDTO transfer(@RequestBody RqTransferDTO rqTransferDTO) {
        RsTransferDTO response;
        logger.info("Request received: {" + rqTransferDTO + "}");
        if(!rqTransferDTO.isTransferValid()) {
            throw new ErrorInputData(INVALID_INPUT_DATA);
        } else {
            response = transferService.transferOperation(rqTransferDTO);
        }
        logger.info("Response sent: {" + response + "}");
        return response;
    }

    //TODO добавление валидации
    @PostMapping("confirmOperation")
    public RsTransferDTO confirmOperation( @RequestBody @Valid RqConfirmDTO rqConfirmDTO) {
        logger.info("Confirmation request received: {" + rqConfirmDTO + "}");
        RsTransferDTO rsTransferDTO = transferService.acceptOperation(rqConfirmDTO);
        logger.info("Response sent: {" + rsTransferDTO+ "}");
        return rsTransferDTO;
    }

}
