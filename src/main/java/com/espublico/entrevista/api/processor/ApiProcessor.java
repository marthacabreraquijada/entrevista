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

/**
 * Clase para procesar y consultar los datos del API de Star Wars
 * @author: Martha Cabrera
 */
public class ApiProcessor implements ApiConstants {

    private HttpClient httpClient;
    private Session session = null;

    /**
     * Constructor del procesador con un HttpClient que que consume el API de StarWars y utiliza Hibernate para interactuar con la base de datos
     */
    public ApiProcessor() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Método que procesa las entidades principales del API Star Wars
     */
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

    /**
     * Método que borra el contenido de las tablas del modelo de datos de Star Wars
     * @return True o False si se han borrado con éxito
     */
    private boolean cleanDB (){
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.createSQLQuery("delete from films_people").executeUpdate();
            session.createSQLQuery("delete from people_starships").executeUpdate();
            session.createSQLQuery("delete from films_starships").executeUpdate();
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

    /**
     * Método que procesa una tabla del API de Star Wars
     * @param endpoint representa el final de la cadena de caracteres de la URL del api y que determina la tabla que se esta procesando
     * @throws InterruptedException  si ocurre algún error de interrupción durante la ejecución
     * @throws IOException si ocurre cualquier otro error
     * @return True o False dependiendo del resultado de haber guardado la tabla
     */
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

    /**
     * Método que guarda el contenido de una tabla del API de Star Wars
     * @param entities Lista de las diferentes ocurrencias dentro del results de las tablas del API
     * @param endpoint representa el final de la cadena de caracteres de la URL del api y que determina la tabla que se esta procesando
     * @return True o False dependiendo si la acción de salvar los datos ha finalizado con éxito
     */
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

    /**
     * Método que extrae el ID de la url de una entidad
     * @param url cadena de caracteres que contiene el identificador de la entidad
     * @param endpoint representa el final de la cadena de caracteres de la URL del api y que determina la tabla que se esta procesando
     * @return un String que contiene el ID de la entidad que se esté procesando
     */
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

    /**
     * Método que genera el contenido de una entidad contenida dentro del result de un film
     * @param rawFilm contiene la información de cada una de las propiedades de un film
     * @throws ParseException por la propiedad de fecha
     * @return una entidad de film
     */
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

    /**
     * Método que genera el contenido de una entidad contenida dentro del result de una Starship
     * @param rawStarship contiene la información de cada una de las propiedades de una Starship
     * @return una entidad de Starships
     */
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

    /**
     * Método que genera el contenido de una entidad contenida dentro del result de una persona
     * @param rawPerson contiene la información de cada una de las propiedades de una persona
     * @return una entidad de persona
     */
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

    /**
     * Método que consulta dentro de la base de datos la lista de actores registrados
     * @return una lista de entidades de persona
     */
    public List<PeopleEntity> listPeople(){

        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();

        List<PeopleEntity> people = session.createQuery("from PeopleEntity p").getResultList();

        if (session != null) {
            session.close();
        }

        return people;

    }

    /**
     * Método que consulta dentro de la base de datos la lista de películas registradas
     * @return una lista de entidades de película
     */
    public List<FilmsEntity> listFilms(){

        Session session = null;
        session = HibernateUtil.getSessionFactory().openSession();

        List<FilmsEntity> films = session.createQuery("from FilmsEntity f").getResultList();

        if (session != null) {
            session.close();
        }

        return films;

    }

    /**
     * Método que consulta dentro de la base de datos la nave que más veces aparece en la lista de películas que recibe como parámetro
     * @param selectedList lista de películas seleccionadas en el menú
     * @return una entidad de Starships
     */
    public StarshipsEntity searchMostDrivenStarship(List<String> selectedList) {
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            List<Integer> query = session.createSQLQuery("SELECT fs.starship_id FROM films_starships fs WHERE fs.film_id in (:films) GROUP BY fs.starship_id order by COUNT(fs.film_id), fs.starship_id DESC LIMIT 1")
                    .setParameterList("films", selectedList).list();
            if (query.size() == 0) {
                return null;
            }

            StarshipsEntity starship = session.get(StarshipsEntity.class, query.get(0));
            return starship;
        } catch (Exception e) {
            return null;
        }
    }
}

