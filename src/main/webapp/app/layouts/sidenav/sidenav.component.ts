import { Component, OnInit } from '@angular/core';
import { Account, JhiLanguageHelper, LoginService, Principal } from 'app/core';
import { JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { Router } from '@angular/router';
import { ProfileService } from 'app/layouts/profiles/profile.service';

@Component({
    selector: 'jhi-sidenav',
    templateUrl: './sidenav.component.html',
    styleUrls: ['sidebar.css']
})
export class SidenavComponent implements OnInit {
    account: Account;
    inProduction: boolean;
    isNavbarCollapsed: boolean;
    languages: any[];
    swaggerEnabled: boolean;
    version: string;
    constructor(
        private principal: Principal,
        private loginService: LoginService,
        private eventManager: JhiEventManager,
        private router: Router,
        private profileService: ProfileService,
        private languageService: JhiLanguageService,
        private languageHelper: JhiLanguageHelper
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
        });
        this.registerAuthenticationSuccess();

        this.languageHelper.getAll().then(languages => {
            this.languages = languages;
        });

        this.profileService.getProfileInfo().then(profileInfo => {
            this.inProduction = profileInfo.inProduction;
            this.swaggerEnabled = profileInfo.swaggerEnabled;
        });
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

    logout() {
        this.collapseNavbar();
        this.loginService.logout();
        this.router.navigate(['']);
    }

    getImageUrl() {
        return this.isAuthenticated() ? this.principal.getImageUrl() : null;
    }

    toggleNavbar() {
        this.isNavbarCollapsed = !this.isNavbarCollapsed;
    }

    collapseNavbar() {
        this.isNavbarCollapsed = true;
    }

    changeLanguage(languageKey: string) {
        this.languageService.changeLanguage(languageKey);
    }
}
