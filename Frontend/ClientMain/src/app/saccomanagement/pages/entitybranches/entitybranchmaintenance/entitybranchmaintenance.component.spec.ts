import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitybranchmaintenanceComponent } from './entitybranchmaintenance.component';

describe('EntitybranchmaintenanceComponent', () => {
  let component: EntitybranchmaintenanceComponent;
  let fixture: ComponentFixture<EntitybranchmaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitybranchmaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitybranchmaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
