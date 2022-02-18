import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { JobsComponent } from '../list/jobs.component';
import { JobsDetailComponent } from '../detail/jobs-detail.component';
import { JobsUpdateComponent } from '../update/jobs-update.component';
import { JobsRoutingResolveService } from './jobs-routing-resolve.service';

const jobsRoute: Routes = [
  {
    path: '',
    component: JobsComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JobsDetailComponent,
    resolve: {
      jobs: JobsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JobsUpdateComponent,
    resolve: {
      jobs: JobsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JobsUpdateComponent,
    resolve: {
      jobs: JobsRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(jobsRoute)],
  exports: [RouterModule],
})
export class JobsRoutingModule {}
