import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJobs, getJobsIdentifier } from '../jobs.model';

export type EntityResponseType = HttpResponse<IJobs>;
export type EntityArrayResponseType = HttpResponse<IJobs[]>;

@Injectable({ providedIn: 'root' })
export class JobsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jobs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(jobs: IJobs): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(jobs);
    return this.http
      .post<IJobs>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(jobs: IJobs): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(jobs);
    return this.http
      .put<IJobs>(`${this.resourceUrl}/${getJobsIdentifier(jobs) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(jobs: IJobs): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(jobs);
    return this.http
      .patch<IJobs>(`${this.resourceUrl}/${getJobsIdentifier(jobs) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJobs>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJobs[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJobsToCollectionIfMissing(jobsCollection: IJobs[], ...jobsToCheck: (IJobs | null | undefined)[]): IJobs[] {
    const jobs: IJobs[] = jobsToCheck.filter(isPresent);
    if (jobs.length > 0) {
      const jobsCollectionIdentifiers = jobsCollection.map(jobsItem => getJobsIdentifier(jobsItem)!);
      const jobsToAdd = jobs.filter(jobsItem => {
        const jobsIdentifier = getJobsIdentifier(jobsItem);
        if (jobsIdentifier == null || jobsCollectionIdentifiers.includes(jobsIdentifier)) {
          return false;
        }
        jobsCollectionIdentifiers.push(jobsIdentifier);
        return true;
      });
      return [...jobsToAdd, ...jobsCollection];
    }
    return jobsCollection;
  }

  protected convertDateFromClient(jobs: IJobs): IJobs {
    return Object.assign({}, jobs, {
      validFrom: jobs.validFrom?.isValid() ? jobs.validFrom.toJSON() : undefined,
      validThrough: jobs.validThrough?.isValid() ? jobs.validThrough.toJSON() : undefined,
      createdDate: jobs.createdDate?.isValid() ? jobs.createdDate.toJSON() : undefined,
      updatedDate: jobs.updatedDate?.isValid() ? jobs.updatedDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.validFrom = res.body.validFrom ? dayjs(res.body.validFrom) : undefined;
      res.body.validThrough = res.body.validThrough ? dayjs(res.body.validThrough) : undefined;
      res.body.createdDate = res.body.createdDate ? dayjs(res.body.createdDate) : undefined;
      res.body.updatedDate = res.body.updatedDate ? dayjs(res.body.updatedDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((jobs: IJobs) => {
        jobs.validFrom = jobs.validFrom ? dayjs(jobs.validFrom) : undefined;
        jobs.validThrough = jobs.validThrough ? dayjs(jobs.validThrough) : undefined;
        jobs.createdDate = jobs.createdDate ? dayjs(jobs.createdDate) : undefined;
        jobs.updatedDate = jobs.updatedDate ? dayjs(jobs.updatedDate) : undefined;
      });
    }
    return res;
  }
}
