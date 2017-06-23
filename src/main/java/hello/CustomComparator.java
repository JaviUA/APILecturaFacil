package hello;

import java.util.Comparator;

/**
 * Created by Javier on 06/04/2017.
 */
public class CustomComparator implements Comparator<Contexto> {
    @Override
    public int compare(Contexto object1, Contexto object2) {
        if(object1.getValor() > object2.getValor())
        {
            return -1;
        }
        else
        {
            return 1;
        }

    }
}
