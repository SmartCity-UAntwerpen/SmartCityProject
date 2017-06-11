![SC Logo](/SmartCity Core/src/main/resources/favicon.ico?raw=true "SmartCity - Application logo")

SmartCityProject (simulation of the SmartCity)
================

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/461c995ba3d3429eb5d67fd1eba67c19)](https://www.codacy.com/app/SmartCity-UAntwerpen/SmartCityProject?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SmartCity-UAntwerpen/SmartCityProject&amp;utm_campaign=Badge_Grade)

The SmartCity Project is a small scale model of an IoT city with autonomous vehicles and other city facilities
controlled by our own developed cloud platform. The main goal of this project is to build a heterogeneous
distributed environment which includes the integration of real-time embedded systems (e.g. cars, traffic-lights)
with the cloud software. This system is developed for educational purposes. This repository only contains the modules used for the simulation part of this project.


SmartCity Modules
=================

 - SmartCity Core: is the hearth of the distributed system. This module controls the access to the database. In our current implementation this is only compatible with the simulated robots (which were not update to work with our new backbone). The updated version of the SmartCity Core (not found in this repository) is used for simulated F1 cars and drones.
 - SmartCity Central (no longer used in the current version): is a webservice which provides a web front-end to request the status of the SmartCity Core on a user friendly dashboard.
 - SmartCity Viewer (no longer used in the current version, only works with the simulated robots an SmartCity Core in this repository): this module creates a visual representation of the physical map. It shows all bots in the SmartCity on the map. This module was replaced by SmartCity MaaS in the current version of the project because it only works with the outdated robot simulation.


SimCity Modules
===============

 - SimCity: is a webservice which provides a web front-end to control the SimBots on the different SimCity Workers (only one local worker in the current implementation). It uses TCP communication to each separate core (robot core, drone core and F1 core).
 - SimCity Worker: this module allow to initialise and run simulation of bots in the SmartCity environment (through a terminal service). It uses TCP communication to the drone core and F1 core but performs the simulation of the robots locally.
 
Remark: The simulation branch is an older version of our system. The new version of the SimCity frontend (found on the master branch) does not run robot simulations locally (like it is the case with the simulation branch version) but disaptches commands over TCP to a seperate robot core (analogous to the F1 and drone implementation, the robot core is found in the SMartCityBot repository). The new version however, isn't fully operational. Right now there isn't a new implementation for the simulated robots. Thats is why we kept the simulation branch alive; it is an older, operational version but it works with an older version of the SmartCity Core (found in this repository). 
The SmartCity Central is no longer used. 
This repository is only used for the simulation of our SmartCity. 


Developed by
============

Huybrechts Thomas,
Janssens Arthur,
Vervliet Niels

University of Antwerp - 2016

Nick De Clerck,
Thomas Molkens

University of Antwerp - 2017
