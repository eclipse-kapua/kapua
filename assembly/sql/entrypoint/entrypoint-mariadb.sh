#!/usr/bin/env bash
################################################################################
#    Copyright (c) 2017, 2022 Red Hat Inc
#
#    This program and the accompanying materials are made
#    available under the terms of the Eclipse Public License 2.0
#    which is available at https://www.eclipse.org/legal/epl-2.0/
#
#    SPDX-License-Identifier: EPL-2.0
#
#    Contributors:
#        Red Hat Inc - initial API and implementation
#        Eurotech
################################################################################

# this is also entrypoint for mysql

set -eo pipefail

MY_CNF_FILE=/etc/mysql/conf.d/ec.cnf

#
# Reset custom configs file
{
  echo "# EC MariaDB custom configs"
} > ${MY_CNF_FILE}

#
# Set custom configs
{
  echo "[mysqld]"
  echo "max_connections=${CONNECTION_MAX:-200}"
  echo "max_allowed_packet=${PACKET_ALLOWED_MAX:-16MB}"
  echo "sql_mode=STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION" #to uniform sql mode between mysql/mariadb. Especially to remove ONLY_FULL_GROUP_BY mode (default in mysql 8.x) because not in line with some liquibase scripts (see https://www.geeksforgeeks.org/how-to-turn-off-only_full_group_by-in-mysql/)
} >> ${MY_CNF_FILE}

if [[ $# -gt 0 ]]; then
  exec /usr/local/bin/docker-entrypoint.sh "$@"
else
  exec /usr/local/bin/docker-entrypoint.sh "--lower-case-table-names=1"
fi