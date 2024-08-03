import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitytellersComponent } from './entitytellers.component';

describe('EntitytellersComponent', () => {
  let component: EntitytellersComponent;
  let fixture: ComponentFixture<EntitytellersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntitytellersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitytellersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
