# dirsearch

Web interface for drilling through group memberships in an LDAP server, such as e.g. Active Directory (AD).

## Configuration

Configuration is done using environment variables.
The following variables are supported:

| Variable | Required | Default | Description |
| -------- | -------- | ------- | ----------- |
| `CA_CERTS_FILE` | No | | Path to file containing additional CA certificates in PEM format |
| `DN_CACHE_TTL_MIN` | No | `5` | How long, in minutes, to cache lookups |
| `FIND_INDIRECT_MEMBERSHIPS` | No | `true` | Recursively find group memberships |
| `LDAP_BASE_DN` | Yes | | The base DN in which to search |
| `LDAP_HOST` | Yes | | Hostname of LDAP server |
| `LDAP_PASSWORD` | Yes | | Password of user with which to bind to the LDAP server |
| `LDAP_PORT` | Yes | | LDAP server port |
| `LDAP_TLS` | No | `true` if port is 636, else `false` | Use TLS ("LDAPS") when connecting |
| `LDAP_USER` | Yes | | Username of user with which to bind to the LDAP server |
