# Third-Party Integration

The top-level functionality is implemented for each third party in its own package, [Fitbit](src/main/kotlin/dtu/openhealth/integration/fitbit) and [Garmin](src/main/kotlin/dtu/openhealth/integration/garmin).

The most important classes in these packages:
- [FitbitRouter](src/main/kotlin/dtu/openhealth/integration/fitbit/auth/FitbitRouter.kt)
- [FitbitOAuth2Router](src/main/kotlin/dtu/openhealth/integration/fitbit/FitbitOAuth2Router.kt)
- [FitbitPullVerticle](src/main/kotlin/dtu/openhealth/integration/fitbit/FitbitPullVerticle.kt)
- [GarminRouter](src/main/kotlin/dtu/openhealth/integration/garmin/GarminRouter.kt)
- [GarminOAuth1Router](src/main/kotlin/dtu/openhealth/integration/garmin/auth/GarminOAuth1Router.kt)

Other classes of interest:
- [WebServerVerticle](src/main/kotlin/dtu/openhealth/integration/shared/verticle/web/WebServerVerticle.kt)
- [OmhConsumerVerticle](src/main/kotlin/dtu/openhealth/integration/shared/verticle/omh/OmhConsumerVerticle.kt)
