package com.espublico.entrevista.api;

import com.espublico.entrevista.api.processor.ApiProcessor;
import com.espublico.entrevista.hibernate.entity.FilmsEntity;
import com.espublico.entrevista.hibernate.entity.PeopleEntity;

import java.io.InputStreamReader;
import java.util.*;

public class Main {

    public static Scanner input = new Scanner(System.in);
    public static boolean exit = false;
    public static int selectionLevel1;
    public static String selectionLevel2;

    public static void main(String[] args) {

        ApiProcessor processor = new ApiProcessor();
        processor.process();

        while (!exit) {
            System.out.println("1. Listar actores");
            System.out.println("2. Listar películas");
            System.out.println("0. Salir");

            try {

                System.out.println("--> Selecciona una de las opciones: ");
                selectionLevel1 = input.nextInt();

                switch (selectionLevel1) {
                    case 1:
                        //System.out.println("Listar actores...");
                        List<PeopleEntity> peopleList;
                        peopleList = processor.listPeople();
                        printPeople(peopleList);
                        break;
                    case 2:
                        //System.out.println("Listar películas...");
                        List<FilmsEntity> filmsList;
                        filmsList = processor.listFilms();
                        printFilms(filmsList);
                        List<String> selectedList = secondSelection();
                        processor.searchDriver(selectedList);
                        break;
                    case 0:
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

    private static List<String> secondSelection() {
        System.out.println("--> Selecciona los códigos de películas separados por coma (,): ");
        System.out.println("    (0 Para salir y X para regresar al menu principal)");

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        selectionLevel2 = scanner.nextLine();

        String[] list = selectionLevel2.split(",");

        List<String> inputList = new ArrayList<String>(Arrays.asList(list));

        if (selectionLevel2.equals("0")) {
            exit = true;
        }

        System.out.println("Input: " + inputList);

        return inputList;

    }

    private static void printFilms(List<FilmsEntity> list) {
        System.out.println("------------------------------");
        list.forEach((film) -> {
            System.out.println("");
            System.out.println(film.getId() + " - " + film.getTitle());
        });
        System.out.println("------------------------------");
    }

    private static void printPeople(List<PeopleEntity> list) {
        System.out.println("------------------------------");
        list.forEach((person) -> {
            System.out.println("");
            int filmsCount = person.getFilms().size();
            System.out.println(person.getId() + " - " + person.getName() + " (" + filmsCount + " películas)");
            if (filmsCount > 0) {
                System.out.println("Películas: " );
                person.getFilms().forEach((film) -> {
                    System.out.println("\t" + film.getId() + " - " + film.getTitle());
                });
            }
        });
        System.out.println("------------------------------");
    }

}