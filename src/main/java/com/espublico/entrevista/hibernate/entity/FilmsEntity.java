package com.espublico.entrevista.hibernate.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "films", schema = "entrevista", catalog = "")
public class FilmsEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "title", nullable = true, length = 255)
    private String title;
    @Basic
    @Column(name = "episode_id", nullable = true)
    private Integer episodeId;
    @Basic
    @Column(name = "opening_crawl", nullable = true, length = -1)
    private String openingCrawl;
    @Basic
    @Column(name = "director", nullable = true, length = 255)
    private String director;
    @Basic
    @Column(name = "producer", nullable = true, length = 255)
    private String producer;
    @Basic
    @Column(name = "release_date", nullable = true)
    private Date releaseDate;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "films_people",
            joinColumns = { @JoinColumn(name = "film_id") },
            inverseJoinColumns = { @JoinColumn(name = "person_id") }
    )
    Set<PeopleEntity> people = new HashSet<>();
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "films_starships",
            joinColumns = { @JoinColumn(name = "film_id") },
            inverseJoinColumns = { @JoinColumn(name = "starship_id") }
    )
    private Set<StarshipsEntity> starships = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public String getOpeningCrawl() {
        return openingCrawl;
    }

    public void setOpeningCrawl(String openingCrawl) {
        this.openingCrawl = openingCrawl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<PeopleEntity> getPeople() { return people; }

    public void setPeople(Set<PeopleEntity> people) { this.people = people; }

    public Set<StarshipsEntity> getStarships() { return starships; }

    public void setStarships(Set<StarshipsEntity> starships) { this.starships = starships; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilmsEntity that = (FilmsEntity) o;
        return id == that.id && Objects.equals(title, that.title) && Objects.equals(episodeId, that.episodeId) && Objects.equals(openingCrawl, that.openingCrawl) && Objects.equals(director, that.director) && Objects.equals(producer, that.producer) && Objects.equals(releaseDate, that.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, episodeId, openingCrawl, director, producer, releaseDate);
    }
}
