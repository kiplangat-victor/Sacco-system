import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityusermaintenanceComponent } from './entityusermaintenance.component';

describe('EntityusermaintenanceComponent', () => {
  let component: EntityusermaintenanceComponent;
  let fixture: ComponentFixture<EntityusermaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityusermaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityusermaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
