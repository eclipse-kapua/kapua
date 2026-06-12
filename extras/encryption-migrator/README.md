Kapua Encryption Migrator
==========

## Introduction

This module contains a Java Application that leverages the Kapua APIs to update the encryption key used to perform symmetric encryption of entity attributes on DB persistence and any other usage of
CryptoUtil class.

## Background

To improve security against unauthorized access to DB some fields that contain sensible data needs to be encrypted. This encryption makes difficult for an attacker to easily read data from the
database that may contain sensible data.

Kapua had already in place one-way encryption for Credentials and other resources while fields that required symmetric encryption didn't have a uniform and simple way to manage the encryption of the
fields.

## Changes in Kapua 2.0

With Kapua 2.0.0 entity attributes can be symmetric encrypted by simply adding an annotation to the rest of the JPA mappings for an entity attribute which instructs JPA to crypt and decrypt fields
values.

Before Kapua 2.0.0 fields that were symmetric-encrypted are:

- MfaOption.mfaSecretKey: which had its own mechanisms with its own key.

After Kapua 2.0.0 fields that are symmetric-encrypted are:

- MfaOption.mfaSecretKey
- JobStepProperty.propertyValue

Every field share the same key and lets the developer abstract from the specific task of encrypting fields which gets delegated to JPA. While this can affect performances (we lose the on-demand
encryption/decryption), we gain the ease of using this security feature.

## The ES indices Migration tool

The encryption migration tool has been implemented with two goals:

- Do the first migration: to allow every field listed before to be unified under a single encryption key and mechanism.
- Update the encryption secret key: it might happen that a secret key needs to be replaced (e.g.: the secret key disclosure)
  and this tools migrate every attribute from a given old encryption secret key to the newly given.

### Settings

The following settings are available as system properties when running the migration tool:

| Name                        | Description                                                               | Default Value    |
|-----------------------------|---------------------------------------------------------------------------|------------------|
| migrator.encryption.dryRun  | Run the tool without affecting data                                       | false            |
| migrator.encryption.key.old | The old secret key for symmetric encryption                               | changeMePlease!! |
| migrator.encryption.key.mfa | The old secret key for symmetric encryption of the MfaOption.mfaSecretKey | rv;ipse329183!@# |
| migrator.encryption.key.new | The new secret key for symmetric encryption                               | changedMeThanks! |

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
java -Dcommons.db.connection.host=somehost -Dmigrator.encryption.key.old=changeMePlease\!\! -Dmigrator.encryption.key.new=changedMeThanks\! -jar kapua-encryption-migrator-2.0.0-SNAPSHOT-app.jar
```
