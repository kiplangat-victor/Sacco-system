import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateCollateralDialogComponent } from './update-collateral-dialog.component';

describe('UpdateCollateralDialogComponent', () => {
  let component: UpdateCollateralDialogComponent;
  let fixture: ComponentFixture<UpdateCollateralDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateCollateralDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateCollateralDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
