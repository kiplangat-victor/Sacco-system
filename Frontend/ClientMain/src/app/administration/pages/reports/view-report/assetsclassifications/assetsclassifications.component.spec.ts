import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ASSETSCLASSIFICATIONSComponent } from './assetsclassifications.component';

describe('ASSETSCLASSIFICATIONSComponent', () => {
  let component: ASSETSCLASSIFICATIONSComponent;
  let fixture: ComponentFixture<ASSETSCLASSIFICATIONSComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ASSETSCLASSIFICATIONSComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ASSETSCLASSIFICATIONSComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
