import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { JhiEventManager } from 'ng-jhipster';

import { LoginService, Principal, Account } from 'app/core';
import { Router } from '@angular/router';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.css']
})
export class HomeComponent implements OnInit {
    account: Account;

    constructor(
        private router: Router,
        private principal: Principal,
        private loginService: LoginService,
        private eventManager: JhiEventManager
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            if (account.authorities.indexOf('ROLE_ADMIN') >= 0) {
                this.router.navigate(['']);
            } else {
                this.account = account;
            }
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.loginService.login();
    }
}
