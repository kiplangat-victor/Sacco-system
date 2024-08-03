import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuOptionBarComponent } from './menu-option-bar.component';

describe('MenuOptionBarComponent', () => {
  let component: MenuOptionBarComponent;
  let fixture: ComponentFixture<MenuOptionBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuOptionBarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuOptionBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
