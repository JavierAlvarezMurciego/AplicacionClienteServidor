import java.io.*;
import java.net.Socket;

public class HiloCliente extends Thread {
    private Socket cliente;
    private ObjectInputStream entradaCliente;
    private ObjectOutputStream salidaCliente;
    private BufferedReader entrada;

    public HiloCliente(Socket cliente) {
        this.cliente = cliente;
    }
    @Override
    public void run() {

        try {
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            while (true) {
                String mensajeRecibido = entrada.readLine();
                if(mensajeRecibido != null) {
                    switch(mensajeRecibido) {
                        case "Subir":
                            try {
                                File ficheroDestino = new File("C:\\Users\\javia\\Documents\\documento3");
                                FileOutputStream escritorFichero = new FileOutputStream(ficheroDestino);

                                this.entradaCliente = new ObjectInputStream(cliente.getInputStream());

                                long fileSize = entradaCliente.readLong();

                                byte[] buffer = new byte[1024];
                                int bytesLeidos;
                                long totalLeido = 0;
                                while( totalLeido < fileSize && (bytesLeidos = entradaCliente.read(buffer)) > 0 ){
                                    escritorFichero.write(buffer, 0, bytesLeidos);
                                    totalLeido += bytesLeidos;
                                }
                                escritorFichero.close();

                                System.out.println(ficheroDestino.getName());

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "Descargar":
                            try {
                                this.salidaCliente = new ObjectOutputStream(cliente.getOutputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "Refrescar":
                            try {
                                this.salidaCliente = new ObjectOutputStream(cliente.getOutputStream());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
