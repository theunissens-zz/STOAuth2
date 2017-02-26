package co.za.st.exceptions;

/**
 * Created by stevy on 2017/02/26.
 */
public class ClientNotFoundException extends Exception{

    public ClientNotFoundException() {
        super("Client not found");
    }

    public ClientNotFoundException(String clientName) {
        super(String.format("Client not found: ", clientName));
    }
}
