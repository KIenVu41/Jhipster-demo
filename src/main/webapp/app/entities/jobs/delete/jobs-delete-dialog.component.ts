import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IJobs } from '../jobs.model';
import { JobsService } from '../service/jobs.service';

@Component({
  templateUrl: './jobs-delete-dialog.component.html',
})
export class JobsDeleteDialogComponent {
  jobs?: IJobs;

  constructor(protected jobsService: JobsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jobsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
