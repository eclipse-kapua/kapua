Kapua Encryption Migrator
==========

## Introduction

This module contains a Java Application that leverages the Kapua APIs to migrate `Device.groupId` attribute to the new `Device.groupIds` attribute introduced in Kapua 2.1.0

This tool copies the former field data into the new one.

## Background

To improve the Device group-ability and make Access Groups more flexible, now a Device can be assigned with one or more Access Group. 

### Settings

No additional settings to run this migration.

Other useful properties from Kapua

| Name                       | Description                  | Default Value |
|----------------------------|------------------------------|---------------|
| commons.db.name            | The target database name     | kapuadb       |
| commons.db.username        | The target database username | kapua         | 
| commons.db.password        | The target database password | kapua         | 
| commons.db.connection.host | The target database host     | 192.168.33.10 |
| commons.db.connection.port | The target database port     | 3306          | 

#### Example usage

```bash
java -Dcommons.db.connection.host=somehost -jar kapua-device-group-migrator-2.1.0-SNAPSHOT-app.jar
```
