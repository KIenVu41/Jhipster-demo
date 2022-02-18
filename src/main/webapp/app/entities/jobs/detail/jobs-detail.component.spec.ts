import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { JobsDetailComponent } from './jobs-detail.component';

describe('Jobs Management Detail Component', () => {
  let comp: JobsDetailComponent;
  let fixture: ComponentFixture<JobsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [JobsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ jobs: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(JobsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(JobsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load jobs on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.jobs).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
