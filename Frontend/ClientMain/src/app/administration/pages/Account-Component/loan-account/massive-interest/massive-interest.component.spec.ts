import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MassiveIntrestComponent } from './massive-interest.component';

describe('MassiveIntrestComponent', () => {
  let component: MassiveIntrestComponent;
  let fixture: ComponentFixture<MassiveIntrestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MassiveIntrestComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MassiveIntrestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
