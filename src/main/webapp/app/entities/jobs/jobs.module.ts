import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { JobsComponent } from './list/jobs.component';
import { JobsDetailComponent } from './detail/jobs-detail.component';
import { JobsUpdateComponent } from './update/jobs-update.component';
import { JobsDeleteDialogComponent } from './delete/jobs-delete-dialog.component';
import { JobsRoutingModule } from './route/jobs-routing.module';

@NgModule({
  imports: [SharedModule, JobsRoutingModule],
  declarations: [JobsComponent, JobsDetailComponent, JobsUpdateComponent, JobsDeleteDialogComponent],
  entryComponents: [JobsDeleteDialogComponent],
})
export class JobsModule {}
