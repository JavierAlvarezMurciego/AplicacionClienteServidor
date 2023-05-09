import javax.swing.*;

public class Cliente {
    private JTextField textfIp;
    private JPanel pnlCliente;
    private JButton bConectar;
    private JList list1;
    private JProgressBar progressBar1;
    private JButton bDescargar;
    private JButton bSubir;
    private JButton bRefrescar;

    public JButton getbConectar() {
        return bConectar;
    }
    public JTextField gettextfIp() {
        return textfIp;
    }
    public JButton getbSubir() {
        return bSubir;
    }
    public JButton getbDescargar() {
        return bDescargar;
    }
    public JList getList1() {
        return list1;
    }


    public Cliente() {
        JFrame frame = new JFrame("Cliente");
        frame.setContentPane(pnlCliente);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new Controlador(new Cliente());
    }


}
