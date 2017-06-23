package hello;

/**
 * Created by Javier on 07/04/2017.
 */
public class Significado
{
   private String contexto;
    private String texto;

    public String getContexto() {
        return contexto;
    }

    public Significado(String contexto, String texto) {
        this.contexto = contexto;
        this.texto = texto;
    }

    public Significado() {
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
