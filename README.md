# dirsearch

Web interface for drilling through group memberships in an LDAP server, such as e.g. Active Directory (AD).

The server serves plain HTTP at port 8080, so you will have to run it behind a proxy to get HTTPS and a reasonable port.

## Searching

In the search field you may enter any of the following to list group memberships:

* Username
* E-mail address
* Common name

Whatever you enter must match exactly what is in the directory.
Everything found is represented as a link that you may drill into.

For individuals, you will see what groups they are a direct member of, and if enabled, what groups they are indirect members of.

For groups, you will see the same as for individuals, in addition to who are direct, and if enabled, indirect members.

## Configuration

Configuration is done using environment variables.
The following variables are supported:

| Variable | Required | Default | Description |
| -------- | -------- | ------- | ----------- |
| `CA_CERTS_FILE` | No | | Path to container-local file containing additional CA certificates in PEM format |
| `DN_CACHE_TTL_MIN` | No | `5` | How long, in minutes, to cache lookups |
| `FIND_INDIRECT_MEMBERSHIPS` | No | `true` | Recursively find group memberships, i.e. "groups within groups" |
| `LDAP_BASE_DN` | Yes | | The base DN in which to search |
| `LDAP_HOST` | Yes | | Hostname of LDAP server |
| `LDAP_PASSWORD` | Yes | | Password of user with which to bind to the LDAP server |
| `LDAP_PORT` | Yes | | LDAP server port |
| `LDAP_TLS` | No | `true` if port is 636, else `false` | Use TLS ("LDAPS") when connecting |
| `LDAP_USER` | Yes | | Username of user with which to bind to the LDAP server |

### Example

Running dirsearch using Docker while mounting a local CA certificate for the LDAP server:

```shell
docker run -it \
  -v /etc/ssl/certs/my-ca.pem:/etc/ssl/certs/my-ca.pem \
  -e CA_CERTS_FILE=/etc/ssl/certs/my-ca.pem \
  -e LDAP_HOST=ldap.example.com \
  -e LDAP_PORT=636 \           
  -e LDAP_BASE_DN=dc=example,dc=com \
  -e LDAP_USER=user@example.com \
  -e LDAP_PASSWORD=user-password \
  -p 8080:8080 \
  ghcr.io/sverrehu/dirsearch:v0.8.2
```
