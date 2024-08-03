import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeConfigComponent } from './employee-config.component';

describe('EmployeeConfigComponent', () => {
  let component: EmployeeConfigComponent;
  let fixture: ComponentFixture<EmployeeConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeeConfigComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeeConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
