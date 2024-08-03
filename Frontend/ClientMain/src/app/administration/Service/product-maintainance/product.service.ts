import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private http: HttpClient) { }

  findMainClassification(): Observable<any>{
    const findMainClassificationUrl = `${environment.systemConfigAPI}/api/v1/main/classification/all`;

    return this.http.get<any>(findMainClassificationUrl);
  }

  findMainClassificationByCode(code): Observable<any>{
    const findMainClassificationUrl = `${environment.systemConfigAPI}/api/v1/main/classification/find/by/code/${code}`;

    return this.http.get<any>(findMainClassificationUrl);
  }


  addCAAProduct(product): Observable<any>{
    const addCAAProductUrl = `${environment.productAPI}/api/v1/product/caa/add`;

    return this.http.post<any>(addCAAProductUrl, product);
  }

  addLAAProduct(product): Observable<any>{
    const addLAAProductUrl = `${environment.productAPI}/api/v1/product/laa/add`;

    return this.http.post<any>(addLAAProductUrl, product);
  }

  addSBAProduct(product): Observable<any>{
    const addSBAProductUrl = `${environment.productAPI}/api/v1/product/sba/add`;

    return this.http.post<any>(addSBAProductUrl, product);
  }

  addTDAProduct(product): Observable<any>{
    const addTDAProductUrl = `${environment.productAPI}/api/v1/product/tda/add`;

    return this.http.post<any>(addTDAProductUrl, product);
  }

  addODAProduct(product): Observable<any>{
    const addODAProductUrl = `${environment.productAPI}/api/v1/product/oda/add`;

    return this.http.post<any>(addODAProductUrl, product);
  }

  getAllCAAProducts(): Observable<any[]>{
    const getAllCAAProductsUrl = `${environment.productAPI}/api/v1/product/caa/all`;

    return this.http.get<any[]>(getAllCAAProductsUrl)
  }

  getAllProducts(scheme): Observable<any[]>{
    const getAllLAAProductsUrl = `${environment.productAPI}/api/v1/product/${scheme}/all`;

    return this.http.get<any[]>(getAllLAAProductsUrl)
  }

  getAllLAAProducts(): Observable<any[]>{
    const getAllLAAProductsUrl = `${environment.productAPI}/api/v1/product/laa/all`;

    return this.http.get<any[]>(getAllLAAProductsUrl)
  }

  getAllSBAProducts(): Observable<any[]>{
    const getAllSBAProductsUrl = `${environment.productAPI}/api/v1/product/sba/all`;

    return this.http.get<any[]>(getAllSBAProductsUrl)
  }

  getAllTDAProducts(): Observable<any>{
    const getAllTDAProductsUrl = `${environment.productAPI}/api/v1/product/tda/all`;
    return this.http.get<any>(getAllTDAProductsUrl);
  }
  getTDAProduct(params: any): Observable<any> {
    const getAllTDAProductsUrl = `${environment.productAPI}/api/v1/product/tda`;
    return this.http.get<any>(getAllTDAProductsUrl, {params: params});
  }

  getAllODAProducts(): Observable<any[]>{
    const getAllODAProductsUrl = `${environment.productAPI}/api/v1/product/oda/all`;

    return this.http.get<any[]>(getAllODAProductsUrl)
  }
  updatePCAAroduct(product): Observable<any>{
    const updateProductUrl = `${environment.productAPI}/api/v1/product/caa/modify`;

    return this.http.put<any>(updateProductUrl, product)
  }

  updatePLAAroduct(product): Observable<any>{
    const updatePLAAroductUrl = `${environment.productAPI}/api/v1/product/laa/modify`;

    return this.http.put<any>(updatePLAAroductUrl, product)
  }

  updateSBAProduct(product): Observable<any>{
    const updateSBAProductUrl = `${environment.productAPI}/api/v1/product/sba/modify`;

    return this.http.put<any>(updateSBAProductUrl, product)
  }

  updateTDAProduct(product: any): Observable<any>{
    const updateTDAProductUrl = `${environment.productAPI}/api/v1/product/tda/modify`;

    return this.http.put<any>(updateTDAProductUrl, product)
  }

  updateODAProduct(product: any): Observable<any>{
    const updateODAProductUrl = `${environment.productAPI}/api/v1/product/oda/modify`;

    return this.http.put<any>(updateODAProductUrl, product)
  }

  deleteProduct(params: any): Observable<any>{
    const deleteProductUrl = `${environment.productAPI}/api/v1/product/delete`;

    return this.http.delete<any>(deleteProductUrl, { params })
  }

  getLAAProductBySchemeCode(params: any): Observable<any>{
    const getProductsSchemeCodeUrl = `${environment.productAPI}/api/v1/product/laa`;

    return this.http.get<any>(getProductsSchemeCodeUrl, { params })
  }
  getSBAProductBySchemeCode(params: any): Observable<any>{
    const getProductsSchemeCodeUrl = `${environment.productAPI}/api/v1/product/sba`;

    return this.http.get<any>(getProductsSchemeCodeUrl, { params })
  }

  verifyProduct(params: any){
    const verifyProductUrl = `${environment.productAPI}/api/v1/product/verify`;

    return this.http.put<any>(verifyProductUrl, {}, { params })
  }
}
