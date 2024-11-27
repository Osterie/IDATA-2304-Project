# TOP PRIROTY

- Clean code.
- Good code.

# Next sprint

- [ ] TOBIAS for generateHeader method usage, what if header is null? handle this.
- [x] TOBIAS FIX When displaying sensor reading in sensor pane, the sensors type is repeated, for example "temperature: temperature = 20deg"
- [ ] KNUT clicking the ON/OFF buttons should not change the state when clicking in control panel, only change from the server response. If failed to get a response show a tooltip or somthn with a "failed or whatever" message.
- [ ] KNUT Refactor gui classes. For example ControlPanelApplication. Can create a class for node tab, instead of having the method do all it's shenanigans.
- [ ] ADRIAN Currently Message and MessageHeader i think have fromString methods. Perhaps another class should be used for this?
<!-- - [ ] DO NOT ASSIGN YOURSELF UNASSIGNED If we send data types in GetSensorDataCommand, we should on the receiving end check what datatype and handle it accordingly, instead of current solution. -->
- [ ] TOBIAS When errors happen and are unable to be handled, an error message should be displayed in GUI. A tooltip, text on screen. Whatever is the best solution.
- [ ] ADRIAN fix issue with images not being replaced, but instead being added when displaying images 
- [x] TOBIAS sensor pane should be scrollable if the sensors use up more space than which is allocated to the sensor pane. The scrollable pane should NOT be too small. Same size as without it.
- [x] TOBIAS ScrollPane for controlPanel.
- [ ] SEBASTIAN Create classes for audio, video and motion sensor, like for image sensor. Send data from greenhouse to control panel. Read and display the data at receiving end (control panel).
- [ ] DANIEL Actuator changes, for audio, video, motion sensor and image, the actuator should "Activate/Deactivate" or "Turn ON/ Turn OFF" the sensor, instead of sending an impact, this will just turn the sensors off, so that they cannot read anymore data.
- [ ] SEBASTIAN Actuator text should not be only ON or OFF, but should support for example CLOSED, OPEN, ACTIVE, UNACTIVE and such.
- [ ] DO NOT ASSIGN YOURSELF UNASSIGNED Add a readme file under resources, or explain in the classes that use the resources (ImageSensorReading, Audio, Video), that the files are read from file to simulate actual audio/video/image sensor data, but in reality the sensors would not read from this file system, but actually record audio, video and take images. (Which can be sent over the network)
- [ ] ADRIAN Sensors for light, fertilizer (Nitrogen), PH, wind speed etc.
- [ ] TOBIAS Implement encryption. Måtte flytte det til neste sprint.
- [ ] TOBIAS Hashing the data and storing it in header(?) for checking integrity. 
- [x] TOBIAS add hash in header of Message.

# GENERAL

- [x] ADRIAN when hovering image in sensors, change cursor to a hand to indicate it can be clicked.
- [x] ADRIAN instead of a toProtocolString method, just override the toString method for transmissions?
- [x] ADRIAN rename fromProtocolString to fromString.
- [ ] ADRIAN use factory to create transmissions (or only commands?)?
- [x] ADRIAN when a client is disconnected, it should send a client identification message again when it reconnects first.
- [x] ADRIAN When control panel disconencts, pause sending of periodic sensor data until reconnected again.

- [ ] ADRIAN Refactor ControlPanelCommunicationChannel.
- [ ] ADRIAN Refactor clientHandler, NodeConnectionHandler and ControlPanelCommunicationChannel to inherit from a common class.
- [x] ADRIAN Fix application using all available resources.
- [x] ADRIAN Create enum for non-predefined client id.
- [x] ADRIAN remove option to remove tabs in control panel
- [x] ADRIAN should actuator change send 0/1 instead of on/off? In that case we can show the text closed/open for window instead of on/off, for example.
- [x] ADRIAN When client handler for some reason disconnects, try to connect again (send identification message and such)
- [x] ADRIAN add enums for failure responses.
- [x] ADRIAN add colors to Logger methods. 
- [x] ADRIAN notifyChanges method of Actuator Class, refactor this method.
- [x] ADRIAN image sensor should have image: [Actual image]

- [x] TOBIAS add a refresh button to control panel

- [x] ADRIAN fix nodes sometimes not being created in gui.

