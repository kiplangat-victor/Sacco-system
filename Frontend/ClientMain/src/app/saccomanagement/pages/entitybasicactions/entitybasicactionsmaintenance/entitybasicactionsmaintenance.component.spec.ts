import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitybasicactionsmaintenanceComponent } from './entitybasicactionsmaintenance.component';

describe('EntitybasicactionsmaintenanceComponent', () => {
  let component: EntitybasicactionsmaintenanceComponent;
  let fixture: ComponentFixture<EntitybasicactionsmaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitybasicactionsmaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitybasicactionsmaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
