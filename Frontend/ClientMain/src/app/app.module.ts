import { AgmCoreModule } from '@agm/core';
import { DatePipe } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { GoogleMapsModule } from '@angular/google-maps';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { DataTablesModule } from 'angular-datatables';
import { CanActivateModuleGuard } from 'src/@core/helpers/CanActivateModule.guard';
import { CanLoadModuleGuard } from 'src/@core/helpers/CanLoadModule.guard';
import { environment } from '../environments/environment';
import { AdministrationModule } from './administration/administration.module';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpHeaderInterceptor } from './http.interceptor';
import { MaterialModule } from './material.module';
import { AutoLogoutService } from './administration/Service/AutoLogout/auto-logout.service';
import { AuthModule } from './Auth/auth.module';
import { SaccomanagementModule } from './saccomanagement/saccomanagement.module';



@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    GoogleMapsModule,
    MaterialModule,
    DataTablesModule,
    AdministrationModule,
    AuthModule,
  
     ServiceWorkerModule.register('ngsw-worker.js', {
       enabled: environment.production,
       // Register the ServiceWorker as soon as the app is stable
       // or after 30 seconds (whichever comes first).
       registrationStrategy: 'registerWhenStable:30000'
     }),
     ServiceWorkerModule.register('ngsw-worker.js', {
       enabled: environment.production,
       // Register the ServiceWorker as soon as the application is stable
       // or after 30 seconds (whichever comes first).
       registrationStrategy: 'registerWhenStable:30000'
     }),

  ],
  exports: [
    MaterialModule
  ],

  providers: [
    AutoLogoutService,
    DatePipe,
    CanLoadModuleGuard,
    CanActivateModuleGuard,
    { provide: HTTP_INTERCEPTORS, useClass: HttpHeaderInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]

})
export class AppModule { }

if (environment.production) {
  console.log = function() {};
  console.warn = function() {};
  console.error = function () { };
  console.time = function () { };
  console.timeEnd = function () { };
}