- [ ] DANIEL when changing actuator state in Sensor Node GUI, should notify all the control panels.

- [ ] DANIEL When sending sensor data (GetSensorDataCommand), the message should contain the data type, for the numberSensorReading, can be For example NUM, for imagesensorreading, can be IMG.

- [x] ADRIAN When the control panel asks for sensor data, perhaps it does this a bit infrequently? Currenlty i believe it does it every 5 seconds. But do not change this to be too often. Is there another solution?

- [x] DANIEL a constant for the broadcast id? (ALL)

- [x] ADRIAN handle if port address already in use.

- [x] ADRIAN automatic generation of unique identifier for control panel.
- [x] ADRIAN support multiple control panesl, unique id

- [x] ADRIAN create host localhost constant or something


- [x] ADRIAN if "no sensor section for node x", ask for nodes again.
- [x] ADRIAN Refactor enums.
- [x] ADRIAN Create Transmission class which Response class and Command class can inherit from.
- [x] ADRIAN Create Response class
- [x] ADRIAN Implement Response class
- [x] ADRIAN Implement Success and Failuer responses.

- [x] SEBASTIAN Handle display if image better. Fix image displaying in greenhouse nodes. For example a small version of the image, which when clicked opens a new window with the full image.
- [x] SEBASTIAN Fix test file structure.
- [ ] TOBIAS AND OTHERS? Look over and fix bad javadoc
- [ ] When using copilot to write javadoc, check that the javadoc is correct

- [x] TOBIAS. Gjør om bilde til string, også den stringen til bilde. (for å sende over socket)
- [ ] ?TOBIAS. Klasse for control panel som kan gjøre om den mottatte informasjonen til riktig format.
- [x] ADRIAN. Følge protokoll, en header og en body. Header inneholer mottaker, mottakerID, datatype Body inneholder dataen.

# JAVADOC

- [ ] ADRIAN/DANIEL/KNUT/SEBASTIAN/TOBIAS. JAVADOC FOLKENS, JAVADOC.
- [x] TOBIAS Skriv javadoc for messages-klasser.
- [ ] ADRIAN Skriv javadoc for command-klasser.
- [x] TOBIAS Skriv javadoc for listeners-klasser.
- [x] TOBIAS Skriv javadoc for intermidiary-klasser.
- [x] SEBASTIAN Skriv javadoc for sensor-klasser.

- [ ] UNASSIGNED Skriv javadoc for greenhouse-klasser.
- [ ] UNASSIGNED Skriv javadoc for GUI-klasser.
- [ ] UNASSIGNED Skriv javadoc for control-panel-klasser.
- [ ] UNASSIGNED Skriv javadoc for run-klasser.
- [x] TOBIAS Skriv javadoc for tools-klasser.

- [ ] UNASSIGNED Gå gjennom alle klasser når produktet er ferdig, for nye klasser har kanskje ikkje javadoc.

# Fill in reports from meeting notes

- [x] TOBIAS report1
- [x] TOBIAS report2
- [ ] KNUT? report3
- [ ] TOBIAS report4

# PROTOCOL DESCRIPTION REQUIREMENTS

For each of the design choices provide a short justification: why did you choose to design it the way you did?

- [ ] 1. DANIEL A short introduction: "This document describes _ _"
- [x?] 2. TOBIAS. Terminology: a list of special terms you use
- [x] 3. The underlying transport you use (TCP or UDP).
- [x] 4. The used port number.
- [ ] 5. SEBASTIAN. The overall architecture:
- [ ]   SEBASTIAN. • Who are the actors (nodes) in your solution?
- [ ]   SEBASTIAN. • Who are the clients, who is/are the server(s)?
- [ ] ADRIAN 6. The flow of information: when and how the messages are sent?
- [ ] 7. KNUT. The type of your protocol:
- [ ]   KNUT. • Is your protocol connection-oriented or connection-less?
- [ ]   KNUT. • Is the protocol state-full or state-less?
- [ ] 8. DANIEL The different types and special values (constants) used
- [ ] 9. TOBIAS The message format:
- [ ]   TOBIAS • The allowed message types (sensor messages, command messages)
- [ ]   TOBIAS • The type of marshalling used (fixed size, separators, TLV?)
- [ ]   TOBIAS • Which messages are sent by which node? For example, are some messages only sent by the control-panel node?
- [ ] 10. DANIEL The different errors that can occur and how each node should react on the errors. For example,
what if a message in an unexpected format is received? Is it ignored, or does the recipient send
a reply with an error code?
- [ ] 11. DANIEL Describe a realistic scenario: what would happen from user perspective and what messages would be sent over the network?
- [x] 12. TOBIAS. The reliability mechanisms in your protocol (handling of network errors), if you have any
- [x] 13. TOBIAS (Tidligere daniel, men var lett å skrive om begge). The security mechanisms in your protocol, if you have any
- [x] 14. TOBIAS Må skrive meir om security fordi eg he endra på klassene.

