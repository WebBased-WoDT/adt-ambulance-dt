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
