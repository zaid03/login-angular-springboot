import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PuaComponent } from './pua.component';

describe('PuaComponent', () => {
  let component: PuaComponent;
  let fixture: ComponentFixture<PuaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PuaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PuaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