# General - [ ]

- [x] Adrian use Clients enum constants instead of strings.
    - [x] Intermediary server
    - [x] Control panel
    - [x] Greenhouse

- [x] ADRIAN. Make identifier message follow our protocol.

- [x] Adrian Implement Header/Body messages for control panel 
- [x] Adrian Implement Header/Body messages for intermediary server
- [x] Adrian Implement Header/Body messages for greenhouse
- [ ] ADRIAN Handling connection errors and messaging errors properly.
- [x] ADRIAN. gitignore fil.
- [x] ADRIAN. Implementere Message og Command interfacer/klasser
- [x] ADRIAN. Command classes for client identification. 
- [x] ADRIAN. Lage ulike Message/Command underklasser
- [ ] SEBASTIAN PROTOKOLL (IKKE GUI) Hvordan gjøre: "turn off all actuators (heaters, fans, window openers) at sensor node 7; or turn on all fans at sensor nodes 7, 12, and 19." Mulig å skru av spesifikke actuators. Eller mulig å skru av alle actuators (av samme type). Hvordan ser denne protokollen ut?
- [x] SEBASTIAN Commands to turn on/off all actuators for a node. 
- [x] ADRIAN. Lage klasse som applikasjonen kan kjøres fra.
- [x] ADRIAN Refaktorer client handler felt, kanskje bruke egen klasse for å lagre klient type og klient id.
- [x] ADRIAN Instead of storing sockets in server, store clienthandler?


- [x] ADRIAN:
How to notify all clients about state update?
• Keep a reference list of connected clients in the server, update when
	• new client connects
	• a client disconnects
	• Client threads will "block" on read - wait for next command from client
• When a set-command is received:
	• Update the state
	• Call server.notifyClients() on the same thread
	• This method iterates over connected clients, sends a message to each client
Need to introduse a new thread on client side, on thread for reading, on thread for sending a command.

# Test classes

## COMMUNICATION

- [X] DANIEL. Lag test klasser som tester kommunikasjon mellom intermediary server og klientene (greenhouse nodes og control panels).
- [X] DANIEL. Test at de kobler seg til serveren riktig, at de blir identifisert av serveren på riktig måte. Test hva som skjer om protokoller ikke blir fulgt, for eksemepel om første melding til server ikke er en identifiserende melding. 
- [X] KNUT: Test å sende kommandoer, sjekk respons.
- [ ] SEBASTIAN Test med flere kontrollere koblet til server, også spør om informasjon fra noder samtidig, både fra ulike noder og fra samme node og slikt.
- [ ] UNASSIGNED Teste å åpne et kontrol panel på localhost pcen, og en annen pc. Koble seg til samme IP... som er IP til PCen som kjører hoved programmet.

## OTHER

- [ ] TOBIAS - Lag tester for alle klasser som kan testes.
- [ ] UNASSIGNED Test protokoll
- [ ] TOBIAS - Test Message og Command klasser
_
# GREENHOUSE

- [x] ADRIAN One Thread for writing and one for reading.

- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN lage kommunikasjons klasse for greenhouse nodes.
- [x] ADRIAN. Koble til multiple greenhouse nodes til intermediary server.

Each sensor-node can do the following:
- [x] SEBASTIAN/ADRIAN. Support different sensors. For example, one node may report humidity and light, while the other node reports only temperature
- [x] SEBASTIAN. Act as an actuator node as well. That is, each sensor node is a "sensor and actuator node", which can have several actuators attached
- [x] SEBASTIAN. Support different actuators. For example, fan, heater, window opener, door lock, shower opener.

