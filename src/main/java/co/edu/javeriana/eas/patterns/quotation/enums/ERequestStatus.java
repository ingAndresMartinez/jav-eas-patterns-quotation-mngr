package co.edu.javeriana.eas.patterns.quotation.enums;

public enum ERequestStatus {

    REGISTERED(1),
    IN_PROCESS(2),
    IN_QUOTATION(3),
    FINISHED(4);

    private int status;

    ERequestStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}