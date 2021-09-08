package ru.netology.p2p.controller;

import org.springframework.web.bind.annotation.*;
import ru.netology.p2p.exceptions.ErrorInputData;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;
import ru.netology.p2p.service.TransferService;

@CrossOrigin
@RestController("/")
public class AppMainController {

    private final TransferService transferService;
    private final static String INVALID_INPUT_DATA = "Invalid input data";
    private final static String INVALID_CONFIRM_MSG = "Code or operationId may not be null";

    public AppMainController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("transfer")
    public RsTransferDTO transfer(@RequestBody RqTransferDTO rqTransferDTO) {
        RsTransferDTO response;
        if(!rqTransferDTO.isTransferValid()) {
            throw new ErrorInputData(INVALID_INPUT_DATA);
        } else {
            response = transferService.transferOperation(rqTransferDTO);
        }
        return response;
    }

    @PostMapping("confirmOperation")
    public RsTransferDTO confirmOperation(@RequestBody RqConfirmDTO rqConfirmDTO) {
        RsTransferDTO rsTransferDTO;
        if(rqConfirmDTO.getCode() == null || rqConfirmDTO.getOperationId() == null) {
            throw new ErrorInputData(INVALID_CONFIRM_MSG);
        } else {
            rsTransferDTO = transferService.acceptOperation(rqConfirmDTO);
        }
        return rsTransferDTO;
    }
}