import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IJobs, Jobs } from '../jobs.model';
import { JobsService } from '../service/jobs.service';
import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { JobStatus } from 'app/entities/enumerations/job-status.model';

@Component({
  selector: 'jhi-jobs-update',
  templateUrl: './jobs-update.component.html',
})
export class JobsUpdateComponent implements OnInit {
  isSaving = false;
  jobStatusValues = Object.keys(JobStatus);

  categoriesSharedCollection: ICategory[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    slug: [null, [Validators.required]],
    featureImage: [],
    validFrom: [],
    validThrough: [],
    status: [],
    createdBy: [],
    createdDate: [],
    updatedDate: [],
    updateBy: [],
    category: [],
  });

  constructor(
    protected jobsService: JobsService,
    protected categoryService: CategoryService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobs }) => {
      if (jobs.id === undefined) {
        const today = dayjs().startOf('day');
        jobs.validFrom = today;
        jobs.validThrough = today;
        jobs.createdDate = today;
        jobs.updatedDate = today;
      }

      this.updateForm(jobs);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const jobs = this.createFromForm();
    if (jobs.id !== undefined) {
      this.subscribeToSaveResponse(this.jobsService.update(jobs));
    } else {
      this.subscribeToSaveResponse(this.jobsService.create(jobs));
    }
  }

  trackCategoryById(index: number, item: ICategory): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IJobs>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(jobs: IJobs): void {
    this.editForm.patchValue({
      id: jobs.id,
      title: jobs.title,
      slug: jobs.slug,
      featureImage: jobs.featureImage,
      validFrom: jobs.validFrom ? jobs.validFrom.format(DATE_TIME_FORMAT) : null,
      validThrough: jobs.validThrough ? jobs.validThrough.format(DATE_TIME_FORMAT) : null,
      status: jobs.status,
      createdBy: jobs.createdBy,
      createdDate: jobs.createdDate ? jobs.createdDate.format(DATE_TIME_FORMAT) : null,
      updatedDate: jobs.updatedDate ? jobs.updatedDate.format(DATE_TIME_FORMAT) : null,
      updateBy: jobs.updateBy,
      category: jobs.category,
    });

    this.categoriesSharedCollection = this.categoryService.addCategoryToCollectionIfMissing(this.categoriesSharedCollection, jobs.category);
  }

  protected loadRelationshipsOptions(): void {
    this.categoryService
      .query()
      .pipe(map((res: HttpResponse<ICategory[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategory[]) =>
          this.categoryService.addCategoryToCollectionIfMissing(categories, this.editForm.get('category')!.value)
        )
      )
      .subscribe((categories: ICategory[]) => (this.categoriesSharedCollection = categories));
  }

  protected createFromForm(): IJobs {
    return {
      ...new Jobs(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      slug: this.editForm.get(['slug'])!.value,
      featureImage: this.editForm.get(['featureImage'])!.value,
      validFrom: this.editForm.get(['validFrom'])!.value ? dayjs(this.editForm.get(['validFrom'])!.value, DATE_TIME_FORMAT) : undefined,
      validThrough: this.editForm.get(['validThrough'])!.value
        ? dayjs(this.editForm.get(['validThrough'])!.value, DATE_TIME_FORMAT)
        : undefined,
      status: this.editForm.get(['status'])!.value,
      createdBy: this.editForm.get(['createdBy'])!.value,
      createdDate: this.editForm.get(['createdDate'])!.value
        ? dayjs(this.editForm.get(['createdDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updatedDate: this.editForm.get(['updatedDate'])!.value
        ? dayjs(this.editForm.get(['updatedDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      updateBy: this.editForm.get(['updateBy'])!.value,
      category: this.editForm.get(['category'])!.value,
    };
  }
}
