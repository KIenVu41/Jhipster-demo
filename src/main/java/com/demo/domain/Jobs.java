package com.demo.domain;

import com.demo.domain.enumeration.JobStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Jobs.
 */
@Entity
@Table(name = "jobs")
public class Jobs implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "feature_image")
    private String featureImage;

    @Column(name = "valid_from")
    private ZonedDateTime validFrom;

    @Column(name = "valid_through")
    private ZonedDateTime validThrough;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobStatus status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Column(name = "update_by")
    private Long updateBy;

    @ManyToOne
    @JsonIgnoreProperties(value = { "products", "jobs" }, allowSetters = true)
    private Category category;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Jobs id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Jobs title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return this.slug;
    }

    public Jobs slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getFeatureImage() {
        return this.featureImage;
    }

    public Jobs featureImage(String featureImage) {
        this.setFeatureImage(featureImage);
        return this;
    }

    public void setFeatureImage(String featureImage) {
        this.featureImage = featureImage;
    }

    public ZonedDateTime getValidFrom() {
        return this.validFrom;
    }

    public Jobs validFrom(ZonedDateTime validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(ZonedDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public ZonedDateTime getValidThrough() {
        return this.validThrough;
    }

    public Jobs validThrough(ZonedDateTime validThrough) {
        this.setValidThrough(validThrough);
        return this;
    }

    public void setValidThrough(ZonedDateTime validThrough) {
        this.validThrough = validThrough;
    }

    public JobStatus getStatus() {
        return this.status;
    }

    public Jobs status(JobStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return this.createdBy;
    }

    public Jobs createdBy(Long createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    public Jobs createdDate(ZonedDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    public Jobs updatedDate(ZonedDateTime updatedDate) {
        this.setUpdatedDate(updatedDate);
        return this;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getUpdateBy() {
        return this.updateBy;
    }

    public Jobs updateBy(Long updateBy) {
        this.setUpdateBy(updateBy);
        return this;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Jobs category(Category category) {
        this.setCategory(category);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Jobs)) {
            return false;
        }
        return id != null && id.equals(((Jobs) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Jobs{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", slug='" + getSlug() + "'" +
            ", featureImage='" + getFeatureImage() + "'" +
            ", validFrom='" + getValidFrom() + "'" +
            ", validThrough='" + getValidThrough() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdBy=" + getCreatedBy() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", updateBy=" + getUpdateBy() +
            "}";
    }
}
