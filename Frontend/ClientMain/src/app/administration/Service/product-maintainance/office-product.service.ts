import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class OfficeProductService {

  constructor(private http: HttpClient) { }

  addOfficeProduct(product): Observable<any>{
    const addOfficeProductUrl = `${environment.productAPI}/api/v1/officeproduct/office/laa/add`;

    return this.http.post<any>(addOfficeProductUrl, product);
  }

  getAllOfficeProducts(): Observable<any[]>{
    const getAllOfficeProductsUrl = `${environment.productAPI}/api/v1/officeproduct/office/laa/all`;

    return this.http.get<any[]>(getAllOfficeProductsUrl)
  }

  getOfficeProductByCode(params): Observable<any>{
    const getAllOfficeProductsUrl = `${environment.productAPI}/api/v1/officeproduct/office/laa`;

    return this.http.get<any>(getAllOfficeProductsUrl, { params })
  }


  updateOfficeProduct(product): Observable<any>{
    const updateOfficeProductUrl = `${environment.productAPI}/api/v1/officeproduct/office/laa/modify`;

    return this.http.put<any>(updateOfficeProductUrl, product)
  }

  verifyOfficeProduct(params){
    const verifyOfficeProductUrl = `${environment.productAPI}/api/v1/officeproduct/office/verify`;

    return this.http.put<any>(verifyOfficeProductUrl, {}, { params })
  }

  deleteOfficeProduct(params): Observable<any>{
    const deleteOfficeProductUrl = `${environment.productAPI}/api/v1/officeproduct/office/delete`;

    return this.http.delete<any>(deleteOfficeProductUrl, { params })
  }
}
