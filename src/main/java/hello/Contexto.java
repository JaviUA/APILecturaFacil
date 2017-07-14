package hello;

/**
 * Created by Javier on 05/04/2017.
 */
public class Contexto
{
    private double valor;
    private String nombre;


    public double getValor() {

        return (double)Math.round(valor * 1000d) / 1000d;
    }

    public Contexto() {

    }
    public Contexto(double valor, String nombre) {
        this.valor = valor;
        this.nombre = nombre;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void addValor(double valor){
        this.valor=this.valor+valor;
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
                if ((other instanceof Contexto))
                {
                    Contexto auxContexto = (Contexto)other;

                    if(auxContexto.getNombre().equals(this.getNombre()))
                    {
                        esIgual=true;
                    }
                }
            }
        }
        return esIgual;


    }
}
