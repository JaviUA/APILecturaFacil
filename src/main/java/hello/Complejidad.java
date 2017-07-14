package hello;

/**
 * Created by Javier on 14/07/2017.
 */
public class Complejidad
{
    private int antes;
    private int despues;
    private boolean calculada;

    public int getAntes() {
        return antes;
    }

    public void setAntes(int antes) {
        this.antes = antes;
    }

    public int getDespues() {
        return despues;
    }

    public void setDespues(int despues) {
        this.despues = despues;
    }

    public boolean isCalculada() {
        return calculada;
    }

    public void setCalculada(boolean calculada) {
        this.calculada = calculada;
    }

    public Complejidad() {

        calculada=false;
    }

    public Complejidad(int antes, int despues) {
        calculada=true;
        this.antes=antes;
        this.despues=despues;
    }
}
