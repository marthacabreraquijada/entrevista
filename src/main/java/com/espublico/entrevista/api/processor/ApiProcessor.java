package com.espublico.entrevista.api.processor;

import com.espublico.entrevista.api.constants.ApiConstants;
import com.espublico.entrevista.hibernate.entity.FilmsEntity;
import com.espublico.entrevista.hibernate.entity.PeopleEntity;
import com.espublico.entrevista.hibernate.entity.StarshipsEntity;
import com.espublico.entrevista.hibernate.util.HibernateUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private Session session = null;

    public ApiProcessor() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public void process() {

        this.cleanDB();

        try {
            this.processEntities(ENDPOINT_STARSHIPS);
            this.processEntities(ENDPOINT_FILMS);
            this.processEntities(ENDPOINT_PEOPLE);
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
        HttpRequest request;
        HttpResponse<String> response;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> responseMap;
        List<LinkedHashMap<String,Object>> entities = new ArrayList<>();
        String next = API_URL + endpoint;
        do {
            request = HttpRequest.newBuilder().GET().uri(URI.create(next)).build();
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            responseMap = mapper.readValue(response.body(), Map.class);
            entities.addAll((Collection<? extends LinkedHashMap<String, Object>>) responseMap.get("results"));
            next = responseMap.get("next") != null ? responseMap.get("next").toString() : null;
        } while (next != null);
        return this.saveEntities(entities, endpoint);
    }


    private boolean saveEntities(List<LinkedHashMap<String,Object>> entities, String endpoint) {
        Transaction transaction = null;
        try {
            this.session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            for (LinkedHashMap<String,Object> rawEntity : entities) {
                rawEntity.put("id", this.parseEntityId(rawEntity.get("url").toString(), endpoint));
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

                this.session.save(entity);
            }

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (this.session != null) {
                this.session.close();
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

    private FilmsEntity generateFilm(LinkedHashMap<String, Object> rawFilm) throws ParseException {
        FilmsEntity film = new FilmsEntity();
        film.setId(Integer.parseInt(rawFilm.get("id").toString()));
        film.setTitle(rawFilm.get("title").toString());
        film.setEpisodeId(Integer.valueOf(rawFilm.get("episode_id").toString()));
        film.setDirector(rawFilm.get("director").toString());
        film.setProducer(rawFilm.get("producer").toString());
        film.setOpeningCrawl(rawFilm.get("opening_crawl").toString());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String releaseDate = rawFilm.get("release_date").toString();
        film.setReleaseDate(new java.sql.Date(format.parse(releaseDate).getTime()));

        Set<StarshipsEntity> starships = new HashSet<>();
        ((ArrayList<String>)rawFilm.get("starships")).forEach((starshipUrl) -> {
            StarshipsEntity starship = this.session.get(StarshipsEntity.class, Integer.parseInt(this.parseEntityId(starshipUrl, ENDPOINT_STARSHIPS)));
            starships.add(starship);
        });
        film.setStarships(starships);

        return film;
    }

    private StarshipsEntity generateStarship(LinkedHashMap<String, Object> rawStarship) {
        StarshipsEntity starship = new StarshipsEntity();
        starship.setId(Integer.parseInt(rawStarship.get("id").toString()));
        starship.setName(rawStarship.get("name").toString());
        starship.setModel(rawStarship.get("model").toString());
        starship.setManufacturer(rawStarship.get("manufacturer").toString());
        starship.setCostInCredits(rawStarship.get("cost_in_credits").toString());
        starship.setLength(Double.parseDouble(rawStarship.get("length").toString().replace(",","")));
        starship.setMaxAtmospheringSpeed(rawStarship.get("max_atmosphering_speed").toString());
        starship.setCrew(rawStarship.get("crew").toString());
        starship.setPassengers(rawStarship.get("passengers").toString());
        starship.setCargoCapacity(rawStarship.get("cargo_capacity").toString());
        starship.setConsumables(rawStarship.get("consumables").toString());
        starship.setHyperdriveRating(rawStarship.get("hyperdrive_rating").toString());
        starship.setMglt(rawStarship.get("MGLT").toString());
        starship.setStarshipClass(rawStarship.get("starship_class").toString());

        return starship;
    }

    private PeopleEntity generatePeople(LinkedHashMap<String, Object> rawPerson) {
        PeopleEntity person = new PeopleEntity();
        person.setId(Integer.parseInt(rawPerson.get("id").toString()));
        person.setName(rawPerson.get("name").toString());
        person.setHeight(rawPerson.get("height").toString());
        person.setMass(rawPerson.get("mass").toString());
        person.setHairColor(rawPerson.get("hair_color").toString());
        person.setSkinColor(rawPerson.get("skin_color").toString());
        person.setEyeColor(rawPerson.get("eye_color").toString());
        person.setBirthYear(rawPerson.get("birth_year").toString());

        Set<StarshipsEntity> starships = new HashSet<>();
        ((ArrayList<String>)rawPerson.get("starships")).forEach((starshipUrl) -> {
            StarshipsEntity starship = this.session.get(StarshipsEntity.class, Integer.parseInt(this.parseEntityId(starshipUrl, ENDPOINT_STARSHIPS)));
            starships.add(starship);
        });
        person.setStarships(starships);

        Set<FilmsEntity> films = new HashSet<>();
        ((ArrayList<String>)rawPerson.get("films")).forEach((filmsUrl) -> {
            FilmsEntity film = this.session.get(FilmsEntity.class, Integer.parseInt(this.parseEntityId(filmsUrl, ENDPOINT_FILMS)));
            films.add(film);
        });
        person.setFilms(films);

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

    public String searchDriver(List<String> selectedList) {
        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();

        String person = session.createQuery("").getResultList();

        if (session != null) {
            session.close();
        }

        return person;
    }
}

