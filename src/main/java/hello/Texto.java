package hello;

import java.util.ArrayList;

/**
 * Created by Javier on 02/07/2017.
 */
public class Texto
{


    private Complejidad complejidad;

    public Complejidad getComplejidad() {
        return complejidad;
    }

    public void setComplejidad(Complejidad complejidad) {
        this.complejidad = complejidad;
    }

    private ArrayList<Contexto> contextos;
    private ArrayList<Palabra> palabras;


    public ArrayList<Palabra> getPalabras() {
        return palabras;
    }

    public void setPalabras(ArrayList<Palabra> palabras) {
        this.palabras = palabras;
    }


    public ArrayList<Contexto> getContextos() {
        return contextos;
    }

    public void setContextos(ArrayList<Contexto> contextos) {
        this.contextos = contextos;
    }



}
