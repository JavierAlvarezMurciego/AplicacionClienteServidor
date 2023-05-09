import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HiloRefrescar extends Thread {
    private Socket socket;
    public HiloRefrescar (Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());

            List<Transferencia> lista = (List<Transferencia>) entrada.readObject();
            for(Transferencia transferencia : lista){
                //Realmente hay que rellenar el jlist, no hacer un println
                //Al tocar un jlist, que forma parte de la GUI, tenemos que controlar el hilo EDT
                System.out.println(transferencia);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
