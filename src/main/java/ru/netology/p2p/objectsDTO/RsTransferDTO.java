package ru.netology.p2p.objectsDTO;

public class RsTransferDTO {
    private String operationId;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    @Override
    public String toString() {
        return "RsTransferDTO{" +
                "operationId='" + operationId + '\'' +
                '}';
    }
}