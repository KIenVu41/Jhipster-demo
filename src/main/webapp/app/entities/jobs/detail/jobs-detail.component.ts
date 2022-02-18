import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IJobs } from '../jobs.model';

@Component({
  selector: 'jhi-jobs-detail',
  templateUrl: './jobs-detail.component.html',
})
export class JobsDetailComponent implements OnInit {
  jobs: IJobs | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jobs }) => {
      this.jobs = jobs;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
