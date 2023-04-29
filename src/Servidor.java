import javax.swing.*;

public class Servidor {
    private JPanel pnl;
    private JButton bIniciar;
    private JList list1;
    private JList list2;

    public JButton getbIniciar() {
        return bIniciar;
    }

    public Servidor() {
        JFrame frame = new JFrame("Servidor");
        frame.setContentPane(pnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Controlador(new Servidor());

    }
}
