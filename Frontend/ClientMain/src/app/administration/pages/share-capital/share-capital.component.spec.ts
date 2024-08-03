import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShareCapitalComponent } from './share-capital.component';

describe('ShareCapitalComponent', () => {
  let component: ShareCapitalComponent;
  let fixture: ComponentFixture<ShareCapitalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShareCapitalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShareCapitalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
