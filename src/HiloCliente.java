import java.net.Socket;

public class HiloCliente extends Thread {
    private Socket cliente;

    public HiloCliente(Socket cliente) {
        this.cliente = cliente;
    }
}
