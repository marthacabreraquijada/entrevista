package com.espublico.entrevista.csv;

import com.espublico.entrevista.csv.processor.OrderProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Clase principal para las consultas asociadas a la lectura de CSV
 * @author: Martha Cabrera
 */
public class Main {

    /**
     * Método principal que hace las llamadas a procesar el CSV y controla posibles errores en la lectura
     * @param args Argumentos de la línea de comando
     */
    public static void main(String[] args) {
        try {
            OrderProcessor processor = new OrderProcessor(args[0]);
            HashMap<String,HashMap<String,Long>> summary = processor.process();
            if (summary.size() > 0) {
                System.out.println("Archivo procesado correctamente");
                printSummary(summary);
            } else {
                System.out.println("El Archivo no pudo ser procesado o estaba vacio");
            }

        } catch (NullPointerException e) {
            System.out.println("Por favor ingrese la ruta del archivo CSV como parámetro.");
        } catch (FileNotFoundException e) {
            System.out.println("El archivo CSV no fue encontrado.");
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error leyendo el archivo CSV. Por favor compruebe si el formato es correcto.");
        } catch (Exception e) {
            System.out.println("Ha ocurrido un error inesperado cargando el archivo CSV: " + e.getMessage());
        } finally {
            System.exit(0);
        }
    }

    /**
     * Método que imprime la cantidad de registros procesados por dimensiones
     * @param summary contenido del archivo procesado
     */
    private static void printSummary(HashMap<String,HashMap<String,Long>> summary) {
        System.out.println("--------------- SUMMARY ---------------");
        summary.forEach((item, itemSummary) -> {
            System.out.println("");
            System.out.println("-- " + item + " --");
            itemSummary.forEach((name, total) -> {
                System.out.println(name + ": " + total );
            });
        });
    }


}
