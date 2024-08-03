import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { map } from 'highcharts';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { UniversalInquiryComponent } from '../../pages/universal-inquiry/universal-inquiry.component';

@Injectable({
  providedIn: 'root'
})
export class UniversalInquiryService {

  constructor(private dialog: MatDialog) { }
   
  public open(options) {
    if(options.key === null || options.key == ''){
      return;
    }
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.autoFocus = true;
    dialogConfig.width = '1200px';
    dialogConfig.data = {
      key: options.key,
      entity: options.entity,
    };
    const dialogRef = this.dialog.open(UniversalInquiryComponent, dialogConfig);
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }
}
