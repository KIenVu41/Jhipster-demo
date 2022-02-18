import dayjs from 'dayjs/esm';
import { ICategory } from 'app/entities/category/category.model';

export interface IProducts {
  id?: number;
  name?: string;
  price?: number;
  quantity?: number;
  imageURLContentType?: string | null;
  imageURL?: string | null;
  createdDate?: dayjs.Dayjs | null;
  updatedDate?: dayjs.Dayjs | null;
  category?: ICategory | null;
}

export class Products implements IProducts {
  constructor(
    public id?: number,
    public name?: string,
    public price?: number,
    public quantity?: number,
    public imageURLContentType?: string | null,
    public imageURL?: string | null,
    public createdDate?: dayjs.Dayjs | null,
    public updatedDate?: dayjs.Dayjs | null,
    public category?: ICategory | null
  ) {}
}

export function getProductsIdentifier(products: IProducts): number | undefined {
  return products.id;
}
