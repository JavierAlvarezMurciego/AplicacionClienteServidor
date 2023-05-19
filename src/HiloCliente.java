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
                                //Se queda aqui parado
                                this.entradaCliente = new ObjectInputStream(cliente.getInputStream());
                                System.out.println("creado object input stream");

                                long fileSize = entradaCliente.readLong();
                                System.out.println("leido tamaño del fichero "+fileSize+" bytes");

                                String nombre = "error.txt";
                                try {
                                    nombre = (String) entradaCliente.readObject();
                                    System.out.println("leido nombre del fichero "+nombre);
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                                File ficheroDestino = new File("C:\\Users\\javia\\Downloads\\"+nombre);
                                System.out.println("creado fichero destino");
                                FileOutputStream escritorFichero = new FileOutputStream(ficheroDestino);
                                System.out.println("creado file output stream");

                                byte[] buffer = new byte[1024];
                                int bytesLeidos;
                                long totalLeido = 0;
                                while( totalLeido < fileSize && (bytesLeidos = entradaCliente.read(buffer)) > 0 ){
                                    escritorFichero.write(buffer, 0, bytesLeidos);
                                    totalLeido += bytesLeidos;
                                    System.out.println("descargando fichero");
                                }
                                escritorFichero.close();
                                entradaCliente.close();

                                System.out.println(ficheroDestino.getName());

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "Descargar":
                            try {
                                this.entradaCliente = new ObjectInputStream(cliente.getInputStream());
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
