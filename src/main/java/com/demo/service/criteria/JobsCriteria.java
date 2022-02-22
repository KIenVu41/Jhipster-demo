package com.demo.service.criteria;

import com.demo.domain.enumeration.JobStatus;
import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link com.demo.domain.Jobs} entity. This class is used
 * in {@link com.demo.web.rest.JobsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /jobs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class JobsCriteria implements Serializable, Criteria {

    /**
     * Class for filtering JobStatus
     */
    public static class JobStatusFilter extends Filter<JobStatus> {

        public JobStatusFilter() {}

        public JobStatusFilter(JobStatusFilter filter) {
            super(filter);
        }

        @Override
        public JobStatusFilter copy() {
            return new JobStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter slug;

    private StringFilter featureImage;

    private ZonedDateTimeFilter validFrom;

    private ZonedDateTimeFilter validThrough;

    private JobStatusFilter status;

    private LongFilter createdBy;

    private ZonedDateTimeFilter createdDate;

    private ZonedDateTimeFilter updatedDate;

    private LongFilter updateBy;

    private LongFilter categoryId;

    private Boolean distinct;

    public JobsCriteria() {}

    public JobsCriteria(JobsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.slug = other.slug == null ? null : other.slug.copy();
        this.featureImage = other.featureImage == null ? null : other.featureImage.copy();
        this.validFrom = other.validFrom == null ? null : other.validFrom.copy();
        this.validThrough = other.validThrough == null ? null : other.validThrough.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.updatedDate = other.updatedDate == null ? null : other.updatedDate.copy();
        this.updateBy = other.updateBy == null ? null : other.updateBy.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public JobsCriteria copy() {
        return new JobsCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public StringFilter slug() {
        if (slug == null) {
            slug = new StringFilter();
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public StringFilter getFeatureImage() {
        return featureImage;
    }

    public StringFilter featureImage() {
        if (featureImage == null) {
            featureImage = new StringFilter();
        }
        return featureImage;
    }

    public void setFeatureImage(StringFilter featureImage) {
        this.featureImage = featureImage;
    }

    public ZonedDateTimeFilter getValidFrom() {
        return validFrom;
    }

    public ZonedDateTimeFilter validFrom() {
        if (validFrom == null) {
            validFrom = new ZonedDateTimeFilter();
        }
        return validFrom;
    }

    public void setValidFrom(ZonedDateTimeFilter validFrom) {
        this.validFrom = validFrom;
    }

    public ZonedDateTimeFilter getValidThrough() {
        return validThrough;
    }

    public ZonedDateTimeFilter validThrough() {
        if (validThrough == null) {
            validThrough = new ZonedDateTimeFilter();
        }
        return validThrough;
    }

    public void setValidThrough(ZonedDateTimeFilter validThrough) {
        this.validThrough = validThrough;
    }

    public JobStatusFilter getStatus() {
        return status;
    }

    public JobStatusFilter status() {
        if (status == null) {
            status = new JobStatusFilter();
        }
        return status;
    }

    public void setStatus(JobStatusFilter status) {
        this.status = status;
    }

    public LongFilter getCreatedBy() {
        return createdBy;
    }

    public LongFilter createdBy() {
        if (createdBy == null) {
            createdBy = new LongFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(LongFilter createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTimeFilter getCreatedDate() {
        return createdDate;
    }

    public ZonedDateTimeFilter createdDate() {
        if (createdDate == null) {
            createdDate = new ZonedDateTimeFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTimeFilter createdDate) {
        this.createdDate = createdDate;
    }

    public ZonedDateTimeFilter getUpdatedDate() {
        return updatedDate;
    }

    public ZonedDateTimeFilter updatedDate() {
        if (updatedDate == null) {
            updatedDate = new ZonedDateTimeFilter();
        }
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTimeFilter updatedDate) {
        this.updatedDate = updatedDate;
    }

    public LongFilter getUpdateBy() {
        return updateBy;
    }

    public LongFilter updateBy() {
        if (updateBy == null) {
            updateBy = new LongFilter();
        }
        return updateBy;
    }

    public void setUpdateBy(LongFilter updateBy) {
        this.updateBy = updateBy;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JobsCriteria that = (JobsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(featureImage, that.featureImage) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validThrough, that.validThrough) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(updatedDate, that.updatedDate) &&
            Objects.equals(updateBy, that.updateBy) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            slug,
            featureImage,
            validFrom,
            validThrough,
            status,
            createdBy,
            createdDate,
            updatedDate,
            updateBy,
            categoryId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JobsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (slug != null ? "slug=" + slug + ", " : "") +
            (featureImage != null ? "featureImage=" + featureImage + ", " : "") +
            (validFrom != null ? "validFrom=" + validFrom + ", " : "") +
            (validThrough != null ? "validThrough=" + validThrough + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (updatedDate != null ? "updatedDate=" + updatedDate + ", " : "") +
            (updateBy != null ? "updateBy=" + updateBy + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
