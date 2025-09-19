import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [
    ...(appConfig.providers || []),
    provideHttpClient(
      withInterceptors([
        (req, next) => {
          const token = sessionStorage.getItem('JWT');
          if (token && !req.headers.has('Authorization')) {
            req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
          }
          return next(req);
        }
      ])
    )
  ]
}).catch((err) => console.error(err));
