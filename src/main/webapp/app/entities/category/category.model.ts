import dayjs from 'dayjs/esm';
import { IProducts } from 'app/entities/products/products.model';
import { IJobs } from 'app/entities/jobs/jobs.model';

export interface ICategory {
  id?: number;
  name?: string;
  createdBy?: number | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  updateBy?: number | null;
  products?: IProducts[] | null;
  jobs?: IJobs[] | null;
}

export class Category implements ICategory {
  constructor(
    public id?: number,
    public name?: string,
    public createdBy?: number | null,
    public createdDate?: dayjs.Dayjs | null,
    public updatedDate?: dayjs.Dayjs | null,
    public updateBy?: number | null,
    public products?: IProducts[] | null,
    public jobs?: IJobs[] | null
  ) {}
}

export function getCategoryIdentifier(category: ICategory): number | undefined {
  return category.id;
}
