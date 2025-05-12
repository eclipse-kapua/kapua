################################################################################
# Copyright (c) 2022, 2025 Eurotech and/or its affiliates. All rights reserved.
################################################################################
FROM mysql:8.0

COPY maven /

# Assigning permission to write to mysql to allow write custom configurations
RUN chown -R mysql:0 /etc/mysql/conf.d && \
    chmod -R g=u /etc/mysql/conf.d

ENTRYPOINT ["/entrypoint-mariadb.sh"]