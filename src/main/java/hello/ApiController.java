package hello;


import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/api")
public class ApiController {




    @RequestMapping(value = "/calcularContextos", method = POST)
    @ResponseBody
    public ArrayList<Contexto> calcularContextos(@RequestBody String body) {


        ArrayList<Contexto> contextos = new ArrayList<Contexto>();

        System.out.println("Calcular contextos de: "+body);
        Connection c = null;
        Statement stmt = null;


            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:diccionario.db");
                c.setAutoCommit(false);
                System.out.println("Opened database successfully");

                stmt = c.createStatement();


                String[] palabras = body.split(" ");
                int numPalabras = palabras.length;

                double contador;
                double valor;
                String palabra;


                for (int i = 0; i < numPalabras; i++) {

                    palabra = palabras[i];

                    if (palabra.endsWith(",")) {
                        palabra = palabra.substring(0, palabra.length() - 1);
                    } else if (palabra.endsWith(".")) {
                        palabra = palabra.substring(0, palabra.length() - 1);
                    }

                    if (!esArticulo(palabra) && !esPreposicion(palabra) && palabra.length() > 1 && !esConjuncion(palabra)) {
                        contador = 0;

                        ResultSet rs = stmt.executeQuery("SELECT significadotocontexto.contexto as contexto  FROM SIGNIFICADOTOCONTEXTO, PALABRATOSIGNIFICADO, SIGNIFICADO WHERE SIGN=SIGNID AND PAL='" + palabra + "' AND SIGNIFICADO.ID=SIGNID;");


                        while (rs.next()) {

                            contador++;
                            valor = 1 / contador;


                            String contexto = rs.getString("contexto");

                            Contexto cont = new Contexto(valor, contexto);

                            if (!contextos.contains(cont)) {
                                contextos.add(cont);
                            } else {
                                int pos = contextos.indexOf(cont);
                                contextos.get(pos).addValor(valor);
                            }
                            System.out.println(palabra);
                            System.out.println(contexto);
                        }

                        //palabra en plural
                        if (contador == 0 && palabras[i].endsWith("s")) {
                            rs = stmt.executeQuery("SELECT significadotocontexto.contexto as contexto FROM SIGNIFICADOTOCONTEXTO, PALABRATOSIGNIFICADO, SIGNIFICADO" +
                                    " WHERE SIGN=SIGNID AND PAL='" + palabra.substring(0, palabra.length() - 1) + "' AND SIGNIFICADO.ID=SIGNID;");
                            while (rs.next()) {

                                contador++;
                                valor = 1 / contador;


                                String contexto = rs.getString("contexto");

                                Contexto cont = new Contexto(valor, contexto);


                                if (!contextos.contains(cont)) {
                                    contextos.add(cont);
                                } else {
                                    int pos = contextos.indexOf(cont);
                                    contextos.get(pos).addValor(valor);
                                }


                                System.out.println(contexto);
                            }
                        }
                    }


                }

                Collections.sort(contextos, new CustomComparator());

                stmt.close();
                c.commit();
                c.close();
                System.out.println("Cierra database");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }



