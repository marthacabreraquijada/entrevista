package com.espublico.entrevista.api;

import com.espublico.entrevista.api.processor.ApiProcessor;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static Scanner input = new Scanner(System.in);
    public static boolean exit = false;
    public static int selectionLevel1, selectionLevel2;

    public static void main(String[] args) {

        ApiProcessor processor = new ApiProcessor();
        processor.process();

        while (!exit) {
            System.out.println("1. Listar actores");
            System.out.println("2. Listar películas");
            System.out.println("3. Salir");

            try {

                System.out.println("--> Selecciona una de las opciones: ");
                selectionLevel1 = input.nextInt();

                switch (selectionLevel1) {
                    case 1:
                        System.out.println("Listar actores...");
                        break;
                    case 2:
                        HashMap<Integer,String> filmsList;
                        filmsList = processor.listFilms();
                        printList(filmsList);
                        //System.out.println("Listar películas...");
                        System.out.println("--> Selecciona los códigos de películas separados por coma (,): ");
                        selectionLevel2 = input.nextInt();
                        secondSelection();
                        break;
                    case 3:
                        exit = true;
                        break;
                    default:
                        System.out.println(" ----- Seleccionar sólo opciones disponibles ----- ");

                }
            } catch (InputMismatchException e) {
                System.out.println(" ----- Debes insertar un número ----- ");
                input.next();
            }
        }
    }

    private static void secondSelection() {
        // Preparar listado de salida
    }

    private static void printList(HashMap<Integer,String> list) {
        System.out.println("------------------------------");
        list.forEach((id, text) -> {
            System.out.println("");
            System.out.println(id + " - " + text);
        });
        System.out.println("------------------------------");
    }

}