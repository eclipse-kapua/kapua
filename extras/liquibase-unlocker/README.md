Liquibase "changeLogLock" table unlocker tool
==========

## Introduction

This module contains a Java Application that unlocks a stucked deployment caused by a wrong execution of liquibase updates

## Background

The liquibase updates, run on changelog files, are performed concurrently by some Kapua containers. To ensure that a single container is running an update on the DB, aka to ensure the right concurrent
access to this shared resource, liquibase uses a table called DATABASECHANGELOGLOCK to track which entity is performing the update. A row in this table testifies the presence of an entity making an
update, therefore consulting it before performing an update allows you to understand if it is possible to acquire the lock on the resource.
After the update, the container that locked the recourse releases the lock by deleting the row in this table.

It may happen that, while inside the critical section, the container running the update is stopped for some reason, for example for a crash. After this event, the DATABASECHANGELOGLOCK is left with a
row therefore the lock will never be released. If subsequent containers tries to perform other liquibase updates (for example, the mentioned container that has been restarted) they will be blocked
endlessly trying to enter the critical section.

Tu unlock this situation, a manual operation on such table (for example, the deletion of the row or the deletion of the entire table) sometime works. The fact is that the liquibase api provides an
explicit command to troubleshoot this situation, in a more safe way, so it is encouraged to use it. Furthermore, the mentioned manual operation requires access to the DB retrieving credentials and so
on. The proposed script doesn't require this and get the job done with a single invocation.

#### Usage

```bash
java -jar kapua-liquibase-unlocker-2.0.0-SNAPSHOT-app.jar
```

To be used when the deployment is stucked for the afore mentioned reasons, the lock will be released and the deployment will continue.