# Upgrade Notes

---

Below are described most important changes, features, additions and bugfixes that needs attention while performing the upgrade from the previous version.

<!-- TOC -->
* [2.0.0-BETA-M13](#200-snapshot)
  * [Changes](#changes)
    * [Deprecation of `db.pool.size.min` and `db.pool.size.max` sizing options [#4112]](#deprecation-of-dbpoolsizemin-and-dbpoolsizemax-sizing-options-4112httpsgithubcomeclipsekapuapull4112)
    * [Duplicate Name Exception is now returned as a 409 error [#4133]](#duplicate-name-exception-is-now-returned-as-a-409-error-4133httpsgithubcomeclipsekapuapull4112)
    * [Introduction of un-scoped entities for Job Step Definitions [#3996]](#introduction-of-un-scoped-entities-for-job-step-definitions-3996httpsgithubcomeclipsekapuapull3996)
    * [Invalid Trigger Exceptions are now returned as a 400 error [#4147]](#invalid-trigger-exceptions-are-now-returned-as-a-400-error-4147httpsgithubcomeclipsekapuapull4147)
    * [Max Number Of Entity Reached exception is now returned as a 403 error [#4154]](#max-number-of-entity-reached-exception-is-now-returned-as-a-403-error-4154httpsgithubcomeclipsekapuapull4154)
    * [Job Step Property value now supports JSON format for complex types [#4161]](#job-step-property-value-now-supports-json-format-for-complex-types-4161httpsgithubcomeclipsekapuapull4161)
    * [Datastore search APIs will now allow multiple `clientId` to be specified [#4130]](#datastore-search-apis-will-now-allow-multiple-clientid-to-be-specified-4130httpsgithubcomeclipsekapuapull4130)
  * [DB Changes](#db-changes)
    * [New column in MFA Option table [#4115]](#new-column-in-mfa-option-table-4115httpsgithubcomeclipsekapuapull4115)
    * [New column in Trigger Definition Property table [#4134]](#new-column-in-trigger-definition-property-table-4134httpsgithubcomeclipsekapuapull4134)
<!-- TOC -->

---

# 2.0.0-BETA-M13

This report is only partial for Eclipse Kapua release 2.0.0-BETA-M13, since we started to maintain it mid-release development.

## Changes

### Deprecation of `db.pool.size.min` and `db.pool.size.max` sizing options [[#4112](https://github.com/eclipse/kapua/pull/4112)]

Deprecated `commons.db.pool.size.min` and `commons.db.pool.size.max` settings, switching to a new `commons.db.pool.size.fixed` setting which will configure the database connection pool to a fixed size with a default value of `5`.

Backward compatibility is granted by `db.pool.size.strategy`. Default value is `range`, but it should be changed to `fixed` strategy because `range` strategy will be removed in future releases.


### Duplicate Name Exception is now returned as a 409 error [[#4133](https://github.com/eclipse/kapua/pull/4112)]

Fixed the HTTP response code when a `KapuaDuplicateNameException` occurs from `500 Internal error` to the correct `409 Conflict`.

The body of the response will contain additional information on the error.


### Introduction of un-scoped entities for Job Step Definitions [[#3996](https://github.com/eclipse/kapua/pull/3996)]

Introduced the concept of "Un-scoped entities" as opposite of "Scoped entities".

Normally entities belongs to a certain scope. For example: Users, Tags, Devices belongs to an Account.
There are some entities that are available for all Accounts because of their nature and usage. For example: JobStepDefinitions are available to all scopes.

With this feature we changed the way this "availability" was implemented and made it available at the core of the Service API.

User is not required to perform any action to the existing data and the migration of the Job Step Definitions will be performed by the Job Step Definition Aligner that will reflect any change to the Job Step Definitions coded into the Database.


### Invalid Trigger Exceptions are now returned as a 400 error [[#4147](https://github.com/eclipse/kapua/pull/4147)]

Fixed the HTTP response code when a `TriggerInvalidDatesException` or `TriggerInvalidSchedulingException` occurs from `500 Internal error` to the correct `400 Bad Request`.

The body of the response will contain additional information on the error.


### Max Number Of Entity Reached exception is now returned as a 403 error [[#4154](https://github.com/eclipse/kapua/pull/4154)]

Fixed the HTTP response code when a `KapuaMaxNumberOfItemsReachedException` occurs from `500 Internal error` to the correct `403 Forbidden`.

The body of the response will contain additional information on the error.


### Job Step Property value now supports JSON format for complex types [[#4161](https://github.com/eclipse/kapua/pull/4161)]

Allowed complex objects to be inserted with JSON format. Before only XML format was allowed.


### Datastore search APIs will now allow multiple `clientId` to be specified [[#4130](https://github.com/eclipse/kapua/pull/4130)]

Allowed the following REST API resources to accept more than one `clientId` as query parameter.

- `GET /{scopeId}/data/messages`
- `GET /{scopeId}/data/metric`
- `GET /{scopeId}/data/clients`
- `GET /{scopeId}/data/channels`


## DB Changes

### New column in MFA Option table [[#4115](https://github.com/eclipse/kapua/pull/4115)]

New `has_trust_me` column has been added to the `atht_mfa_option`.

New column defaults to `null` and doesn't need any migration. The code itself will populate the column accordingly on first update.
For large `atht_mfa_option` tables it might take sometime to be added.


### New column in Trigger Definition Property table [[#4134](https://github.com/eclipse/kapua/pull/4134)]

New `description` column has been added to `schdl_trigger_properties` and `schdl_trigger_definition_properties`.

The column default to `null` and the migration/population procedure will be performed by the Trigger Definition Aligner at application startup.

### Fixed the event subscription group handling [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Renamed variables from eventModuleName to subscriptionGroupId so it's more clear the meaning (since a module could have more subscriptions to the same address but different groups).
This change had impact in other areas because now every event handler should receive, once instantiated, the appropriate subscription group name.
For example, for the service , the generic
@Named("eventsModuleName") String eventModuleName
will be replaced with the correct
@Named("userEvtSubscriptionGroupId") String subscriptionGroupId
so instead of having the module name as group for this event subscription, the "svc-ath-" + containerIdResolver.getContainerId() string will be returned

### Re-organized message headers [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Re-organized the broker ServerPlugin logic to set message headers and the message categorization

### Improved message error handling [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Improved error handling to serialize messages before discarding them (to byte[]) and the abstract message converter (used more generic Camel Message instead of the JmsMessage)

### Removed trusted classes check [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Removed trusted classed from KapuaSession "security" check. That's no security improvement on doing that, only performances impact.

### Removed the client id set if null on connect [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Removed client id set if null on connect (it's not compliant with our use case using JMS 2.0) and modified the Camel connection factory to remove this parameter

### Correct the exception thrown by Authentication service [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Changed the exceptions thrown by ServiceClient (the one used by AuthenticationService) to generic because the implementations could be various obviously

### Changed Env Variables to set Service broker url [[#4198](https://github.com/eclipse/kapua/pull/4198)]

The `SERVICE_BROKER_HOST` and `SERVICE_BROKER_PORT` are replaced by a new env variable: `SERVICE_BROKER_URL` (default value `amqp://events-broker:5672`).<br>
Credentials are provided through `SERVICE_BROKER_USERNAME` and `SERVICE_BROKER_PASSWORD`.<br>
In this way more complex connection strings can be defined but, also, different event/service broker protocols could be used like MQTT or JMS.

### Changed Env Variables to set Event broker url [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Same for the event broker `where EVENT_BROKER_URL` will contain the full connection string to the event broker (see Artemis documentation for more informations).<br>
Credentials are provided through `EVENT_BROKER_USERNAME` and `EVENT_BROKER_PASSWORD`.

### Fixed the event subscription group handling [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Renamed variables from eventModuleName to subscriptionGroupId so it's more clear the meaning (since a module could have more subscriptions to the same address but different groups).
This change had impact in other areas because now every event handler should receive, once instantiated, the appropriate subscription group name.
For example, for the service , the generic
@Named("eventsModuleName") String eventModuleName
will be replaced with the correct
@Named("userEvtSubscriptionGroupId") String subscriptionGroupId
so instead of having the module name as group for this event subscription, the "svc-ath-" + containerIdResolver.getContainerId() string will be returned

### Re-organized message headers [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Re-organized the broker ServerPlugin logic to set message headers and the message categorization

### Improved message error handling [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Improved error handling to serialize messages before discarding them (to byte[]) and the abstract message converter (used more generic Camel Message instead of the JmsMessage)

### Removed trusted classes check [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Removed trusted classed from KapuaSession "security" check. That's no security improvement on doing that, only performances impact.

### Removed the client id set if null on connect [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Removed client id set if null on connect (it's not compliant with our use case using JMS 2.0) and modified the Camel connection factory to remove this parameter

### Correct the exception thrown by Authentication service [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Changed the exceptions thrown by ServiceClient (the one used by AuthenticationService) to generic because the implementations could be various obviously

### Changed Env Variables to set Service broker url [[#4198](https://github.com/eclipse/kapua/pull/4198)]

The `SERVICE_BROKER_HOST` and `SERVICE_BROKER_PORT` are replaced by a new env variable: `SERVICE_BROKER_URL` (default value `amqp://events-broker:5672`).<br>
Credentials are provided through `SERVICE_BROKER_USERNAME` and `SERVICE_BROKER_PASSWORD`.<br>
In this way more complex connection strings can be defined but, also, different event/service broker protocols could be used like MQTT or JMS.

### Changed Env Variables to set Event broker url [[#4198](https://github.com/eclipse/kapua/pull/4198)]

Same for the event broker `where EVENT_BROKER_URL` will contain the full connection string to the event broker (see Artemis documentation for more informations).<br>
Credentials are provided through `EVENT_BROKER_USERNAME` and `EVENT_BROKER_PASSWORD`.