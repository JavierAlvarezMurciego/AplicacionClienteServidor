import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HiloCliente extends Thread {
    private Socket cliente;
    private ObjectInputStream entradaCliente;
    private ObjectOutputStream salidaServidor;
    private BufferedReader entrada;
    private List<Transferencia> lista;

    public HiloCliente(Socket cliente, List<Transferencia> lista) {

        this.cliente = cliente;
        this.lista = lista;
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

                                File ficheroDestino = new File( nombre);
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
                                //escritorFichero.close();
                                //entradaCliente.close();

                                System.out.println(ficheroDestino.getName());

                                lista.add(new Transferencia(ficheroDestino.getName(),ficheroDestino));

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "Descargar":
                            System.out.println("Pulsado descarga");
                            try {
                                //readLine lee un String del socket
                                File transferencia2 = new File(entrada.readLine());

                                salidaServidor = new ObjectOutputStream(cliente.getOutputStream());
                                System.out.println("Creado object output");

                                salidaServidor.writeLong(transferencia2.length());
                                System.out.println("Enviada longitud de archivo: "+transferencia2.length());

                                salidaServidor.writeObject(transferencia2.getName());
                                System.out.println("Enviado nombre de archivo: "+transferencia2.getName());

                                FileInputStream lectorFichero = new FileInputStream(transferencia2);
                                System.out.println("Creado fileinput: "+lectorFichero);

                                byte[] buffer = new byte[1024];
                                int bytesLeidos = 0;
                                System.out.println("Comienza envio del archivo");
                                while( (bytesLeidos = lectorFichero.read(buffer)) > 0){
                                    //Envio datos a través del socket
                                    salidaServidor.write(buffer, 0, bytesLeidos);
                                    //Me aseguro de que se escriben todos lo bytes del buffer
                                    salidaServidor.flush();

                                }
                                lectorFichero.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            break;
                        case "Refrescar":
                            try {
                                this.salidaServidor = new ObjectOutputStream(cliente.getOutputStream());
                                System.out.println("Creado objeto output");
                                //Se envía el objeto lista al cliente
                                salidaServidor.writeObject(lista);
                                //salidaServidor.reset();

                            } catch (Exception e) {
                                e.printStackTrace();
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
