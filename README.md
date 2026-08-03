### Ports
- 8070: eurekaserver
- 8085: product
- 8090: stock
- 9000: gatewayserver
- 8000: keycloak
- 8443: nginx
- 9092: kafka
- 8888: temporary google auth server

Enter keycloak via https://localhost:8443

The actual endpoint calls happen via gatewayserver: http://localhost:9000/...

