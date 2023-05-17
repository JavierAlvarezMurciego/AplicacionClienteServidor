import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Controlador implements ActionListener {
    private Servidor servidor;
    private Cliente cliente;
    private File transferencia;
    private File fichDescarga;
    ObjectInputStream entradaCliente;
    ObjectOutputStream salidaCliente;
    PrintWriter salidaClientePrint;
    private Socket socket;
    private List<Transferencia> lista;
    private DefaultListModel<String> listModel;

    public Controlador(Servidor servidor){
        this.servidor = servidor;
        vincularServidor(this);
    }

    public Controlador(Cliente cliente) {
        this.cliente = cliente;
        vincularCliente(this);
    }

    private void vincularCliente(ActionListener listener){
        cliente.getbConectar().addActionListener(listener);
        cliente.getbSubir().addActionListener(listener);
        cliente.getbDescargar().addActionListener(listener);
    }

    private void vincularServidor(ActionListener listener){

        servidor.getbIniciar().addActionListener(listener);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String elige=e.getActionCommand();
        switch (elige){
            case "Iniciar":
                SwingWorker<List<Transferencia>, Void> worker = new SwingWorker<List<Transferencia>, Void>() {
                    @Override
                    protected List<Transferencia> doInBackground() throws Exception {
                        try {
                            ServerSocket serverSocket = new ServerSocket(55555);
                            do {
                                System.out.println("Se inicia el servidor");
                                socket= serverSocket.accept();
                                HiloCliente hiloCliente = new HiloCliente(socket);
                                System.out.println("Cliente conectado");
                                hiloCliente.start();
                            }while (!serverSocket.isClosed());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }

                        //ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                        //ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());

                        //lista = (List<Transferencia>) entrada.readObject();

                        //return lista;
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            for(int i=0; i < lista.size(); i++){
                                DefaultListModel modelo = new DefaultListModel();
                                cliente.getList1().setModel(listModel);
                                modelo.addElement(lista.get(i).getNombre());
                                cliente.getList1().setModel(modelo);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
                break;
            case "Conectar":
                Thread hilo = new Thread(() -> {
                    try {
                        //Leer ip de la gui
                        System.out.println(cliente.gettextfIp().getText());
                        socket = new Socket(cliente.gettextfIp().getText(),55555);
                        //Si hacemos esto puede que no nos aseguremos de que realmente esté creado el input y outputstream
                        cliente.getbSubir().setEnabled(true);
                        cliente.getbDescargar().setEnabled(true);
                        //entradaCliente = new ObjectInputStream(socket.getInputStream());

                        try {
                            salidaClientePrint = new PrintWriter(socket.getOutputStream(), true);
                            salidaClientePrint.println("hola servidor");
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        //socket.close();


                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });
                hilo.start();
                break;

            case "Subir":
                salidaClientePrint.println("Subir");
                SwingWorker<List<Transferencia>, Void> workerSubir = new SwingWorker<List<Transferencia>, Void>() {
                    @Override
                    protected List<Transferencia> doInBackground() throws Exception {
                        JFileChooser selecciona = new JFileChooser();

                        int accion = selecciona.showSaveDialog(null);
                        System.out.println(accion);
                        if (accion == 0) {
                            //Crear hilo swingworker que notifique que va a enviar un fichero
                            transferencia = selecciona.getSelectedFile();
                            try {
                                //Descomentar siguiente linea, se queda ahi parado no se por que
                                salidaCliente = new ObjectOutputStream(socket.getOutputStream());
                                System.out.println("Creado object output");

                                salidaCliente.writeLong(transferencia.length());
                                System.out.println("Enviada longitud de archivo: "+transferencia.length());

                                FileInputStream lectorFichero = new FileInputStream(transferencia);
                                System.out.println("Creado fileinput: "+lectorFichero);

                                byte[] buffer = new byte[1024];
                                int bytesLeidos = 0;
                                System.out.println("Comienza envio del archivo");
                                while( (bytesLeidos = lectorFichero.read(buffer)) > 0){
                                    //Envio datos a través del socket
                                    salidaCliente.write(buffer, 0, bytesLeidos);
                                    //Me aseguro de que se escriben todos lo bytes del buffer
                                    salidaCliente.flush();

                                }
                                lectorFichero.close();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {

                    }
                };
                workerSubir.execute();
                break;

            case "Descargar":
                salidaClientePrint.println("Descargar");
                System.out.println("pulsado boton descargar");
                salidaClientePrint.println("hola servidor desde descargas");

                /*JFileChooser descarga = new JFileChooser();
                int evt = descarga.showSaveDialog(null);
                if (evt == JFileChooser.APPROVE_OPTION) {
                    SwingWorker<List<Transferencia>, Void> workerSubir = new SwingWorker<List<Transferencia>, Void>() {
                        @Override
                        protected List<Transferencia> doInBackground() throws Exception {
                            fichDescarga = descarga.getSelectedFile();

                            return lista;
                        }

                        @Override
                        protected void done() {
                            try {
                                for(int i=0; i < lista.size(); i++){
                                    DefaultListModel modelo = new DefaultListModel();
                                    cliente.getList1().setModel(listModel);
                                    modelo.addElement(lista.get(i).getNombre());
                                    cliente.getList1().setModel(modelo);
                                }

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    };

                }*/
                break;
            case "Refrescar":
                salidaClientePrint.println("Refrescar");
                SwingWorker<List<Transferencia>, Void> worker2 = new SwingWorker<List<Transferencia>, Void>() {
                    @Override
                    protected List<Transferencia> doInBackground() throws Exception {
                        //Buscar en el servidor los archivos disponibles
                        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                        //                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
//                    printWriter.println("Lista de elementos");
//
//                    String respuesta = bufferedReader.readLine();
//                    System.out.println("Lista de elementos"+respuesta);
                        lista = (List<Transferencia>) entrada.readObject();

                        return lista;
                    }

                    @Override
                    protected void done() {
                        try {
                            for(int i=0; i < lista.size(); i++){
                                DefaultListModel modelo = new DefaultListModel();
                                cliente.getList1().setModel(listModel);
                                modelo.addElement(lista.get(i).getNombre());
                                cliente.getList1().setModel(modelo);
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };

        }
    }
}
