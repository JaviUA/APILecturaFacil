package hello;

/**
 * Created by Javier on 10/05/2017.
 */
public class Palabra
{
    private String tipo;
    private String pos;
    private String palabra;

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public Palabra(String tipo, String pos, String palabra) {
        this.tipo = tipo;
        this.pos = pos;
        this.palabra = palabra;
    }



    public String getTipo() {
        return tipo;
    }

    public Palabra(String tipo, String palabra) {
        this.tipo = tipo;
        this.palabra = palabra;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    @Override
    public boolean equals(Object other){
        boolean esIgual= false;
        if (other != null)
        {
            if (other == this)
            {
                esIgual=true;
            }
            else
            {
                if ((other instanceof Palabra))
                {
                    Palabra auxPalabra = (Palabra)other;

                    if(auxPalabra.getPalabra().equals(this.getPalabra()))
                    {
                        esIgual=true;
                    }
                }
            }
        }
        return esIgual;


    }
}
