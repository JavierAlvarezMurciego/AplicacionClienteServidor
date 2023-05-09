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
    }

    private void vincularServidor(ActionListener listener){

        servidor.getbIniciar().addActionListener(listener);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String elige=e.getActionCommand();
        switch (elige){
            case "Iniciar":

                try {
                    ServerSocket serverSocket = new ServerSocket(55555);
                    do {
                        System.out.println("Se inicia el servidor");
                        socket= serverSocket.accept();
                        HiloCliente hiloCliente = new HiloCliente(socket);
                        System.out.println("Cliente conectado");
                    }while (!serverSocket.isClosed());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Conectar":
                //Hilo infinito de espera de escucha a clientes

                //Cuando un cliente se conecta, crear un hilo, que se quede a la espera de recibir 3 opciones
                //-Obtener lista -Subir fichero -Descargar fichero

                //Crear hilo
                HilosCliente hilo = new HilosCliente();
                hilo.start();

                try {
                    //Leer ip de la gui
                    System.out.println(cliente.gettextfIp().getText());
                    Socket socket = new Socket(cliente.gettextfIp().getText(),55555);
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
//                    printWriter.println("Lista de elementos");
//
//                    String respuesta = bufferedReader.readLine();
//                    System.out.println("Lista de elementos"+respuesta);
                    cliente.getbSubir().setEnabled(true);
                    cliente.getbDescargar().setEnabled(true);
                    socket.close();


                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Subir":
                JFileChooser selecciona = new JFileChooser();
                int accion = selecciona.showSaveDialog(null);
                if (accion == JFileChooser.APPROVE_OPTION) {
                    transferencia = selecciona.getSelectedFile();
                }
                break;

            case "Descargar":
                JFileChooser descarga = new JFileChooser();
                int evt = descarga.showSaveDialog(null);
                if (evt == JFileChooser.APPROVE_OPTION) {
                    fichDescarga = descarga.getSelectedFile();
                }
            case "Refrescar":

                SwingWorker<List<Transferencia>, Void> worker = new SwingWorker<List<Transferencia>, Void>() {
                    @Override
                    protected List<Transferencia> doInBackground() throws Exception {
                        //Buscar en el servidor los archivos disponibles
                        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());

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
