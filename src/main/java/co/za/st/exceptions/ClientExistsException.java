package co.za.st.exceptions;

/**
 * Created by stevy on 2017/02/26.
 */
public class ClientExistsException extends Exception {

    public ClientExistsException() {
        super("Client already exists");
    }

    public ClientExistsException(String clientName) {
        super(String.format("Client already exists: ", clientName));
    }
}
