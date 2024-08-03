import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ARREARSGENERALSTATEMENTComponent } from './arrearsgeneralstatement.component';

describe('ARREARSGENERALSTATEMENTComponent', () => {
  let component: ARREARSGENERALSTATEMENTComponent;
  let fixture: ComponentFixture<ARREARSGENERALSTATEMENTComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ARREARSGENERALSTATEMENTComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ARREARSGENERALSTATEMENTComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
