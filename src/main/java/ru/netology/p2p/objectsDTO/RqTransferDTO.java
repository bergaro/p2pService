package ru.netology.p2p.objectsDTO;

public class RqTransferDTO {

    private int transferSum;
    private double commission;
    private String cardFromCVV;
    private String cardToNumber;
    private String cardFromNumber;
    private Amount amount;
    private String cardFromValidTill;

    public static class Amount {
        private int value;
        private String currency;
        public Amount(int value, String currency) {
            this.value = value;
            this.currency = currency;
        }
        public void setValue(int value) {
            this.value = value;
        }
        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public int getTransferSum() {
        return transferSum;
    }
    public double getCommission() {
        return commission;
    }
    public String getCardFromCVV() {
        return cardFromCVV;
    }
    public int getAmount() {
        return amount.value;
    }
    public String getCardToNumber() {
        return cardToNumber;
    }
    public String getCardFromNumber() {
        return cardFromNumber;
    }
    public String getCardFromValidTill() {
        return cardFromValidTill;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }
    public RqTransferDTO setTransferSum(int transferSum) {
        this.transferSum = transferSum;
        return this;
    }
    public RqTransferDTO setCardFromCVV(String cardFromCVV) {
        this.cardFromCVV = cardFromCVV;
        return this;
    }
    public RqTransferDTO setCardToNumber(String cardToNumber) {
        this.cardToNumber = cardToNumber;
        return this;
    }
    public RqTransferDTO setAmount(Amount amountOperation) {
        this.amount = amountOperation;
        return this;
    }
    public RqTransferDTO setCardFromNumber(String cardFromNumber) {
        this.cardFromNumber = cardFromNumber;
        return this;
    }
    public RqTransferDTO setCardFromValidTill(String cardFromValidTill) {
        this.cardFromValidTill = cardFromValidTill;
        return this;
    }


    public boolean isTransferValid() {
        return !cardFromNumber.equals(cardToNumber);
    }

    @Override
    public String toString() {
        return "RqTransferDTO{" +
                "cardFromNumber='" + cardFromNumber + '\'' +
                ", cardFromValidTill='" + cardFromValidTill + '\'' +
                ", cardFromCVV='" + cardFromCVV + '\'' +
                ", cardToNumber='" + cardToNumber + '\'' +
                ", amount=" +
                "[" +
                "amount='" + amount.value + '\'' +
                ", currency='" + amount.currency + '\'' +
                "]" +
                ", commission='" + commission + '\'' +
                ", transferSum='" + transferSum + '\'' +
                '}';
    }
}