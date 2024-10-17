# TOP PRIROTY
- Clean code.
- Good code.

# PROTOCOL DESCRIPTION REQUIREMENTS

For each of the design choices provide a short justification: why did you choose to design it the way you did?

- [ ] 1. A short introduction: "This document describes _ _"
- [ ] 2. Terminology: a list of special terms you use
- [x] 3. The underlying transport you use (TCP or UDP).
- [x] 4. The used port number.
- [ ] 5. The overall architecture:
- [ ] • Who are the actors (nodes) in your solution?
- [ ] • Who are the clients, who is/are the server(s)?
- [ ] 6. The flow of information: when and how the messages are sent?
- [ ] 7. The type of your protocol:
- [ ] • Is your protocol connection-oriented or connection-less?
- [ ] • Is the protocol state-full or state-less?
- [ ] 8. The different types and special values (constants) used
- [ ] 9. The message format:
- [ ] • The allowed message types (sensor messages, command messages)
- [ ] • The type of marshalling used (fixed size, separators, TLV?)
- [ ] • Which messages are sent by which node? For example, are some messages only sent by
the control-panel node?
- [ ] 10. The different errors that can occur and how each node should react on the errors. For example,
what if a message in an unexpected format is received? Is it ignored, or does the recipient send
a reply with an error code?
- [ ] 11. Describe a realistic scenario: what would happen from user perspective and what messages
would be sent over the network?
- [ ] 12. The reliability mechanisms in your protocol (handling of network errors), if you have any
- [ ] 13. The security mechanisms in your protocol, if you have any

# General TODO

- [ ] Adrian? Handling connection errors and messaging errors properly.
- [x] ADRIAN. gitignore fil.
- [ ] Implementere Message og Command interfacer/klasser
- [ ] Lage ulike Message/Command underklasser
- [ ] Different sensor types must be supported. The different types are not fixed. I.e., your protocol should allow adding new sensor types when needed. 
- [ ] PROTOKOLL (IKKE GUI) Hvordan gjøre: "turn off all actuators (heaters, fans, window openers) at sensor node 7; or turn on all fans at sensor nodes 7, 12, and 19."
Mulig å skru av spesifikke actuators. Eller mulig å skru av alle actuators (av samme type). Hvordan ser denne protokollen ut?
- [ ] Commands to turn on/off all actuators for a node. 


- [x] ADRIAN. Lage klasse som applikasjonen kan kjøres fra.

# Test classes

- [ ] Lag test klasser som tester kommunikasjon mellom intermediary server og klientene (greenhouse nodes og control panels).
- [ ] Test at de kobler seg til serveren riktig, at de blir identifisert av serveren på riktig måte. Test hva som skjer om protokoller ikke blir fulgt, for eksemepel om første melding til server ikke er en identifiserende melding. 
- [ ] Test å sende kommandoer, sjekk respons.
- [ ] Test med flere kontrollere koblet til server, også spør om informasjon fra noder samtidig, både fra ulike noder og fra samme node og slikt.
- [ ] Finn på tester selv, pass på at koden er robust og at feil blir håndtert riktig. 
- [ ] Teste å åpne et kontrol panel på localhost pcen, og en annen pc. Koble seg til samme IP... som er IP til PCen som kjører hoved programmet.

# GREENHOUSE

- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN lage kommunikasjons klasse for greenhouse nodes.
- [x] ADRIAN. Koble til multiple greenhouse nodes til intermediary server.

Each sensor-node can do the following:
- [ ] Support different sensors. For example, one node may report humidity and light, while the other node reports only temperature
- [ ] Act as an actuator node as well. That is, each sensor node is a "sensor and actuator node", which can have several actuators attached
- [ ] Support different actuators. For example, fan, heater, window opener, door lock, shower opener.

Lenger version under. Vi trenger support for at en node kan ha flere sensorer og actuators av samme type.
- [ ] Hint: if your protocol will support only one instance of each sensor type on a node (only one
temperature sensor per node, one humidity sensor, etc.), it is probably enough to address the sensors
by their type. If you want to support multiple instances of the same sensor type per node, you need
to introduce the addressing of the sensors (and actuators). For example, temperature sensors 1 and
2 on the sensor node 7, humidity sensors 1, 2 and 3 on sensor node 12, etc.


# INTERMEDIARY SERVER

- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. Lage en intermediary server som kan brukes for å tilrettelegge kommunikasjon mellom greenhouse nodes og control panel.
- [x] ADRIAN. Ta i mot klienter som vil koble seg til.
- [x] ADRIAN. Motta meldinger fra klienter.
- [x] ADRIAN. Identifisere klienter.
- [ ] ADRIAN. Videre sende melding fra en klient til en/flere andre klienter.
- [x] ADRIAN. Huske hvilke klienter som er koblet til.
- [x] ADRIAN. Etablere kommunikasjon mellom intermediary server og control panel og greenhouse nodes.

# CONTROL PANEL

## GENERAL

- [ ] Håndtere å motta ulike datatyper hoss control panel. Foreløbig mottas kun desimal tall, og vi displayer det som tekst. Men hva om det er et bilde som mottas? En video? Lydopptak? Video med lyd? En kommentar? (for eksempel en advarsel om lavt batteri på noe, advarsel om farlig temperatur, brann, natta melding, whatever.)
Hvordan kan vi håndtere ulike datatyper uten å hardkode? slik det er lett å utvide løsningen.

## COMMUNICATION

- [ ] what to do when creating a new control panel?
    - [ ] ADRIAN Connect to server
    - [ ] ADRIAN send unique identifier to server so server knows id of control panel (and that it is a control panel)
    - [ ] ADRIAN Ask server for nodes
    - [ ] ADRIAN Control panel then asks for all their information so that it can show it, does this periodically.
    - [ ] ADRIAN How does control panel know what info to ask for?
    - [ ] ADRIAN How does control panel know what to do with the info it receives?

- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. koble til multiple control panels til intermediary server.
- [x] ADRIAN. lage kommunikasjons klasse for control panel
- [ ] ADRIAN. Spør hele tiden om informasjon for noden som vises i control panel GUI.
- [ ] ADRIAN. Send kommandoer til sensor nodes. For eksempel, spør om hvilke noder som finnes, spør om data, skru på en vifte, skru av en vifte, skru på en varmeovn, skru av en varmeovn, åpne et vindu, lukke et vindu, osv.
- [ ] ADRIAN. Receive actuator status data from any sensor node. For example, is a window open or closed, is the fan on or off? 
For å håndtere dette på en god måte. Hva med at greenhouse nodes hvor actuatoren ble endret, sier fra til server, som videre sier det til alle control panel. Slik unngår vi å måtte spør om status til actuators hele tiden (fra control panel)

## GUI

- [ ] Visualize charts.
- [ ] Visualize actuator status for each sensor node. Simple, textual visualization is enough.
- [ ] GUI Hvordan gjøre: "turn off all actuators (heaters, fans, window openers) at sensor node 7; or turn on all fans at sensor nodes 7, 12, and 19."
Mulig å skru av spesifikke actuators. Eller mulig å skru av alle actuators (av samme type). Hvordan ser dette ut i GUI?

# EXTRA WORK

- [ ] ADRIAN. 1. Resilience in case of network outages. The solution functions when the network connection is
temporarily lost. This means buffering data, retransmissions, reconnecting, etc
    - [ ] When failing to connect, try again after a few seconds. Do this 3 times. If it fails, show an error message to the user.
    - [ ] If the connection is lost, try to reconnect. If it fails, show an error message to the user.
    - [ ] If a message is not received, try to receive it again. If it fails, show an error message to the user.
    - [ ] Buffer data if the connection is lost. When the connection is reestablished, send the buffered data.


- [ ] 2. Data encryption. You can think of different methods of integrating security into your solution,
either using public-key cryptography or other methods.

- [ ] 3. Automated generation of unique identifiers (addresses) for sensor nodes. By default, the programmer can assign static addresses to sensor nodes when running them (as a command-line
argument). But you can design automated-address assignments as part of your protocol. For
example, look at DHCP as an inspiration.

- [ ] 4. Images/files as sensor data. Imagine a scenario when a web camera is attached to a sensor
node and the image frames it captures could be transmitted to the control panel. Image
transfer poses some extra challenges. It is therefore considered an extra feature if you manage
to integrate it in your protocol and implement it in your source code.
- [ ] 5. Support of more flexible actuator commands. By default, it is expected that a command is
sent to a specific sensor node, specific actuator. If you manage to support also either broadcast
commands (to all sensor nodes at a time), or multicast (to specific groups of sensor nodes),
this is considered an extra.
- [ ] 6. Support data of different resolutions. For example, the sensor nodes could buffer and aggregate
data, and the actuator nodes could request the 1- 1-minute averages, 1-hour averages, etc.


# WORK PROCESS DOCUMENTATION REQUIREMENTS
provide some form of documentation of the following:
- [ ] 1. How the work was planned in terms of iterations (sprints)
- [ ] 2. How the tasks were distributed - who was responsible for what. Note - it is NOT OK to
have someone in the group responsible only for creating videos. This course is primarily about
network protocols and network programming. Every group member must demonstrate expertise
within these fields. Perhaps one student works more on the protocol, another on the server
programming, and the third on client programming, that is fine. But it is not fine if one student
does all the programming, one just writes documentation (sprint reports) and the third creates
a video. How are the second and third students showing their computer network expertise?
- [ ] 3. How was the work broken down into issues? Remember the principles you learned about issue
definition in the System development course. Issues should be small, have descriptive names,
clear "definition of done", etc. For example, an issue like "improve GUI" is a bad issue. What
does it mean? When is it complete?
- [ ] 4. How the work was progressing - what did you accomplish in each sprint? Which issues were
planned, and which ones were completed?
- [ ] 5. A short retrospective after each sprint - what went well, and how can you improve?


It is suggested that you keep the sprint documentation in Git as well, but you are allowed to use
Wiki pages, Jira or other tools if you want.


# VIDEO

- [ ] 1. Introduction of the application: what problem does your solution solve? (1min)
- [ ] 2. The information, materials, approach you used, and the research you did. (1min)
- [ ] 3. The work process: how did you organize work throughout the semester? Role of each team
member? How did you work with the sprints? Were there any general themes for the sprints, phases of the project? (1min)
- [ ] 4. The architecture of your solution. What nodes are communicating? What is the responsibility
of each? Preferably, include model’s diagrams here. (1-2min)
- [ ] 5. Your communication protocol. Summarize it in a clear yet concise way. (3-4min)
- [ ] 6. Your solution and result. Explain what is working. Show a demo of the system. (2-4min)
- [ ] 7. Explain what extra work you have done (if any) for this to be considered an excellent project.
(1-3min)
- [ ] 8. Reflect on potential improvements and future work. (1 min)

Note: it is not a big problem if your video is 17 or 18 minutes, but don’t make it to 35 minutes!
Ability to present your ideas and results concisely is a general skill you must master. While the
video creation can be as simple as recording your screen during a Discord call, for better results it is
suggested that you plan your presentation beforehand, try how you would explain the concepts, how
much time that would take, etc. In other terms - prepare for the video presentation in the same way
as you would prepare for a physical presentation in the class.