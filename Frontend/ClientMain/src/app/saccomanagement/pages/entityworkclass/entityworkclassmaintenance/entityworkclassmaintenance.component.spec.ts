import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityworkclassmaintenanceComponent } from './entityworkclassmaintenance.component';

describe('EntityworkclassmaintenanceComponent', () => {
  let component: EntityworkclassmaintenanceComponent;
  let fixture: ComponentFixture<EntityworkclassmaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityworkclassmaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityworkclassmaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
