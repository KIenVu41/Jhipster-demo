package com.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Category.
 */
@Entity
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "update_by")
    private Long updateBy;

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Set<Products> products = new HashSet<>();

    @OneToMany(mappedBy = "category")
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private Set<Jobs> jobs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Category id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Category name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Category createdBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public Category createdDate(ZonedDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Category updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getUpdateBy() {
        return this.updateBy;
    }

    public Category updateBy(Long updateBy) {
        this.setUpdateBy(updateBy);
        return this;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Set<Products> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Products> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setCategory(null));
        }
        if (products != null) {
            products.forEach(i -> i.setCategory(this));
        }
        this.products = products;
    }

    public Category products(Set<Products> products) {
        this.setProducts(products);
        return this;
    }

    public Category addProducts(Products products) {
        this.products.add(products);
        products.setCategory(this);
        return this;
    }

    public Category removeProducts(Products products) {
        this.products.remove(products);
        products.setCategory(null);
        return this;
    }

    public Set<Jobs> getJobs() {
        return this.jobs;
    }

    public void setJobs(Set<Jobs> jobs) {
        if (this.jobs != null) {
            this.jobs.forEach(i -> i.setCategory(null));
        }
        if (jobs != null) {
            jobs.forEach(i -> i.setCategory(this));
        }
        this.jobs = jobs;
    }

    public Category jobs(Set<Jobs> jobs) {
        this.setJobs(jobs);
        return this;
    }

    public Category addJobs(Jobs jobs) {
        this.jobs.add(jobs);
        jobs.setCategory(this);
        return this;
    }

    public Category removeJobs(Jobs jobs) {
        this.jobs.remove(jobs);
        jobs.setCategory(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category)) {
            return false;
        }
        return id != null && id.equals(((Category) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Category{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", updateBy=" + getUpdateBy() +
            "}";
    }
}