        return contextos;
    }

    @RequestMapping(value = "/calcularContextosModificado", method = POST)
    @ResponseBody
    public ArrayList<Contexto> calcularContextosModificado(@RequestBody ArrayList<Palabra> palabras) {

        String texto=devuelveFrase(palabras);
        ArrayList<Contexto> contextos=calcularContextos(texto);

        return contextos;
    }

    @RequestMapping(value = "/calcularComplejidad", method = POST)
    @ResponseBody
    public int calcularComplejidad(@RequestBody String body)
    {

      return calcularComplejidadSimple(body);
    }

    @RequestMapping(value = "/calcularComplejidadModificado", method = POST)
    @ResponseBody
    public int calcularComplejidad(@RequestBody ArrayList<Palabra> palabras)
    {
          String texto=devuelveFrase(palabras);
         texto= texto.replace("|","\n").replace(".",".\n");
        return calcularComplejidadSimple(texto);
    }

    public int calcularComplejidadSimple(String texto)
    {
        double numComas = 0;
        double numPuntosAparte = 0;
        double numPalTriPoli = 0;
        double numPuntos = 0;
        double numPalabras = 0;


        String aux = texto.replace("\n", "");
        numPuntosAparte = texto.length() - aux.length();

        if(!texto.endsWith(".")) //Cuando se introduce un texto sin punto al final
        {
            texto=texto+".";
        }

        String[] palabras = texto.replace("\n"," ").split(" ");
        numPalabras = palabras.length;

        double contador;
        double valor;
        String palabra;

        for (int i = 0; i < numPalabras; i++) {

            palabra = palabras[i];
            //Parte calculo de variables para la complejidad del texto
            if (palabra.endsWith(",")) {
                numComas++;
                palabra = palabra.substring(0, palabra.length() - 1);
            } else if (palabra.endsWith(".")) {

                numPuntos++;
                palabra = palabra.substring(0, palabra.length() - 1);
            }

            SeparaSilabas separador = new SeparaSilabas(palabra);

            if(separador.silabear()>2)
            {
                numPalTriPoli++;
            }
        }

        double numFrases=numComas+numPuntos;
        numPuntosAparte++;

        System.out.println("Puntos aparte: " +numPuntosAparte);
        System.out.println("Palabras: "+numPalabras);
        System.out.println("Comas: "+numComas);
        System.out.println("Frases: "+numFrases);


        double x1 = ((numComas * 100) / numPalabras) * 10;
        double x2 = ((numPuntosAparte * 100) / numPalabras) * 10;
        double x3 = numPalabras / numFrases;
        double x4 = ((numPalTriPoli * 100) / numPalabras) * 10;

        double indiceDif = 67.069 - (0.103 * x1) + (0.219 * x2) - (0.779 * x3) - (0.080 * x4);

        int res=(int)indiceDif;

        return res;
    }

    @RequestMapping(value = "/significado", method = POST)
    @ResponseBody
    public String devuelveSignificado(@RequestBody ArrayList<Contexto> contextos, String palabra) {

        String cont = "";
        boolean acabaS = palabra.endsWith("s");
        String result = "(No encontrada)";

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:diccionario.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();


            System.out.println(cont);
            ResultSet rs = stmt.executeQuery("SELECT significado.texto as resultado, SIGNIFICADOTOCONTEXTO.contexto as context" +
                    " FROM SIGNIFICADOTOCONTEXTO, PALABRATOSIGNIFICADO, " +
                    "SIGNIFICADO WHERE SIGN=SIGNID AND PAL='" + palabra + "' AND SIGNIFICADO.ID=SIGNID;");


            ArrayList<Significado> significados = new ArrayList<Significado>();
            if (rs.next()) {
                do {
                    String resultado = rs.getString("resultado");
                    String context = rs.getString("context");
                    System.out.println(context);
                    Significado sign = new Significado(context, resultado);
                    significados.add(sign);
                } while (rs.next());
            } else if (acabaS) //Para los plurales
            {
                rs = stmt.executeQuery("SELECT significado.texto as resultado, SIGNIFICADOTOCONTEXTO.contexto as context" +
                        " FROM SIGNIFICADOTOCONTEXTO, PALABRATOSIGNIFICADO, " +
                        "SIGNIFICADO WHERE SIGN=SIGNID AND PAL='" + palabra.substring(0, palabra.length() - 1) + "' AND SIGNIFICADO.ID=SIGNID;");
                while (rs.next()) {
                    String resultado = rs.getString("resultado");
                    String context = rs.getString("context");
                    System.out.println(context);
                    Significado sign = new Significado(context, resultado);
                    significados.add(sign);
                }

            }

            if (!significados.isEmpty()) {
                double val = 0;
                double aux;
                String contActual;

                for (int i = 0; i < significados.size(); i++) {
                    contActual = significados.get(i).getContexto();

                    Contexto ct = new Contexto(0, contActual);
                    aux = contextos.get(contextos.indexOf(ct)).getValor();
                    if (aux > val) {
                        result = "(" + contActual + ")" + significados.get(i).getTexto();
                        val = aux;
                    }
                    System.out.println(val);

                }
            }


            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }


        return result;
    }

    @RequestMapping(value = "/significado2")
    @ResponseBody
    public ArrayList<Significado> devuelveSignificado2( String palabra) {

        ArrayList<Significado> significados;
        System.out.println("Buscando palabra: "+palabra);

       significados= significadoDiccionarioFacil(palabra);
       if(significados.isEmpty())
       {
           significados=significadoDiccionario(palabra);
       }

      return significados;
    }

    public boolean esArticulo(String texto) {
        return (texto.equals("el") || texto.equals("la") || texto.equals("los") || texto.equals("las") || texto.equals("un") || texto.equals("uno")
                || texto.equals("una") || texto.equals("unos") || texto.equals("unas") || texto.equals("al") || texto.equals("del"));
    }


    public boolean esPreposicion(String texto) {
        return (texto.equals("a") || texto.equals("ante") || texto.equals("bajo") || texto.equals("con") || texto.equals("de") || texto.equals("desde")
                || texto.equals("durante") || texto.equals("en") || texto.equals("entre") || texto.equals("excepto") || texto.equals("hacia")
                || texto.equals("hasta") || texto.equals("mediante") || texto.equals("para") || texto.equals("por") || texto.equals("salvo")
                || texto.equals("según") || texto.equals("sin") || texto.equals("sobre") || texto.equals("tras"));
    }


    public boolean esConjuncion(String texto) {
        return (texto.equals("y") || texto.equals("o") || texto.equals("pero") || texto.equals("mas") || texto.equals("sino") || texto.equals("si")
                || texto.equals("ni") || texto.equals("que") || texto.equals("aunque"));
    }

    @RequestMapping(value = "/convertirTexto", method = POST)
    @ResponseBody
    public Texto convertirTexto(@RequestBody String body) {


        Boolean introdPunto=false;
        String nuevoTexto="";
        body=body.replace(";", ",").replace("{", "(").replace("}", ")").replace("&", "y").replace("%", " por ciento");
        body=body.replace("\n", "").replace("\r", "");

        //Separacion del texto por .
        String[] textos = body.split("\\.");

       for(int i=0; i<textos.length;i++)
        {
            String texto=textos[i];


            //System.out.println(i+ "i "+ texto);

            ArrayList<String> tipos=new ArrayList<>();
            if(texto.contains(","))
            {
                String frases[]=texto.split("\\,");
                for(int j=0;j<frases.length;j++)
                {


                    String url="undefined";
                    boolean precedidaSer=false;

                    boolean pasiva=false;
                    tipos.clear();
                    String frase=frases[j];

                  System.out.println(j+ "j"+ frase);
                    String palabras[]=frase.split(" ");
                    String vSer="";

                    //Guardar el tipo de cada palabra
                    for(int k=0;k<palabras.length;k++)
                    {
                       // System.out.println(k+ "k"+palabras[k]);

                        if(precedidaSer)
                        {
                            String pal=quitarFlexion(palabras[k]);
                            url=esParticipio(pal);
                            System.out.println(url);
                            if(!url.equals("undefined"))
                            {
                                pasiva=true;
                            }


                        }

                        if(perteneceVSer(palabras[k]))
                        {
                            if(palabras[k].equals("sido") && (k-1)>=0) //verbo ser compuesto
                            {
                                vSer=palabras[k-1]+" "+palabras[k];
                            }
                            else
                            {
                                vSer=palabras[k];
                            }

                            precedidaSer=true;
                        }
                        else
                        {

                            precedidaSer=false;
                        }

                        switch (palabras[k]) {
                            case "y":
                                tipos.add("conj.");
                                break;
                            case "que":
                                tipos.add("conj.");
                                break;
                            default:
                                tipos.add(llamaDiccionario(palabras[k]));
                                break;
                        }
                    }

                    if(pasiva==true)
                    {

                        if(j==0)
                        {
                            if(!frase.startsWith(" ")) //Viene de punto y aparte
                            {
                                frase=Character.toLowerCase(frase.charAt(0))+frase.substring(1,frase.length()); //Pasar a minúscula el C Ag
                                frase=" "+frase;
                            }
                            else
                            {
                                frase=Character.toLowerCase(frase.charAt(1))+frase.substring(2,frase.length()); //Pasar a minúscula el C Ag
                                frase=" "+frase;
                            }

                            frase=cambiaActiva(frase, url, vSer);
                            frase=Character.toUpperCase(frase.charAt(0))+frase.substring(1,frase.length()); //Cambiar la primera letra a mayúscula
                        }
                        else
                        {
                            frase = " " + cambiaActiva(frase, url, vSer);
                        }
                    }



                    System.out.println(frase+ " tam tipos: "+tipos.size());
                    if(j!=0)
                    if((tipos.contains("conj.") && tipos.size() <= 4) || tipos.size()==2)
                    {
                        //System.out.println("Texto cuando reemplaza: "+nuevoTexto);
                       // System.out.println("tamaño del texto: "+nuevoTexto.length());
                        nuevoTexto=nuevoTexto.substring(0,nuevoTexto.length()-1)+",";
                        introdPunto=false;
                      //  System.out.println("Texto despues de que reemplaza: "+nuevoTexto);
                    }



                    if(j==frases.length-1) //La ultima frase es . siempre
                    {
                        if(introdPunto) {
                            nuevoTexto = nuevoTexto + Character.toUpperCase(frase.charAt(1))+ frase.substring(2,frase.length()) + ".";
                        } else {
                            nuevoTexto = nuevoTexto + frase + ".";
                        }
                    }
                    else if(!tipos.get(tipos.size()-1).equals("conj."))
                    {
                        //Si contiene un sujeto y un verbo, se introduce punto
                        if (((tipos.contains("f.") || tipos.contains("m.") || tipos.contains("pron.")) && (tipos.contains("intr.") || tipos.contains("tr.")))
                                || j==(frases.length-1))
                        {

                            //Mayúsculas cuando se introdujo un punto
                            if(introdPunto) {
                                nuevoTexto = nuevoTexto + Character.toUpperCase(frase.charAt(1))+ frase.substring(2,frase.length()) + ".";
                            } else {
                                nuevoTexto = nuevoTexto + frase + ".";
                            }

                            System.out.println("Introduciendo frase con punto: "+frase);
                            introdPunto=true;
                        }
                        else
                        {

                            if(introdPunto) {
                                nuevoTexto = nuevoTexto + Character.toUpperCase(frase.charAt(1))+ frase.substring(2,frase.length()) + ",";
                            } else {
                                nuevoTexto = nuevoTexto + frase + ",";
                            }
                            introdPunto=false;
                            System.out.println("Introduciendo frase con , por frase sin sujeto+verbo: "+frase);

                        }
                    }
                    else //Si la última palabra es una conjunción
                    {
                        if(introdPunto) {
                            nuevoTexto = nuevoTexto + Character.toUpperCase(frase.charAt(1))+ frase.substring(2,frase.length()) + ",";
                        } else {
                            nuevoTexto = nuevoTexto + frase + ",";
                        }
                        introdPunto=false;

                    }
                }

            }
            else //El texto no contiene "," Solo una oración hasta el "."
            {
                String palabras[]=texto.split(" ");
                String vSer="";
                boolean precedidaSer=false;
                boolean pasiva=false;
                String url="undefined";


                for(int k=0;k<palabras.length;k++)
                {

                    if(precedidaSer)
                    {
                        String pal=quitarFlexion(palabras[k]);
                        url=esParticipio(pal);
                        System.out.println(url);
                        if(!url.equals("undefined"))
                        {
                            pasiva=true;
                        }

                    }

                    if(perteneceVSer(palabras[k]))
                    {



                        if(palabras[k].equals("sido") && (k-1)>=0)
                        {
                            vSer=palabras[k-1]+" "+palabras[k];
                        }
                        else
                        {
                            vSer=palabras[k];
                        }

                        precedidaSer=true;
                    }
                    else
                    {
                        precedidaSer=false;
                    }

                    switch (palabras[k]) {
                        case "y":
                            tipos.add("conj.");
                            break;
                        case "que":
                            tipos.add("conj.");
                            break;
                        default:
                            tipos.add(llamaDiccionario(palabras[k]));
                            break;
                    }
                }

                if(pasiva==true)
                {

                    System.out.println("Unica frase: "+texto);

                    if(!texto.startsWith(" "))
                    {
                        texto=Character.toLowerCase(texto.charAt(0))+texto.substring(1,texto.length()); //Pasar a minúscula el C Ag
                        texto=" "+texto;
                    }
                    else
                    {
                        texto=Character.toLowerCase(texto.charAt(1))+texto.substring(2,texto.length()); //Pasar a minúscula el C Ag
                        texto=" "+texto;
                    }


                    texto=cambiaActiva(texto, url, vSer);
                    System.out.println("Frase obtenida: "+texto);
                    texto=Character.toUpperCase(texto.charAt(0))+texto.substring(1,texto.length()); //Cambiar la primera letra a mayúscula

                }

                nuevoTexto=nuevoTexto+texto+".";
            }

        }

       ArrayList<Palabra> pals=separaPalabras(nuevoTexto, true);
       Texto texto=new Texto();
       texto.setPalabras(pals);
        return texto;
    }



    @RequestMapping(value = "/convertirTexto2", method = POST)
    @ResponseBody
    public Texto convertirTexto2(@RequestBody String body, boolean puntuacion, boolean sinonimos, boolean pasivas, boolean complejidad, boolean contextos)
    {


        Texto texto=new Texto();
        int compAntes=0;

        if(complejidad)
        {
           compAntes=calcularComplejidadSimple(body);
        }



        String nuevoTexto="";
        body=body.replace(";", ",").replace("{", "(").replace("}", ")").replace("&", "y").replace("%", " por ciento").replace("|","");
        System.out.println(body);
        body=body.replace("\n", "|");
        System.out.println("cuerpo: "+body);
        ArrayList<Palabra> palabras= new ArrayList<Palabra>();


        ArrayList<String> resultados=separaFreeling(body);

        for(int j=0;j<resultados.size();j++)
        {
            System.out.println("Separaciones de texto: " +resultados.get(j));
            palabras.addAll(llamaFreeling(resultados.get(j)));

        }


        if(sinonimos)
        {

            for(int i=0;i<palabras.size();i++)
            {
                if(palabras.get(i).getPos().equals("noun") || palabras.get(i).getPos().equals("adjective"))
                {
                    Palabra nueva=palabras.get(i);
                    String cambiada=cambiaSinonimo(palabras.get(i).getPalabra());


                    if(!cambiada.equals(nueva.getPalabra()))
                    {

                        if(palabras.get(i).getPos().equals("adjective") && i>0 &&palabras.get(i).getGen().equals("common")) //Cuando el adjetivo no contiene género
                        {
                           if(palabras.get(i-1).getPos().equals("noun"))
                           {
                               nueva.setGen(palabras.get(i-1).getGen()); //Se añade el género del sustantivo anterior
                               cambiada = cambiaFlexion(cambiada, nueva);
                               nueva.setPalabra(cambiada);
                               palabras.set(i, nueva);
                           }
                        }
                        else
                        {
                            cambiada = cambiaFlexion(cambiada, nueva);
                            nueva.setPalabra(cambiada);
                            palabras.set(i, nueva);
                        }

                    }

                }

            }
        }

        if(puntuacion)
        {
            palabras=cambiaPuntuacion(palabras);
        }

        nuevoTexto=devuelveFrase(palabras);

        if(pasivas)
        {
            nuevoTexto=cambiaTextoPasivas(nuevoTexto);

        }


        System.out.println("Obtenido al final"+nuevoTexto);

        ArrayList<Palabra> pals=separaPalabras(nuevoTexto, puntuacion);


        if(complejidad)
        {
            Complejidad comp=new Complejidad(compAntes,calcularComplejidad(pals));
            texto.setComplejidad(comp);
        }
        else
        {
            texto.setComplejidad(new Complejidad());
        }

        texto.setPalabras(pals);

        return texto;
    }



    public  String cambiaTextoPasivas(String texto)
    {
        String frases[]=texto.split("\\.");
        texto="";
        boolean paragrafo=false;

        for(int j=0;j<frases.length;j++)
        {
            String frase=frases[j];
            System.out.println("Frase en bucle pasiva:"+frase);

            if(frase.contains(","))
            {
               String frases2[]= frase.split("\\,");
                for(int i=0;i<frases2.length;i++)
                {
                    String fraseComa=frases2[i];

                    String res[]=esPasiva(fraseComa);
                    if(!res[0].equals("undefined"))
                    {
                        String url=res[0];
                        String vSer=res[1];
                        paragrafo=false;
                        System.out.println("Frase hasta coma:"+fraseComa);

                        if(fraseComa.startsWith("|")) //Guardar simbolo para salto
                        {
                            fraseComa=fraseComa.replace("|","");
                            paragrafo=true;
                            System.out.println("llega alguna vez?");
                        }

                        if(!fraseComa.startsWith(" "))
                        {
                            fraseComa=Character.toLowerCase(fraseComa.charAt(0))+fraseComa.substring(1,fraseComa.length()); //Pasar a minúscula el C Ag
                            fraseComa=" "+fraseComa;
                        }
                        else
                        {
                            fraseComa=Character.toLowerCase(fraseComa.charAt(1))+fraseComa.substring(2,fraseComa.length()); //Pasar a minúscula el C Ag
                            fraseComa=" "+fraseComa;
                        }


                        fraseComa=cambiaActiva(fraseComa, url, vSer);
                        System.out.println("fraseComa obtenidad: "+fraseComa);
                        if(i==0)
                        fraseComa=" "+Character.toUpperCase(fraseComa.charAt(0))+fraseComa.substring(1,fraseComa.length()); //Cambiar la primera letra a mayúscula
                        else
                        {
                            fraseComa=" "+fraseComa;
                        }

                        if(paragrafo)
                            fraseComa="|"+fraseComa;

                    }

                    if(i<frases2.length-1)
                        texto=texto+fraseComa+",";
                    else
                        texto=texto+fraseComa+".";
                }
            }
            else
            {
                String res[]=esPasiva(frase);
                paragrafo=false;
                if(!res[0].equals("undefined"))
                {
                    String url=res[0];
                    String vSer=res[1];

                    System.out.println("Unica frase: "+frase);


                    if(frase.startsWith("|")) //Guardar simbolo para salto
                    {
                        frase=frase.replace("|","");
                        paragrafo=true;
                        System.out.println("llega alguna vez?");
                    }

                    if(!frase.startsWith(" "))
                    {
                        frase=Character.toLowerCase(frase.charAt(0))+frase.substring(1,frase.length()); //Pasar a minúscula el C Ag
                        frase=" "+frase;
                    }
                    else
                    {
                        frase=Character.toLowerCase(frase.charAt(1))+frase.substring(2,frase.length()); //Pasar a minúscula el C Ag
                        frase=" "+frase;
                    }


                    frase=cambiaActiva(frase, url, vSer);
                    System.out.println("Frase obtenidad: "+frase);
                    frase=" "+Character.toUpperCase(frase.charAt(0))+frase.substring(1,frase.length()); //Cambiar la primera letra a mayúscula

                    if(paragrafo)
                        frase="|"+frase;
                }

                texto=texto+frase+".";


            }





        }

        return texto;

    }

    public String[] esPasiva(String frase)
    {
        String pals[]=frase.split(" ");
        boolean precedidaSer=false;
        String vSer="";
        boolean pasiva=false;
        String url="undefined";

        for(int k=0;k<pals.length;k++)
        {

            if(precedidaSer)
            {
                String pal=quitarFlexion(pals[k]);
                System.out.println("Despues de quitar flexion"+pal);
                url=esParticipio(pal);
                System.out.println(url);
                if(!url.equals("undefined"))
                {
                    pasiva=true;
                }

            }

            if(perteneceVSer(pals[k]))
            {

                if(pals[k].equals("sido") && (k-1)>=0)
                {
                    vSer=pals[k-1]+" "+pals[k];
                }
                else
                {
                    vSer=pals[k];
                }

                precedidaSer=true;
            }
            else
            {
                precedidaSer=false;
            }


        }

        String[] resultado=new String[2];
        resultado[0]=url;
        resultado[1]=vSer;
        return resultado;
    }

    public static ArrayList<Palabra> separaPalabras(String texto, boolean puntuacion)
    {
        ArrayList<Palabra> palabrasArray=new ArrayList<Palabra>();

        if(puntuacion)
        {
            texto=texto.replace(".",".|");
           // texto=texto.replace("||","|");
        }



        texto=texto.replace("."," .").replace(","," ,").replace("|", " |");
        String[] palabras=texto.split(" ");
        Palabra pal;
        pal=new Palabra("first",palabras[0]);
        palabrasArray.add(pal);
        for(int i=1;i<palabras.length;i++)
        {
            switch(palabras[i])
            {
                case ",":
                    pal=new Palabra("comma",palabras[i]);
                    palabrasArray.add(pal);
                    break;
                case ".":
                    pal=new Palabra("period",palabras[i]);
                    palabrasArray.add(pal);
                    break;
                case "|":
                    System.out.println("Llega a other");
                    pal=new Palabra("other",palabras[i]);
                    palabrasArray.add(pal);
                    break;
                default:
                    pal=new Palabra("common",palabras[i]);
                    palabrasArray.add(pal);
                    break;

            }
        }
        return palabrasArray;

    }

    public static ArrayList<Palabra> cambiaPuntuacion(ArrayList<Palabra> palabras)
    {
        boolean enumeracion=false;

        Palabra pComa=new Palabra("undefined",",");
        ArrayList<Palabra> aux=new ArrayList<Palabra>();
        boolean preComaCompleta=false;

        int posComa=0;


            for(int i=0;i<palabras.size();i++)
            {
                aux.add(palabras.get(i));
                if(palabras.get(i).getPos().equals("punctuation"))
                {
                    if(preComaCompleta) //llega a un signo de puntuación despues de una coma con frase completa
                    {

                        if(esCompleta(aux)) //Tambien es completa la siguiente, así que cambio coma por punto
                        {
                            palabras.set(posComa, new Palabra("period","punctuation","." ));
                        }

                        if(palabras.get(i).getTipo().equals("comma"))
                        {
                            preComaCompleta=esCompleta(aux);
                            posComa=i;
                        }
                        else
                        {
                            preComaCompleta=false;
                        }

                    }
                    else if(palabras.get(i).getTipo().equals("comma"))
                    {
                        preComaCompleta=esCompleta(aux);
                        posComa=i;
                    }
                    else
                    {
                        preComaCompleta=false;
                    }

                    aux.clear(); //Limpia las palabras porque ha llegado a un signo de puntuación

                }

                System.out.println(palabras.get(i).getPalabra());
            }


        return palabras;


    }

    public static boolean esCompleta(ArrayList<Palabra> palabras)
    {

        boolean contieneVerbo=false;
        boolean contieneSujeto=false;


        if(!palabras.get(0).getPos().equals("conjunction")) {
            for (int i = 0; i < palabras.size(); i++) {
                if (palabras.get(i).getPos().equals("pronoun") || palabras.get(i).getPos().equals("noun")) {
                    contieneSujeto = true;
                } else if (palabras.get(i).getPos().equals("verb") && palabras.get(i).getTipo().equals("main")) {
                    contieneVerbo = true;
                }

            }
        }

        return (contieneSujeto && contieneVerbo);
    }

    public static String devuelveFrase(ArrayList<Palabra> palabras)
    {
        String frase="";
        boolean despuesPunto=false;
        String pal;
        for(int i=0;i<palabras.size();i++)
        {
            if(i!=0)
            {
                switch(palabras.get(i).getTipo())
                {
                    case "comma":
                        frase = frase +  ",";
                        despuesPunto=false;
                        break;
                    case "period":
                        frase = frase +  ".";
                        despuesPunto=true;
                        break;
                    case "other":
                        frase= frase +'|';
                        despuesPunto=true;
                        break;
                    case "article":
                        if(despuesPunto) //añadir mayúscula despues de punto
                        {
                            pal=palabras.get(i).getPalabra();
                            frase = frase + " "+Character.toUpperCase(pal.charAt(0))+pal.substring(1, pal.length());
                        }
                        else
                        {
                            if(palabras.get(i).getPalabra().equals("l")) //volver a juntar "l" separada por freeling
                            {
                                frase = frase + palabras.get(i).getPalabra();
                            }
                            else
                            {
                                frase = frase + " " + palabras.get(i).getPalabra();
                            }
                        }
                        despuesPunto=false;
                        break;
                    default:
                        if(despuesPunto) //añadir mayúscula despues de punto
                        {
                            pal=palabras.get(i).getPalabra();
                            frase = frase + " "+ Character.toUpperCase(pal.charAt(0))+pal.substring(1, pal.length());
                        }
                        else
                        {
                            frase = frase + " " + palabras.get(i).getPalabra();
                        }
                        despuesPunto=false;
                        break;
                }

            }
            else
            {
                frase = palabras.get(i).getPalabra();
            }
        }

        return frase;
    }


    public static String cambiaSinonimo(String palabra)
    {
        String resultado="";
        String sinonimo=palabra;
        try {

            Document doc;
            int veces=0;
            Elements elems;


                doc = Jsoup.connect("http://www.wordreference.com/definicion/" + palabra).get();

                elems = doc.getElementsByClass("entry");
                for (Element el : elems) {
                    resultado = resultado + el.html();
                }
                veces = StringUtils.countMatches(resultado, "<li>");

            //Si la palabra contiene un solo significado
            if(veces==1) {
                doc = Jsoup.connect("http://www.wordreference.com/sinonimos/" + palabra).get();


                elems = doc.getElementsByClass("trans clickable");
                if (!elems.isEmpty()) {
                    Element el = elems.get(0);
                    String res = el.html();

                    String partes[] = res.split("<li>");
                    String partes2[] = partes[1].split("</li>");

                    String sinonimos[] = partes2[0].split(", ");
                    System.out.println(partes[1]);
                    int tamMin = palabra.length();
                    int tam;
                    for (int i = 0; i < sinonimos.length; i++) {
                        tam = sinonimos[i].length();
                        System.out.println("Sinonimo: " + sinonimos[i] + ", tamaño: " + tam);
                        if (tam < tamMin) {

                            sinonimo = sinonimos[i];
                            tamMin = tam;
                        }
                    }


                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return sinonimo;
    }

    public static String cambiaFlexion(String cambiada, Palabra original)
    {
        String resultado="";
        System.out.println("Palabra que se quiere modificar: "+original.getPalabra());
        try {

            Document doc = Jsoup.connect("http://www.wordreference.com/definicion/"+cambiada).get();


            Elements elems=doc.select("#articleHead");
            for(Element el: elems)
            {
                resultado=resultado+el.html();
            }
            System.out.println(resultado);
            if(resultado.contains("<strong>Inflexiones</strong>")) {
                String r2[] = resultado.split("<strong>Inflexiones</strong>");

                System.out.println(r2[1]);

                String inflexiones = r2[1].replace("</span>:", "inflexion").replace("\n", "");
                System.out.println(inflexiones);

                Pattern p = Pattern.compile("^*.finflexion (\\S+).*.mplinflexion (\\S+).*.fplinflexion (\\S+)");//Cualquier inflexión posible
                Pattern p2 = Pattern.compile("^*.(\\w)plinflexion (\\S+)"); //Un solo género posible
                Pattern p3 = Pattern.compile("^*.plinflexion (\\S+)"); //Solo plural, no tiene género
                Matcher matcher = p.matcher(inflexiones);
                Matcher matcher2 = p2.matcher(inflexiones);
                Matcher matcher3 = p3.matcher(inflexiones);

                //System.out.println(resultado);
                if (matcher.find()) {


                    String f = matcher.group(1).replace(",", "");
                    String mpl = matcher.group(2).replace(",", "");
                    String fpl = matcher.group(3).replace("<br>", "").replace("<div", "");

                    if (original.getGen().equals("feminine")) {
                        if (original.getNum().equals("plural")) {
                            cambiada = fpl;
                        } else {
                            cambiada = f;
                        }
                    } else if (original.getNum().equals("plural")) {
                        cambiada = mpl;
                    }

                    System.out.println("Fem: " + f + " MPL: " + mpl + " FPL: " + fpl);
                } else if (matcher2.find()) {
                    String gen = matcher2.group(1);
                    String pl = matcher2.group(2).replace("<br>","").replace("<div","");
                    System.out.println("Genero encontrado: " + gen);
                    if (original.getGen().startsWith(gen)) {
                        if (original.getNum().equals("plural")) {
                            cambiada = pl;
                        }
                    } else //El género no concuerda, volvemos a introducir la original
                    {
                        cambiada = original.getPalabra();
                    }
                    System.out.println("PL: " + pl);
                } else if (matcher3.find()) {
                    String pl = matcher3.group(1).replace("<br>", "").replace("<div","");;
                    if (original.getNum().equals("plural")) {
                        cambiada = pl;
                    }
                }

            } else
            {
                cambiada=original.getPalabra();
            }


        } catch (IOException e) {

           e.printStackTrace();

        }

        return cambiada;
    }

    //Llama a wordreference para devolver el tipo de palabra
    public static String llamaDiccionario(String palabra) {
        String tipo="undefined";

        try {
            Document doc = Jsoup.connect("http://www.wordreference.com/definicion/"+palabra).get();


           Elements  elems=doc.getElementsByClass("entry");
            if(!elems.isEmpty()) {
                Element el = elems.get(0);
                String res[] = el.html().replace("<li>", "").split(" ");
                tipo = res[0];
                System.out.println(res[0]);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return tipo;
    }


    //Llama a freeling para obtener el etiquetado gramatical(pos tagging) de una serie de palabras en un texto
   public static ArrayList<Palabra> llamaFreeling(String texto) {


        WebDriver driver = new HtmlUnitDriver();


        ArrayList<Palabra> palabras=new ArrayList<Palabra>();
        // And now use this to visit Google
        driver.get("http://nlp.lsi.upc.edu/freeling/demo/demo.php");
        WebElement element = driver.findElement(By.name("frase"));

        System.out.println(element.getText());
        element.clear();
        element.sendKeys(texto);
        //System.out.println(element.getText());


        WebElement element2 = driver.findElement(By.cssSelector("input[value='Submit']"));



        element2.click();


        WebElement output=driver.findElement(By.className("output"));
      // WebElement sentences= output.findElement(By.id("sentences"));

        String raw=output.getAttribute("innerHTML");

        System.out.println("Raw: "+raw);
        String raw2[]=raw.split("\\<paragraph\\>");
        String frases[]=raw2[1].split("\\<\\/sentence\\>");
        String frase="";
        String token="";

        Pattern p0 = Pattern.compile("^*.token begin=(\\S+).*.end=(\\S+).*.gen=(\\S+).*.num=(\\S+).*.pos=(\\S+).*.type=(\\S+)");
        Pattern p = Pattern.compile("^*.token begin=(\\S+).*.end=(\\S+).*.pos=(\\S+).*.type=(\\S+)");
        Pattern p2 = Pattern.compile("^*.token begin=(\\S+).*.end=(\\S+).*.pos=(\\S+)");
        for(int i=0;i<frases.length-1;i++)
        {
            frase=frases[i];
            System.out.println("Frase "+i+": " +frase);
            String tokens[]=frase.split("\\<\\/token\\>");


            for(int j=0;j<tokens.length;j++)
            {
                token=tokens[j];
                String[] token2=token.split("\\<morpho\\>");

                Matcher matcher0 = p0.matcher(token2[0]);
                Matcher matcher = p.matcher(token2[0]);
                Matcher matcher2 = p2.matcher(token2[0]);

                System.out.println("Token2: "+token2[0]);

                if(matcher0.find()) {

                    int begin=Integer.parseInt(matcher0.group(1).replace("\\\"",""));
                    int end=Integer.parseInt(matcher0.group(2).replace("\\\"",""));
                    String gen=matcher0.group(3).replace("\\\"","");
                    String num=matcher0.group(4).replace("\\\"","");
                    String pos=matcher0.group(5).replace("\\\"","");
                    String tipo=matcher0.group(6).replace("\\\"","").replace(">\\n","");


                    Palabra pal=new Palabra(num, gen,tipo, pos, texto.substring(begin,end));
                    palabras.add(pal);
                }
                else if(matcher.find()) {

                    int begin=Integer.parseInt(matcher.group(1).replace("\\\"",""));
                    int end=Integer.parseInt(matcher.group(2).replace("\\\"",""));
                    String pos=matcher.group(3).replace("\\\"","");
                    String tipo=matcher.group(4).replace("\\\"","").replace(">\\n","");


                    Palabra pal=new Palabra(tipo, pos, texto.substring(begin,end));
                    palabras.add(pal);
                } else if(matcher2.find())
                {
                    int begin=Integer.parseInt(matcher2.group(1).replace("\\\"",""));
                    int end=Integer.parseInt(matcher2.group(2).replace("\\\"",""));
                    String pos=matcher2.group(3).replace("\\\"","");

                    Palabra pal=new Palabra(pos, texto.substring(begin,end));
                    palabras.add(pal);
                }

                System.out.println("Token "+j+" "+token);
            }


        }





        return palabras;
    }

    //Devuelve la definicion de una palabra en wordreference
    public static ArrayList<Significado> significadoDiccionario(String palabra) {
        String resultado="";
        ArrayList<Significado> significados=new ArrayList<Significado>();

        try {
            Document doc = Jsoup.connect("http://www.wordreference.com/definicion/"+palabra).get();


            Elements elems=doc.getElementsByClass("entry");
            for(Element el: elems)
            {
                resultado=resultado+el.html();
            }

            String defs[]=resultado.split("</li>");
            for(int i=0;i<defs.length;i++)
            {
                significados.add(new Significado("SignificadoWR",i+". "+Jsoup.parse(defs[i]).text()));

            }
           // resultado=Jsoup.parse(resultado).text();



        } catch (IOException e) {
            e.printStackTrace();
        }

        return significados;
    }

    public static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str,  Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    //Devuelve la definicion de una palabra en el diccionario de lectura fácil
    public static ArrayList<Significado> significadoDiccionarioFacil(String palabra) {
        String resultado="";
        ArrayList<Significado> significados=new ArrayList<Significado>();

        char indice=Character.toUpperCase(palabra.charAt(0));
        try {


            palabra=deAccent(palabra);


            Document doc = Jsoup.connect("http://diccionariofacil.org/diccionario/"+indice+"/"+palabra+".html").get();


            Elements elems=doc.getElementsByClass("definicionContent");
            for(Element el: elems)
            {
                resultado=resultado+el.html();
            }

            if(resultado.equals(""))
            {
                doc = Jsoup.connect("http://diccionariofacil.org/diccionario/"+indice+"/"+palabra+"-1.html").get();


                elems=doc.getElementsByClass("definicionContent");
                for(Element el: elems)
                {
                    resultado=resultado+el.html();
                }
            }

               String defs[]= resultado.split("<h4>");

                for(int i=1;i<defs.length;i++)
                {
                    System.out.println(Jsoup.parse(defs[i]).text());
                    significados.add(new Significado("SignificadoDF",Jsoup.parse(defs[i]).text()));
                }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return significados;
    }

    public static String esParticipio(String texto) {
        String url="undefined";


        try {
            Document doc = Jsoup.connect("http://www.wordreference.com/definicion/"+texto).get();
            Elements elems= doc.getElementsByAttributeValue("onclick","'redirectWR(event,\"WRdefinicion\")'");

              for(Element el: elems)
              {
                  if(el.html().contains("el participio")) {
                      String partes[] = el.html().split("href=\"");
                     String p2[]= partes[2].split("\"");
                      System.out.println(p2[0]);
                      url=p2[0];
                  }

              }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }

    public  String quitarFlexion(String palabra)
    {

        if(palabra.endsWith("a"))
        {
            palabra=palabra.substring(0, palabra.length()-1)+"o";
        } else if(palabra.endsWith("as") || palabra.endsWith("os"))
        {
            palabra=palabra.substring(0, palabra.length()-2)+"o";
        }

        return palabra;
    }


    public boolean perteneceVSer(String pal)
    {
        boolean pertenece=false;
        if(presenteVSer(pal) || preteritoVSer(pal) || imperfectoVSer(pal) || futuroVSer(pal) || condicionalVSer(pal) || pal.equals("sido"))
        {
            pertenece=true;
        }

        return pertenece;
    }

    public boolean presenteVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("soy") || pal.equals("eres") || pal.equals("es") || pal.equals("somos") || pal.equals("sois") || pal.equals("son"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public boolean imperfectoVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("era") || pal.equals("eras") || pal.equals("éramos") || pal.equals("erais") || pal.equals("eran"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public boolean preteritoVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("fui") || pal.equals("fuiste") || pal.equals("fue") || pal.equals("fuimos") || pal.equals("fuisteis") || pal.equals("fueron"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public boolean futuroVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("seré") || pal.equals("serás") || pal.equals("será") || pal.equals("seremos") || pal.equals("seréis") || pal.equals("serán"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public boolean condicionalVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("sería") || pal.equals("serías") || pal.equals("seríamos") || pal.equals("seríais") || pal.equals("serían"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public boolean pretperfVSer(String pal)
    {
        boolean pertenece=false;
        if(pal.equals("ha sido") || pal.equals("has sido") || pal.equals("hemos sido") || pal.equals("habéis sido") || pal.equals("han sido"))
        {
            pertenece=true;
        }

        return pertenece;

    }

    public String cambiaActiva(String frase, String url, String vSer)
    {
        boolean contieneSujeto=false;
        String sujeto="";
        String verbo="";
        String sep[];

        if(frase.contains("por ")) {
            contieneSujeto=true;
            sep = frase.split("por ");
            sujeto=sep[1];
        }

        System.out.println("Frase que se cambia a activa: "+frase);

        System.out.println("verbo ser: "+ vSer);

        String partes[];
        String p2[];

        switch (sujeto) {
            case "mi":
                sujeto="yo";
                break;
            case "ti":
                sujeto="tú";
                break;
            default:
                break;
        }

        try {
            Document doc = Jsoup.connect("http://www.wordreference.com"+url).get();
            String tiempo="";
            int numElem=0;
            if(presenteVSer(vSer)) {
                numElem=0;
            } else if(imperfectoVSer(vSer)) {
                numElem=1;
            } else if(preteritoVSer(vSer)) {
                numElem=2;
            } else if(futuroVSer(vSer)) {
                numElem=3;
            } else if(condicionalVSer(vSer)) {
                numElem=4;
            } else if(pretperfVSer(vSer)) {
                numElem=5;
            }

            Elements elems= doc.getElementsByClass("neoConj");
           System.out.println("Tamaño de elementos: " +elems.size());
            if(!elems.isEmpty()) {
                Element el = elems.get(numElem);
                String inicio="\\<td\\>";
                String fin="\\<\\/td\\>";
                switch (sujeto) {
                    case "yo":
                        partes=el.html().split(inicio);
                        p2=partes[1].split(fin);
                        verbo=p2[0];
                        break;
                    case "tú":
                        partes=el.html().split(inicio);
                        p2=partes[2].split(fin);
                        verbo=p2[0];
                        break;
                    case "nosotros":
                        partes=el.html().split(inicio);
                        p2=partes[4].split(fin);
                        verbo=p2[0];
                        break;
                    case "vosotros":
                        partes=el.html().split(inicio);
                        p2=partes[5].split(fin);
                        verbo=p2[0];
                        break;
                    case "ellos":
                        partes=el.html().split(inicio);
                        p2=partes[6].split(fin);
                        verbo=p2[0];
                        break;
                    case "ellas":
                        partes=el.html().split(inicio);
                        p2=partes[6].split(fin);
                        verbo=p2[0];
                        break;
                    default:
                        if(sujeto.startsWith("los ") || sujeto.startsWith("las "))
                        {
                            partes=el.html().split(inicio);
                            p2=partes[6].split(fin);
                            verbo=p2[0];
                        }
                        else
                        {
                            partes = el.html().split(inicio);
                            p2 = partes[3].split(fin);
                            verbo = p2[0];
                        }
                        break;
                }
               // System.out.println(el.html());
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

       verbo= verbo.replace("<b>", "").replace("</b>", "");
        System.out.println("Sujeto: "+sujeto);
        System.out.println("Verbo obtenido: "+verbo);

        String fraseAux[]=frase.split(" " +vSer+ " ");
            System.out.println("Verbo ser: "+vSer);
        if(sujeto.equals("")) //Pasiva impersonal
        {
            System.out.println(fraseAux[0]);
            String complemento[]=fraseAux[1].split(" ");
            frase="se "+verbo +  fraseAux[0];
            for(int i=1;i<complemento.length;i++)
            {
                frase=frase+" "+complemento[i];
            }
        }
        else { //Pasiva personal
            frase = sujeto + " " + verbo + fraseAux[0];
        }
        return frase;
    }

    public ArrayList<String> separaFreeling(String texto)
    {
        System.out.println("LLama separa freeling con texto: "+texto);
        texto=texto.replace("\n", "").replace("\r", "").replace("||","|");
        String frases[]=texto.split("\\.");

        ArrayList<String> resultados=new ArrayList<String>();

        String aux=frases[0];
        int contador=frases[0].split(" ").length;


        for(int i=1;i<frases.length;i++)
        {
            int pal=frases[i].split(" ").length;
            contador=contador+pal;

            if(contador<170) //No sobrepasa, se añade la frase
            {
                aux=aux+". "+frases[i];
            }
            else //Sobrepasa, se añade el texto al array, se resetea el aux con la nueva frase y el contador con su numero de palabras
            {

                resultados.add(aux+".");
                aux=frases[i];
                contador=pal;
            }

        }

        resultados.add(aux+".");

        return resultados;
    }



}



