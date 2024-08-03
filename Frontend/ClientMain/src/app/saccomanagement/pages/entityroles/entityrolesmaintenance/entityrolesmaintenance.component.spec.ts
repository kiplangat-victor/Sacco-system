import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityrolesmaintenanceComponent } from './entityrolesmaintenance.component';

describe('EntityrolesmaintenanceComponent', () => {
  let component: EntityrolesmaintenanceComponent;
  let fixture: ComponentFixture<EntityrolesmaintenanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntityrolesmaintenanceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityrolesmaintenanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
