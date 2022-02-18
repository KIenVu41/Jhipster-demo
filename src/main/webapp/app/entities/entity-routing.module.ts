import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'jobs',
        data: { pageTitle: 'demoSpringBootstrapApp.jobs.home.title' },
        loadChildren: () => import('./jobs/jobs.module').then(m => m.JobsModule),
      },
      {
        path: 'category',
        data: { pageTitle: 'demoSpringBootstrapApp.category.home.title' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'products',
        data: { pageTitle: 'demoSpringBootstrapApp.products.home.title' },
        loadChildren: () => import('./products/products.module').then(m => m.ProductsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
