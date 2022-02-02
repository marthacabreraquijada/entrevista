package com.espublico.entrevista.hibernate.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "starships", schema = "entrevista", catalog = "")
public class StarshipsEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = true, length = 255)
    private String name;
    @Basic
    @Column(name = "model", nullable = true, length = 255)
    private String model;
    @Basic
    @Column(name = "manufacturer", nullable = true, length = 255)
    private String manufacturer;
    @Basic
    @Column(name = "cost_in_credits", nullable = true)
    private String costInCredits;
    @Basic
    @Column(name = "length", nullable = true, precision = 0)
    private Double length;
    @Basic
    @Column(name = "max_atmosphering_speed", nullable = true, precision = 0)
    private String maxAtmospheringSpeed;
    @Basic
    @Column(name = "crew", nullable = true, length = 255)
    private String crew;
    @Basic
    @Column(name = "passengers", nullable = true, length = 255)
    private String passengers;
    @Basic
    @Column(name = "cargo_capacity", nullable = true)
    private String cargoCapacity;
    @Basic
    @Column(name = "consumables", nullable = true, length = 255)
    private String consumables;
    @Basic
    @Column(name = "hyperdrive_rating", nullable = true, length = 255)
    private String hyperdriveRating;
    @Basic
    @Column(name = "mglt", nullable = true)
    private String mglt;
    @Basic
    @Column(name = "starship_class", nullable = true, length = 255)
    private String starshipClass;
    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "films_starships",
            joinColumns = { @JoinColumn(name = "starship_id") },
            inverseJoinColumns = { @JoinColumn(name = "film_id") }
    )
    private Set<FilmsEntity> films = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCostInCredits() {
        return costInCredits;
    }

    public void setCostInCredits(String costInCredits) {
        this.costInCredits = costInCredits;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getMaxAtmospheringSpeed() {
        return maxAtmospheringSpeed;
    }

    public void setMaxAtmospheringSpeed(String maxAtmospheringSpeed) {
        this.maxAtmospheringSpeed = maxAtmospheringSpeed;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public String getPassengers() {
        return passengers;
    }

    public void setPassengers(String passengers) {
        this.passengers = passengers;
    }

    public String getCargoCapacity() {
        return cargoCapacity;
    }

    public void setCargoCapacity(String cargoCapacity) {
        this.cargoCapacity = cargoCapacity;
    }

    public String getConsumables() {
        return consumables;
    }

    public void setConsumables(String consumables) {
        this.consumables = consumables;
    }

    public String getHyperdriveRating() {
        return hyperdriveRating;
    }

    public void setHyperdriveRating(String hyperdriveRating) {
        this.hyperdriveRating = hyperdriveRating;
    }

    public String getMglt() {
        return mglt;
    }

    public void setMglt(String mglt) {
        this.mglt = mglt;
    }

    public String getStarshipClass() {
        return starshipClass;
    }

    public void setStarshipClass(String starshipClass) {
        this.starshipClass = starshipClass;
    }

    public Set<FilmsEntity> getFilms() { return films; }

    public void setFilms(Set<FilmsEntity> films) { this.films = films; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarshipsEntity that = (StarshipsEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(model, that.model) && Objects.equals(manufacturer, that.manufacturer) && Objects.equals(costInCredits, that.costInCredits) && Objects.equals(length, that.length) && Objects.equals(maxAtmospheringSpeed, that.maxAtmospheringSpeed) && Objects.equals(crew, that.crew) && Objects.equals(passengers, that.passengers) && Objects.equals(cargoCapacity, that.cargoCapacity) && Objects.equals(consumables, that.consumables) && Objects.equals(hyperdriveRating, that.hyperdriveRating) && Objects.equals(mglt, that.mglt) && Objects.equals(starshipClass, that.starshipClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, model, manufacturer, costInCredits, length, maxAtmospheringSpeed, crew, passengers, cargoCapacity, consumables, hyperdriveRating, mglt, starshipClass);
    }
}
