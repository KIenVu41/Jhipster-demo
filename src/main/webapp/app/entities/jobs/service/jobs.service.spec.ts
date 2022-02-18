import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { JobStatus } from 'app/entities/enumerations/job-status.model';
import { IJobs, Jobs } from '../jobs.model';

import { JobsService } from './jobs.service';

describe('Jobs Service', () => {
  let service: JobsService;
  let httpMock: HttpTestingController;
  let elemDefault: IJobs;
  let expectedResult: IJobs | IJobs[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(JobsService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      title: 'AAAAAAA',
      slug: 'AAAAAAA',
      featureImage: 'AAAAAAA',
      validFrom: currentDate,
      validThrough: currentDate,
      status: JobStatus.DRAFT,
      createdBy: 0,
      createdDate: currentDate,
      updatedDate: currentDate,
      updateBy: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          validFrom: currentDate.format(DATE_TIME_FORMAT),
          validThrough: currentDate.format(DATE_TIME_FORMAT),
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Jobs', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          validFrom: currentDate.format(DATE_TIME_FORMAT),
          validThrough: currentDate.format(DATE_TIME_FORMAT),
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          validFrom: currentDate,
          validThrough: currentDate,
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.create(new Jobs()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Jobs', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          slug: 'BBBBBB',
          featureImage: 'BBBBBB',
          validFrom: currentDate.format(DATE_TIME_FORMAT),
          validThrough: currentDate.format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
          createdBy: 1,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
          updateBy: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          validFrom: currentDate,
          validThrough: currentDate,
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Jobs', () => {
      const patchObject = Object.assign(
        {
          title: 'BBBBBB',
          slug: 'BBBBBB',
          status: 'BBBBBB',
          createdBy: 1,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updateBy: 1,
        },
        new Jobs()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          validFrom: currentDate,
          validThrough: currentDate,
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Jobs', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          title: 'BBBBBB',
          slug: 'BBBBBB',
          featureImage: 'BBBBBB',
          validFrom: currentDate.format(DATE_TIME_FORMAT),
          validThrough: currentDate.format(DATE_TIME_FORMAT),
          status: 'BBBBBB',
          createdBy: 1,
          createdDate: currentDate.format(DATE_TIME_FORMAT),
          updatedDate: currentDate.format(DATE_TIME_FORMAT),
          updateBy: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          validFrom: currentDate,
          validThrough: currentDate,
          createdDate: currentDate,
          updatedDate: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Jobs', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addJobsToCollectionIfMissing', () => {
      it('should add a Jobs to an empty array', () => {
        const jobs: IJobs = { id: 123 };
        expectedResult = service.addJobsToCollectionIfMissing([], jobs);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobs);
      });

      it('should not add a Jobs to an array that contains it', () => {
        const jobs: IJobs = { id: 123 };
        const jobsCollection: IJobs[] = [
          {
            ...jobs,
          },
          { id: 456 },
        ];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, jobs);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Jobs to an array that doesn't contain it", () => {
        const jobs: IJobs = { id: 123 };
        const jobsCollection: IJobs[] = [{ id: 456 }];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, jobs);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobs);
      });

      it('should add only unique Jobs to an array', () => {
        const jobsArray: IJobs[] = [{ id: 123 }, { id: 456 }, { id: 1797 }];
        const jobsCollection: IJobs[] = [{ id: 123 }];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, ...jobsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const jobs: IJobs = { id: 123 };
        const jobs2: IJobs = { id: 456 };
        expectedResult = service.addJobsToCollectionIfMissing([], jobs, jobs2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(jobs);
        expect(expectedResult).toContain(jobs2);
      });

      it('should accept null and undefined values', () => {
        const jobs: IJobs = { id: 123 };
        expectedResult = service.addJobsToCollectionIfMissing([], null, jobs, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(jobs);
      });

      it('should return initial array if no Jobs is added', () => {
        const jobsCollection: IJobs[] = [{ id: 123 }];
        expectedResult = service.addJobsToCollectionIfMissing(jobsCollection, undefined, null);
        expect(expectedResult).toEqual(jobsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
