import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpHeaders } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

  export class HttpHeaderInterceptor implements HttpInterceptor {
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      console.log(request);
     let currentUser = JSON.parse(localStorage.getItem('auth-user'));
    if (currentUser) {
      const accessToken = currentUser.token
        const headers = new HttpHeaders({
            Authorization: `${'Bearer '+accessToken}`,
            userName: `${currentUser.username}`,
            entityId: `${currentUser.entityId}`,
            accessToken: accessToken,

            // 'WEB-API-key': environment.webApiKey,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin':'*'
          });
          const cloneReq = request.clone({headers});
          return next.handle(cloneReq);
    }
    return next.handle(request);
    }
  }

 