- [x] SEBASTIAN. Hint: if your protocol will support only one instance of each sensor type on a node (only one temperature sensor per node, one humidity sensor, etc.), it is probably enough to address the sensors by their type. If you want to support multiple instances of the same sensor type per node, you need to introduce the addressing of the sensors (and actuators). For example, temperature sensors 1 and 2 on the sensor node 7, humidity sensors 1, 2 and 3 on sensor node 12, etc.


# INTERMEDIARY SERVER

- [x] ADRIAN One Thread for writing and one for reading.
- [x] ADRIAN Refactor intermediary server
- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. Lage en intermediary server som kan brukes for å tilrettelegge kommunikasjon mellom greenhouse nodes og control panel.
- [x] ADRIAN. Ta i mot klienter som vil koble seg til.
- [x] ADRIAN. Motta meldinger fra klienter.
- [x] ADRIAN. Identifisere klienter.
- [x] ADRIAN. Videre sende melding fra en klient til en/flere andre klienter.
- [x] ADRIAN. Huske hvilke klienter som er koblet til.
- [x] ADRIAN. Etablere kommunikasjon mellom intermediary server og control panel og greenhouse nodes.

# CONTROL PANEL

## GENERAL

- [x] TOBIAS: Add css.
- [ ] TOBIAS: Håndtere å motta ulike datatyper hoss control panel. Foreløbig mottas kun desimal tall, og vi displayer det som tekst. Men hva om det er et bilde som mottas? En video? Lydopptak? Video med lyd? En kommentar? (for eksempel en advarsel om lavt batteri på noe, ada varsel om farlig temperatur, brann, natta melding, whatever.)
Hvordan kan vi håndtere ulike datatyper uten å hardkode? slik det er lett å utvide løsningen.

## COMMUNICATION

- [x] ADRIAN One Thread for writing and one for reading.

- [x] what to do when creating a new control panel?
    - [X] ADRIAN Connect to server
    - [X] ADRIAN send unique identifier to server so server knows id of control panel (and that it is a control panel)
    - [X] ADRIAN Ask server for nodes
    - [x] ADRIAN Control panel then asks for all their information so that it can show it, does this periodically.
    - [x] ADRIAN How does control panel know what info to ask for?
    - [x] ADRIAN How does control panel know what to do with the info it receives?

- [ ] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. koble til multiple control panels til intermediary server.
- [x] ADRIAN. lage kommunikasjons klasse for control panel
- [x] ADRIAN. Spør hele tiden om informasjon for noden som vises i control panel GUI.
- [x] ADRIAN. Send kommandoer til sensor nodes. For eksempel, spør om hvilke noder som finnes, spør om data, skru på en vifte, skru av en vifte, skru på en varmeovn, skru av en varmeovn, åpne et vindu, lukke et vindu, osv.
- [x] ADRIAN. Receive actuator status data from any sensor node. For example, is a window open or closed, is the fan on or off? 
- [x] For å håndtere dette på en god måte. Hva med at greenhouse nodes hvor actuatoren ble endret, sier fra til server, som videre sier det til alle control panel. Slik unngår vi å måtte spør om status til actuators hele tiden (fra control panel)
- [x] ADRIAN receive sensor data periodically only for current tab in control panel.
- [x] SEBASTIAN. Send image data from greenhouse to control panel. 
- [ ] SEBASTIAN Parse image reading in control panel.

## GUI

- [ ] KNUT. Implement ComponentBuilder class
- [X] KNUT. Visualize charts.
- [X] KNUT. GUI Hvordan gjøre: "turn off all actuators (heaters, fans, window openers) at sensor node 7; or turn on all fans at sensor nodes 7, 12, and 19." Mulig å skru av spesifikke actuators. Eller mulig å skru av alle actuators (av samme type). Hvordan ser dette ut i GUI?
- [ ] KNUT. Utbedre UI for ControlPanelApplication, Sett TurnOffAllActuators button til actuatorPane og fjern nodeSelect
- [X] KNUT. Class to create javafx components containing text, or an image, or whatever, should be genereal. Based on data gotten from SensorReading class or whatever.
- [x] ADRIAN. Make actuator buttons send data.
- [ ] DANIEL. Do the task which was sent in discord. Which was something about gui components for adding more sensors and actuators to a node. Also in the main window of the greenhouse, have components for adding more nodes, with sensors and actuators. Also when a new node is added, connect it to the server and notify the control panels

