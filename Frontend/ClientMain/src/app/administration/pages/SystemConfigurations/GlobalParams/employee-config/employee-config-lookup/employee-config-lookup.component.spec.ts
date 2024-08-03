import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmployeeConfigLookupComponent } from './employee-config-lookup.component';

describe('EmployeeConfigLookupComponent', () => {
  let component: EmployeeConfigLookupComponent;
  let fixture: ComponentFixture<EmployeeConfigLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmployeeConfigLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmployeeConfigLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
