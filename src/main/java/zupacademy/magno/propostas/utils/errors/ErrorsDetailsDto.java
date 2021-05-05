package zupacademy.magno.propostas.utils.errors;

public class ErrorsDetailsDto {
    private String reason;
    private String error;

    public ErrorsDetailsDto(String reason, String error) {
        this.reason = reason;
        this.error = error;
    }

    public ErrorsDetailsDto(String error) {
        this.error = error;
    }

    public String getReason() {
        return reason;
    }

    public String getError() {
        return error;
    }
}