# EXTRA WORK

- [ ] ADRIAN. 1. Resilience in case of network outages. The solution functions when the network connection is temporarily lost. This means buffering data, retransmissions, reconnecting, etc
    - [x] When failing to connect, try again after a few seconds. Do this 3 times. If it fails, show an error message to the user.
    - [x] If the connection is lost, try to reconnect. 
    - [ ] If reconnection fails, show an error message to the user. (then they can choose to reload the control panel perhaps when the server is back up. Display a "Server down" message in the control panel)
    - [ ] If a message is not received, try to receive it again. If it fails, show an error message to the user.
    - [x] Buffer data if the connection is lost. When the connection is reestablished, send the buffered data.


- [x] TOBIAS. 2. Data encryption. You can think of different methods of integrating security into your solution,
either using public-key cryptography or other methods.
- [x] TOBIAS Convert symmetric encryption to RSA.

- [ ] SEBASTIAN 3. Automated generation of unique identifiers (addresses) for sensor nodes. By default, the programmer can assign static addresses to sensor nodes when running them (as a command-line argument). But you can design automated-address assignments as part of your protocol. For example, look at DHCP as an inspiration.

- [x] 4. SEBASTIAN/ADRIAN Images/files as sensor data. 

- [x] 5. ADRIAN Support of more flexible actuator commands. By default, it is expected that a command is sent to a specific sensor node, specific actuator. If you manage to support also either broadcast commands (to all sensor nodes at a time), or multicast (to specific groups of sensor nodes), this is considered an extra.


# WORK PROCESS DOCUMENTATION REQUIREMENTS
provide some form of documentation of the following:
- [ ] KNUT 1. How the work was planned in terms of iterations (sprints)
- [ ] ADRIAN 2. How the tasks were distributed - who was responsible for what. Note - it is NOT OK to have someone in the group responsible only for creating videos. This course is primarily about network protocols and network programming. Every group member must demonstrate expertise within these fields. Perhaps one student works more on the protocol, another on the server programming, and the third on client programming, that is fine. But it is not fine if one student does all the programming, one just writes documentation (sprint reports) and the third creates a video. How are the second and third students showing their computer network expertise?
- [ ] KNUT 3. How was the work broken down into issues? Remember the principles you learned about issue
definition in the System development course. Issues should be small, have descriptive names,
clear "definition of done", etc. For example, an issue like "improve GUI" is a bad issue. What
does it mean? When is it complete?
- [ ] DANIEL 4. How the work was progressing - what did you accomplish in each sprint? Which issues were
planned, and which ones were completed?
- [ ] SEBASTIAN 5. A short retrospective after each sprint - what went well, and how can you improve?


It is suggested that you keep the sprint documentation in Git as well, but you are allowed to use
Wiki pages, Jira or other tools if you want.


# VIDEO

- [ ] KNUT 1. Introduction of the application: what problem does your solution solve? (1min)
- [ ] SEBASTIAN 2. The information, materials, approach you used, and the research you did. (1min)
- [ ] KNUT 3. The work process: how did you organize work throughout the semester? Role of each team
member? How did you work with the sprints? Were there any general themes for the sprints, phases of the project? (1min)
- [ ] DANIEL 4. The architecture of your solution. What nodes are communicating? What is the responsibility
of each? Preferably, include model’s diagrams here. (1-2min)
- [ ] TOBIAS OG DANIEL 5. Your communication protocol. Summarize it in a clear yet concise way. (3-4min)
- [ ] ALLE 6. Your solution and result. Explain what is working. Show a demo of the system. (2-4min)
- [ ] 7. SEBASTIAN / TOBIAS Explain what extra work you have done (if any) for this to be considered an excellent project.
(1-3min)
- [ ] ADRIAN 8. Reflect on potential improvements and future work. (1 min)

Note: it is not a big problem if your video is 17 or 18 minutes, but don’t make it to 35 minutes!
Ability to present your ideas and results concisely is a general skill you must master. While the
video creation can be as simple as recording your screen during a Discord call, for better results it is
suggested that you plan your presentation beforehand, try how you would explain the concepts, how
much time that would take, etc. In other terms - prepare for the video presentation in the same way
as you would prepare for a physical presentation in the class.