package com.espublico.entrevista.hibernate.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "people", schema = "entrevista", catalog = "")
public class PeopleEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Basic
    @Column(name = "height", nullable = true, precision = 0)
    private Double height;
    @Basic
    @Column(name = "mass", nullable = true, precision = 0)
    private Double mass;
    @Basic
    @Column(name = "hair_color", nullable = true, length = 255)
    private String hairColor;
    @Basic
    @Column(name = "skin_color", nullable = true, length = 255)
    private String skinColor;
    @Basic
    @Column(name = "eye_color", nullable = true, length = 255)
    private String eyeColor;
    @Basic
    @Column(name = "birth_year", nullable = true, length = 255)
    private String birthYear;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "films_people",
            joinColumns = { @JoinColumn(name = "person_id") },
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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getMass() {
        return mass;
    }

    public void setMass(Double mass) {
        this.mass = mass;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public Set<FilmsEntity> getFilms() { return films; }

    public void setFilms(Set<FilmsEntity> films) { this.films = films; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeopleEntity that = (PeopleEntity) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(height, that.height) && Objects.equals(mass, that.mass) && Objects.equals(hairColor, that.hairColor) && Objects.equals(skinColor, that.skinColor) && Objects.equals(eyeColor, that.eyeColor) && Objects.equals(birthYear, that.birthYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, height, mass, hairColor, skinColor, eyeColor, birthYear);
    }
}
