package ru.netology.p2p.objectsDTO;

public class RqConfirmDTO {

    private String code;
    private String operationId;
    private int amountWithCommission;

    public RqConfirmDTO setCode(String code) {
        this.code = code;
        return this;
    }
    public RqConfirmDTO setOperationId(String operationId) {
        this.operationId = operationId;
        return this;
    }
    public int getAmountWithCommission() {
        return amountWithCommission;
    }

    public String getCode() {
        return code;
    }
    public String getOperationId() {
        return operationId;
    }
    public void setAmountWithCommission(int amountWithCommission) {
        this.amountWithCommission = amountWithCommission;
    }

    @Override
    public String toString() {
        return "RqConfirmDTO{" +
                "code='" + code + '\'' +
                ", operationId='" + operationId + '\'' +
                ", amountWithCommission=" + amountWithCommission +
                '}';
    }
}
