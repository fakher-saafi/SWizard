package com.lyance.srvwiz.repository;

import com.lyance.srvwiz.domain.App;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the App entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppRepository extends JpaRepository<App, Long> {

    @Query("select app from App app where app.user.login = ?#{principal.username}")
    List<App> findByUserIsCurrentUser();

    @Query("select app from App app where app.apiKey = :api_key")
    App findByApiKey(@Param("api_key") String api_key);

}
