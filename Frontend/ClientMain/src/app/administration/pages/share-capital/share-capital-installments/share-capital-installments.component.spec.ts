import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalInstallmentsComponent } from './share-capital-installments.component';

describe('ShareCapitalInstallmentsComponent', () => {
  let component: ShareCapitalInstallmentsComponent;
  let fixture: ComponentFixture<ShareCapitalInstallmentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalInstallmentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalInstallmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
