import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJobs, Jobs } from '../jobs.model';
import { JobsService } from '../service/jobs.service';

@Injectable({ providedIn: 'root' })
export class JobsRoutingResolveService implements Resolve<IJobs> {
  constructor(protected service: JobsService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJobs> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((jobs: HttpResponse<Jobs>) => {
          if (jobs.body) {
            return of(jobs.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Jobs());
  }
}
