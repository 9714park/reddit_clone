import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SignupRequestPayload } from './sign-up/sign-up-request.payload';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private httpClient: HttpClient) {}

  signup(payload: SignupRequestPayload): Observable<any> {
    let url = environment.baseUrl + '/auth/signup';
    return this.httpClient.post(url, payload, { responseType: 'text' });
  }
}
