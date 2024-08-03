import { Component, ElementRef, Input, OnInit } from "@angular/core";
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from "@angular/forms";

@Component({
  selector: 'app-excel-upload',
  templateUrl: './excel-upload.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: ExcelUploadComponent,
      multi: true,
    },
  ],
  styleUrls: ["./excel-upload.component.scss"],
})
export class ExcelUploadComponent implements ControlValueAccessor {

  @Input() progress;
  onChange: Function;
  myFiles: string[] = [];

  constructor(private host: ElementRef<HTMLInputElement>) {}

  writeValue(value: null) {
    this.host.nativeElement.value = "";
    this.myFiles = [];
  }

  registerOnChange(fn: Function) {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function) {}

  getFileDetails(e) {
    this.myFiles = [];
    for (var i = 0; i < e.target.files.length; i++) {
      console.log(e.target.files[i]);
      this.myFiles.push(e.target.files[i]);
    }
  }
}
