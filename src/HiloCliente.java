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

                                File ficheroDestino = new File("C:\\Users\\USUARIO\\IdeaProjects\\AplicacionClienteServidor\\src\\ArchivoServidor\\" + nombre);
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

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        case "Descargar":
                            //Crear hilo swingworker que notifique que va a enviar un fichero


                            System.out.println("Pulsado descarga");
                            try {
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

                                List<Transferencia> lista = Collections.synchronizedList(new ArrayList<>());
                                String ruta = "C:\\Users\\USUARIO\\IdeaProjects\\AplicacionClienteServidor\\src\\ArchivoServidor\\";
                                File directorio = new File(ruta);
                                String[] nombresArchivos = directorio.list();
                                File[] rutas = directorio.listFiles();
                                for (int i=0; i<nombresArchivos.length; i++) {
                                    Transferencia transferencia = new Transferencia(nombresArchivos[i],rutas[i]);
                                    lista.add(transferencia);
                                }
                                for (int i=0; i<lista.size(); i++) {
                                    System.out.println(lista.get(i));
                                }
                                System.out.println("Lista impresa");
                                salidaServidor.writeObject(lista);
                                salidaServidor.reset();

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
