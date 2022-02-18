import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';
import { JobStatus } from 'app/entities/enumerations/job-status.model';

export interface IJobs {
  id?: number;
  title?: string;
  slug?: string;
  featureImage?: string | null;
  validFrom?: dayjs.Dayjs | null;
  validThrough?: dayjs.Dayjs | null;
  status?: JobStatus | null;
  createdBy?: number | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  updateBy?: number | null;
  category?: ICategory | null;
}

export class Jobs implements IJobs {
  constructor(
    public id?: number,
    public title?: string,
    public slug?: string,
    public featureImage?: string | null,
    public validFrom?: dayjs.Dayjs | null,
    public validThrough?: dayjs.Dayjs | null,
    public status?: JobStatus | null,
    public createdBy?: number | null,
    public createdDate?: dayjs.Dayjs | null,
    public updatedDate?: dayjs.Dayjs | null,
    public updateBy?: number | null,
    public category?: ICategory | null
  ) {}
}

export function getJobsIdentifier(jobs: IJobs): number | undefined {
  return jobs.id;
}
