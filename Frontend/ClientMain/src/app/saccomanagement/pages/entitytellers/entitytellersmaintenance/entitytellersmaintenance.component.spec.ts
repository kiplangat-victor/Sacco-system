import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitytellersmaintenanceComponent } from './entitytellersmaintenance.component';

describe('EntitytellersmaintenanceComponent', () => {
  let component: EntitytellersmaintenanceComponent;
  let fixture: ComponentFixture<EntitytellersmaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitytellersmaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitytellersmaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
