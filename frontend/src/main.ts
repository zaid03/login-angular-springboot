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
          const isPublicEndpoint = req.url.includes('/api/cas/validate') || 
                                  req.url.includes('/api/login/') ||
                                  req.url.includes('/api/filter');

          if (token && !req.headers.has('Authorization') && !req.headers.has('Skip-Auth') && !isPublicEndpoint) {
            req = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
          }
          return next(req);
        }
      ])
    )
  ]
}).catch((err) => console.error(err));
