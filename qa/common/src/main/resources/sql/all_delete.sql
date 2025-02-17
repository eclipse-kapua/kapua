/*******************************************************************************
 * Copyright (c) 2017, 2022 Eurotech and/or its affiliates and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/

UPDATE act_account SET scope_id = null;

DELETE
FROM act_account
WHERE id NOT IN (1);

DELETE
FROM atht_credential
WHERE NOT (scope_id = 1 AND id IN (1, 2, 3));

DELETE
FROM atht_access_token;

DELETE
FROM atht_mfa_option;

DELETE
FROM atht_scratch_code;

DELETE
FROM athz_role
WHERE NOT (scope_id = 1 AND id = 1);

DELETE
FROM athz_role_permission
WHERE NOT (scope_id = 1 AND role_id = 1);

DELETE
FROM dvc_device_connection;

DELETE
FROM dvc_device;

DELETE
FROM dvc_device_event;

DELETE
FROM sys_configuration
WHERE NOT (scope_id = 1 AND id IN (1, 2, 3, 4, 5, 6, 7, 8, 9));

DELETE
FROM usr_user
WHERE NOT (scope_id = 1 AND id IN (1, 2));

DELETE
FROM athz_access_info
WHERE NOT (scope_id = 1 AND id IN (1, 2));

DELETE
FROM athz_access_permission
WHERE NOT (scope_id = 1 AND id = 1);

DELETE
FROM athz_access_role
WHERE NOT (scope_id = 1 AND id = 1);

DELETE
FROM athz_domain_actions
WHERE domain_id NOT IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);

DELETE
FROM athz_domain
WHERE NOT (scope_id IS null AND id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21));

DELETE
FROM athz_group;

DELETE
FROM tag_tag;

DELETE
FROM job_job;

DELETE
FROM job_job_execution;

DELETE
FROM job_job_step;

DELETE
FROM job_job_step_definition
WHERE name NOT IN ('Asset Write','Bundle Start','Bundle Stop','Command Execution','Configuration Put','Keystore Certificate Create','Keystore Keypair Create','Keystore Item Delete','Package Download / Install','Package Uninstall','Container Start','Container Stop');

DELETE
FROM job_job_step_properties;

DELETE
FROM job_job_target;

DELETE
FROM schdl_trigger;

DELETE
FROM schdl_trigger_properties;

DELETE
FROM endp_endpoint_info;
