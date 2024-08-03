import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestructureManitenanceComponent } from './restructure-manitenance.component';

describe('RestructureManitenanceComponent', () => {
  let component: RestructureManitenanceComponent;
  let fixture: ComponentFixture<RestructureManitenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RestructureManitenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RestructureManitenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
