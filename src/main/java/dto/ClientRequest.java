package dto;

/**
 * Represents a client request sent to the server over a socket connection.
 * Carries a request type and an optional JSON payload.
 * @author D00276269
 */
public class ClientRequest {

    private String requestType;
    private String payload;

    public ClientRequest(String requestType, String payload) {
        this.requestType = requestType;
        this.payload = payload;
    }

    public String getRequestType() { return requestType; }
    public String getPayload() { return payload; }

    public void setRequestType(String requestType) { this.requestType = requestType; }
    public void setPayload(String payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "ClientRequest{" +
                "requestType='" + requestType + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }
}