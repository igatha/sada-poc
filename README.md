# sada-protocol-poc
A delay-tolerant local messaging protocol using hotspots — like a wireless internet.

## What is this?

This is a proof of concept for a "local internet" that can be plugged into international internet infrastructure.

## Who is this for?

It is for people who are in war zones.

For example, people in Gaza walk to the border of Egypt to get cell service. Read more about it [here](https://www.reuters.com/world/middle-east/desperate-news-gazans-struggle-with-mobile-network-2024-02-02/).

## How does it work?

There are three components here:
1. Towers: Hotspots that manage a network.
2. Nodes: Devices that connect to towers.
3. Relays: Devices that sync towers with each other.

### Towers

Towers are hotspots with a VPN server.

They receive traffic from nodes and handle it based on supported protocols.

### Nodes

Nodes are any device that can connect to a tower via WiFi.

They will have applications that use the protocols supported by the tower.

### Relays

Relays are nodes that only exist to sync towers with each other.

They are used to transmit data over larger distances and, theoretically, plug into international internet infrastructure.

## What is the state of this project?

This is a proof of concept to explore how feasible this idea is.
