import java.io.File;
import java.io.Serializable;

public class Transferencia implements Serializable {
    private String nombre;
    private File ruta;

    public Transferencia(String nombre, File ruta) {
        this.nombre = nombre;
        this.ruta = ruta;
    }

    public String getNombre() {
        return nombre;
    }

    public File getRuta() {
        return ruta;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
