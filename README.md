![SC Logo](/SmartCity Core/src/main/resources/favicon.ico?raw=true "SmartCity - Application logo")

SmartCityProject (simulation of the SmartCity)
================

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/461c995ba3d3429eb5d67fd1eba67c19)](https://www.codacy.com/app/SmartCity-UAntwerpen/SmartCityProject?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SmartCity-UAntwerpen/SmartCityProject&amp;utm_campaign=Badge_Grade)

The SmartCity Project is a small scale model of an IoT city with autonomous vehicles and other city facilities
controlled by our own developed cloud platform. The main goal of this project is to build a heterogeneous
distributed environment which includes the integration of real-time embedded systems (e.g. cars, traffic-lights)
with the cloud software. This system is developed for educational purposes.


SmartCity Modules
=================

 - SmartCity Core: is the hearth of the distributed system. This module controls the access to the database.
 - SmartCity Central: is a webservice which provides a web front-end to request the status of the SmartCity Core on a user friendly dashboard.
 - SmartCity Viewer: this module creates a visual representation of the physical map. It shows all bots in the SmartCity on the map.


SimCity Modules
===============

 - SimCity: is a webservice which provides a web front-end to control the SimBots on the different SimCity Workers. It uses TCP communication to each separate core (robot core, drone core and F1 core).
 - SimCity Worker: this module allow to initialise and run simulation of bots in the SmartCity environment. It uses TCP communication to the drone core and F1 core but perform the simulation of the robots locally.
 
Remark: The simulation branch is an older version of our system. The new version separates the SimCity frontend from the SimCity Worker. The new version however, isn't fully operational. Right now there isn't a new implementation for the simulated robots. Thats is why we kept the simulation branch alive; it is an older, operational version but it works with an older version of the SimCity Core. 
The SmartCity Central is no longer used. 
This repository is only used for the simulation of our SmartCity. 


Developed by
============

Huybrechts Thomas,
Janssens Arthur,
Vervliet Niels

University of Antwerp - 2016
