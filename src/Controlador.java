import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;

public class Controlador implements ActionListener {
    private Servidor servidor;
    private Cliente cliente;
    private File transferencia;
    private File fichDescarga;

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
                        Socket socket= serverSocket.accept();
                        System.out.println("Cliente conectado");
                    }while (!serverSocket.isClosed());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                break;
            case "Conectar":
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
        }
    }
}
