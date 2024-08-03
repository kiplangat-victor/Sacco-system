import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SharesLookupComponent } from './shares-lookup.component';

describe('SharesLookupComponent', () => {
  let component: SharesLookupComponent;
  let fixture: ComponentFixture<SharesLookupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SharesLookupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SharesLookupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
