package com.espublico.entrevista.api.processor;

import com.espublico.entrevista.api.constants.ApiConstants;
import com.espublico.entrevista.hibernate.entity.FilmsEntity;
import com.espublico.entrevista.hibernate.entity.PeopleEntity;
import com.espublico.entrevista.hibernate.entity.StarshipsEntity;
import com.espublico.entrevista.hibernate.util.HibernateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.CaseUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiProcessor implements ApiConstants {

    private HttpClient httpClient;

    public ApiProcessor() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void process() {

//        this.cleanDB();

        try {
            this.processEntities(ENDPOINT_PEOPLE);
            this.processEntities(ENDPOINT_STARSHIPS);
            this.processEntities(ENDPOINT_FILMS);
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
            Logger.getLogger(ApiProcessor.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    private boolean cleanDB (){
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.createSQLQuery("delete from films_people").executeUpdate();
            session.createSQLQuery("delete from films_starships").executeUpdate();
            session.createSQLQuery("delete from people_starships").executeUpdate();
            session.createSQLQuery("delete from films").executeUpdate();
            session.createSQLQuery("delete from people").executeUpdate();
            session.createSQLQuery("delete from starships").executeUpdate();

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }

    }

    private boolean processEntities(String endpoint) throws IOException, InterruptedException{
        final HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(API_URL + endpoint)).build();
        final HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        final ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
        List<LinkedHashMap<String,String>> entities = (ArrayList<LinkedHashMap<String,String>>)responseMap.get("results");
        return this.saveEntities(entities, endpoint);
    }


    private boolean saveEntities(List<LinkedHashMap<String,String>> entities, String endpoint) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            for (LinkedHashMap<String,String> rawEntity : entities) {
                rawEntity.put("id", this.parseEntityId(rawEntity.get("url"), endpoint));
                Object entity;
                switch (endpoint) {
                    case ENDPOINT_FILMS:
                        entity = this.generateFilm(rawEntity);
                        break;
                    case ENDPOINT_PEOPLE:
                        entity = this.generatePeople(rawEntity);
                        break;
                    case ENDPOINT_STARSHIPS:
                        entity = this.generateStarship(rawEntity);
                        break;
                    default:
                        throw new IllegalArgumentException("Endpoint no reconocido");
                }

                session.save(entity);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private String parseEntityId(String url, String endpoint) {
        Pattern pattern = Pattern.compile(
                API_URL + endpoint + "([0-9]+)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) {
            throw new IllegalArgumentException("ID no encontrado en la respuesta");
        }
        return matcher.group(1);
    }

    private FilmsEntity generateFilm(LinkedHashMap<String, String> rawFilm) throws ParseException {
        FilmsEntity film = new FilmsEntity();
        film.setId(Integer.parseInt(rawFilm.get("id")));
        film.setTitle(String.valueOf(rawFilm.get("title")));
        film.setEpisodeId(Integer.valueOf(String.valueOf(rawFilm.get("episode_id"))));
        film.setDirector(String.valueOf(rawFilm.get("title")));
        film.setProducer(String.valueOf(rawFilm.get("title")));
        film.setOpeningCrawl(String.valueOf(rawFilm.get("title")));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String releaseDate = String.valueOf(rawFilm.get("release_date"));
        film.setReleaseDate(new java.sql.Date(format.parse(releaseDate).getTime()));

        return film;
    }

    private StarshipsEntity generateStarship(LinkedHashMap<String, String> rawStarship) {
        StarshipsEntity starship = new StarshipsEntity();
        starship.setId(Integer.parseInt(rawStarship.get("id")));
        starship.setName(String.valueOf(rawStarship.get("name")));
        starship.setModel(String.valueOf(rawStarship.get("model")));
        starship.setManufacturer(String.valueOf(rawStarship.get("manufacturer")));
        starship.setCostInCredits(String.valueOf(rawStarship.get("cost_in_credits")));
        starship.setLength(Double.parseDouble(rawStarship.get("length").replace(",","")));
        starship.setMaxAtmospheringSpeed(String.valueOf(rawStarship.get("max_atmosphering_speed")));
        starship.setCrew(String.valueOf(rawStarship.get("crew")));
        starship.setPassengers(String.valueOf(rawStarship.get("passengers")));
        starship.setCargoCapacity(Long.parseLong(rawStarship.get("cargo_capacity")));
        starship.setConsumables(String.valueOf(rawStarship.get("consumables")));
        starship.setHyperdriveRating(String.valueOf(rawStarship.get("hyperdrive_rating")));
        starship.setMglt(Integer.parseInt(rawStarship.get("MGLT")));
        starship.setStarshipClass(String.valueOf(rawStarship.get("starship_class")));

        return starship;
    }

    private PeopleEntity generatePeople(LinkedHashMap<String, String> rawPerson) {
        PeopleEntity person = new PeopleEntity();
        person.setId(Integer.parseInt(rawPerson.get("id")));
        person.setName(String.valueOf(rawPerson.get("name")));
        person.setHeight(Double.parseDouble(rawPerson.get("height")));
        person.setMass(Double.parseDouble(rawPerson.get("mass")));
        person.setHairColor(String.valueOf(rawPerson.get("hair_color")));
        person.setSkinColor(String.valueOf(rawPerson.get("skin_color")));
        person.setEyeColor(String.valueOf(rawPerson.get("eye_color")));
        person.setBirthYear(String.valueOf(rawPerson.get("birth_year")));

        return person;
    }

    public List<PeopleEntity> listPeople(){

        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();

        List<PeopleEntity> people = session.createQuery("from PeopleEntity p").getResultList();

        if (session != null) {
            session.close();
        }

        return people;

    }

    public List<FilmsEntity> listFilms(){

        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();

        List<FilmsEntity> films = session.createQuery("from FilmsEntity f").getResultList();

        if (session != null) {
            session.close();
        }

        return films;

    }


}

