# Ambulance WoDT Digital Twin

![Release](https://github.com/WebBased-WoDT/adt-ambulance-dt/actions/workflows/build-and-deploy.yml/badge.svg?style=plastic)
[![License: Apache License](https://img.shields.io/badge/License-Apache_License_2.0-yellow.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![Version](https://img.shields.io/github/v/release/WebBased-WoDT/adt-ambulance-dt?style=plastic)

This is the Ambulance WoDT Digital Twin layer that use Azure Digital Twins as a Twin Builder. 

## Usage
You need to specify the following environment variable:
- `SIGNALR_NEGOTIATION_URL`: Azure SignalR Negotiation url
- `SIGNALR_TOPIC_NAME`: the topic name used to publish events on SignalR
- `DIGITAL_TWIN_URI`: the uri of the exposed WoDT Digital Twin
- `AZURE_CLIENT_ID`: ID of an Azure AD application
- `AZURE_TENANT_ID`: ID of the application's Azure AD tenant
- `AZURE_CLIENT_SECRET`: the application's client secrets
- `AZURE_DT_ENDPOINT`: the Azure Digital Twins instance endpoint
- `AZURE_DT_ID`: the id of the Digital Twin on Azure Digital Twins
- `PHYSICAL_ASSET_ID`: the ID of the Physical Asset

If you want to run it via docker container:
1. Provide a `.env` file with all the environment variable described above
2. Run the container with the command:
   ```bash
    docker run ghcr.io/webbased-wodt/adt-ambulance-dt:latest
    ```
    1. If you want to try the REST-API from the external you need to provide a port mapping to port 3000.
    2. If you want to pass an environment file whose name is different from `.env` use the `--env-file <name>` parameter.

## Documentation
- Check out the website [here](https://webbased-wodt.github.io/adt-ambulance-dt/)
- Direct link to the *Code* documentation [here](https://webbased-wodt.github.io/adt-ambulance-dt/documentation/code-doc/)