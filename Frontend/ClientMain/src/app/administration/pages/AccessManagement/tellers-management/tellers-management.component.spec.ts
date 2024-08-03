import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TellersManagementComponent } from './tellers-management.component';

describe('TellersManagementComponent', () => {
  let component: TellersManagementComponent;
  let fixture: ComponentFixture<TellersManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TellersManagementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TellersManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
