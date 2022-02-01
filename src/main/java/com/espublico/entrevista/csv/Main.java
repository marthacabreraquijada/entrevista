package com.espublico.entrevista.csv;

import com.espublico.entrevista.csv.processor.OrderProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        try {
            OrderProcessor processor = new OrderProcessor(args[0]);
            //OrderProcessor processor = new OrderProcessor("C:\\Users\\marthacabrera.GJ\\Downloads\\entrevistaCSV\\RegistroVentas1.csv");
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
        } finally {
            System.exit(0);
        }
    }

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
