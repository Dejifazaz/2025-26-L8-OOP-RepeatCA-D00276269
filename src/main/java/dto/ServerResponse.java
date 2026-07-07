package dto;

/**
 * Generic wrapper for all server responses.
 * Standardises replies with a status, message and typed data payload.
 * @param <T> the type of data being returned
 * @author D00276269
 */
public class ServerResponse<T> {

    private String status;
    private String message;
    private T data;

    public ServerResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}