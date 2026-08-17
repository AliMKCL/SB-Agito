### Ports
- 8070: eurekaserver
- 8085: product
- 8090: stock
- 9000: gatewayserver
- 8000: keycloak
- 8443: nginx (Reverse proxy to bypass Keycloak https requirement)
- 9092: kafka
- 8888: temporary google auth server
- 3310: ClamAv (File virus scanning)

Enter keycloak via https://localhost:8443

The actual endpoint calls happen via gatewayserver: http://localhost:9000/...

