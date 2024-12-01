# TOP PRIROTY

- Clean code.
- Good code.

- [ ] TODO create a starter for only IntermediaryServer.

# important

- [ ] SEBASTIAN GOOGLE CHECK ON MY RESPONSIBILITIES.
- [ ] TODO change nodeId to only be int, or that it can be string. (go for can be string.)

- [x] TOBIAS Refactor tools.

- [x] SEBASTIAN Refactor SensorReadingParser.java

- [x] KNUT pass på god kode for gui controlpanel (start metoden gjør meget mye.).
- [x] ADRIAN Ensure high quality code for all classes under controlpanel folder (MISSING SOME JAVADOC STILL (tyty))
- [x] ADRIAN Ensure high quality code for all classes under intermediary server folder
  - [x] ADRIAN clienthandler folder
  - [x] ADRIAN server folder
- [x] ADRIAN Ensure high quality code for all classes under greenhouse folder
  - [x] ADRIAN actuator folder
  - [x] ADRIAN sensor folder
  - [x] ADRIAN other
- [x] ADRIAN Ensure high quality code for all classes under messages folder
  - [x] ADRIAN Commands
    - [x] ADRIAN Greenhouse
    - [x] ADRIAN Other
  - [x] ADRIAN Responses
  - [x] ADRIAN Other
- [x] ADRIAN Ensure high quality code for TcpConnection.java
- [x] ADRIAN Ensure high quality code for SocketCommunicationChannel.java
- [x] TOBIAS Forsikre god kode kvalitet i tools
- [x] SEBASTIAN Pass på god kode for GUI common folder

# Probably wont do

- [ ] TODO dont print out audio in console...
- [ ] TODO can add a location variable to the sensors to show where they are located? if user wants. Would be especially good for image sensor and motion sensor and whatver
- [ ] TODO make control panel and nodes not scrollable, only sensorpane and actuaotrPane.

# CURRENT

- [x] ADRIAN Add a readme file under resources, or explain in the classes that use the resources (ImageSensorReading, Audio, Video), that the files are read from file to simulate actual audio/video/image sensor data, but in reality the sensors would not read from this file system, but actually record audio, video and take images. (Which can be sent over the network)
- [x] ADRIAN create a starter for only IntermediaryServer.


- [x] ADRIAN refactor commands.
- [x] ADRIAN Use enums for SensorActuatorNodeInfoParser and GetNodeCommand.
- [x] ADRIAN builder pattern for devices instead of DeviceFactory.
- [x] ADRIAN ControlPanelCommunicationChannel, instead of checking if .equals("Get_node") should check if classes are equal or something, idk. some better solution!
- [x] ADRIAN Separate factory for actuators.
- [x] TOBIAS FIX When displaying sensor reading in sensor pane, the sensors type is repeated, for example "temperature: temperature = 20deg"
- [X] KNUT Refactor gui classes. For example ControlPanelApplication. Can create a class for node tab, instead of having the method do all it's shenanigans.
- [x] TOBIAS When errors happen and are unable to be handled, an error message should be displayed in GUI. A tooltip, text on screen. Whatever is the best solution.
- [x] ADRIAN fix issue with images not being replaced, but instead being added when displaying images
- [x] TOBIAS sensor pane should be scrollable if the sensors use up more space than which is allocated to the sensor pane. The scrollable pane should NOT be too small. Same size as without it.
- [x] TOBIAS Judge if sensor pane should be scrollable. I tried it out, and it didn't look good, and it didn't feel good to use.
- [x] TOBIAS ScrollPane for controlPanel.
- [x] SEBASTIAN Create classes for audio, video and motion sensor, like for image sensor. Send data from greenhouse to control panel. Read and display the data at receiving end (control panel).
- [ ] DANIEL Actuator changes, for audio, video, motion sensor and image, the actuator should "Activate/Deactivate" or "Turn ON/ Turn OFF" the sensor, instead of sending an impact, this will just turn the sensors off, so that they cannot read anymore data.
- [x] SEBASTIAN Actuator text should not be only ON or OFF, but should support for example CLOSED, OPEN, ACTIVE, UNACTIVE and such. Each actuator should have "Turned on/off text" when they are made.

- [x] ADRIAN Sensors for light, PH.
- [ ] TOBIAS Implement encryption. PAIN
- [x] ADRIAN Handling is the integrity of the data is altered.
- [x] TOBIAS Hashing the data and storing it in header(?) for checking integrity. 
- [x] TOBIAS add hash in header of Message.
- [x] ADRIAN handle message should take a message


# GENERAL

- [x] ADRIAN when hovering image in sensors, change cursor to a hand to indicate it can be clicked.
- [x] ADRIAN instead of a toProtocolString method, just override the toString method for transmissions?
- [x] ADRIAN rename fromProtocolString to fromString.
- [x] ADRIAN when a client is disconnected, it should send a client identification message again when it reconnects first.
- [x] ADRIAN When control panel disconencts, pause sending of periodic sensor data until reconnected again.

- [x] ADRIAN Refactor ControlPanelCommunicationChannel.
  - [x] ADRIAN Create parser for SensorActuatorNodeInfo.
  - [x] ADRIAN Create Sensor readings parser.
  - [x] ADRIAN create ControlPanelResponseHandler
  - [x] ADRIAN give ControlPanelLogic more responsibility.
- [x] ADRIAN Refactor clientHandler, NodeConnectionHandler and ControlPanelCommunicationChannel to inherit from a common class.
  - [x] ADRIAN ClientHandler
  - [x] ADRIAN NodeConnectionHandler
  - [x] ADRIAN ControlPanelCommunicationChannel
- [x] ADRIAN Fix application using all available resources.
- [x] ADRIAN Create enum for non-predefined client id.
- [x] ADRIAN remove option to remove tabs in control panel
- [x] ADRIAN should actuator change send 0/1 instead of on/off? In that case we can show the text closed/open for window instead of on/off, for example.
- [x] ADRIAN When client handler for some reason disconnects, try to connect again (send identification message and such)
- [x] ADRIAN add enums for failure responses.
- [x] ADRIAN add colors to Logger methods. 
- [x] ADRIAN notifyChanges method of Actuator Class, refactor this method.
- [x] ADRIAN image sensor should have image: [Actual image]

- [x] ADRIAN add a working refresh button to control panel

- [x] ADRIAN fix nodes sometimes not being created in gui.

- [x] DANIEL when changing actuator state in Sensor Node GUI, should notify all the control panels.

- [ ] DANIEL Actuator class can be abstract, other classes can inherit. ONe for deactivating/activating sensor, onher for applying impact

- [x] DANIEL When sending sensor data (GetSensorDataCommand), the message should contain the data type, for the numberSensorReading, can be For example NUM, for imagesensorreading, can be IMG.

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
- [ ] TOBIAS. ? Klasse for control panel som kan gjøre om den mottatte informasjonen til riktig format.
- [x] ADRIAN. Følge protokoll, en header og en body. Header inneholer mottaker, mottakerID, datatype Body inneholder dataen.

# JAVADOC

- [x] TOBIAS Skriv javadoc for messages-klasser.
- [x] ADRIAN Skriv javadoc for command-klasser.
- [x] TOBIAS Skriv javadoc for listeners-klasser.
- [x] TOBIAS Skriv javadoc for intermidiary-klasser.
- [x] SEBASTIAN Skriv javadoc for sensor-klasser.

- [x] ADRIAN Skriv javadoc for greenhouse-klasser.
- [x] KNUT Skriv javadoc for GUI-klasser.
- [x] ADRIAN Skriv javadoc for control-panel-klasser.
- [x] ADRIAN Skriv javadoc for run-klasser.
- [x] TOBIAS Skriv javadoc for tools-klasser.

- [ ] ALLE TODO Gå gjennom alle klasser når produktet er ferdig, for nye klasser har kanskje ikkje javadoc.

# Fill in reports from meeting notes

- [x] TOBIAS report1
- [x] TOBIAS report2
- [x] KNUT report3
- [x] TOBIAS report4
- [x] TOBIAS report5

# PROTOCOL DESCRIPTION REQUIREMENTS

For each of the design choices provide a short justification: why did you choose to design it the way you did?

- [ ] DANIEL 1. A short introduction: "This document describes _ _"
- [x] TOBIAS 2. ?. Terminology: a list of special terms you use
- [x] 3. The underlying transport you use (TCP or UDP).
- [x] 4. The used port number.
- [ ] SEBASTIAN 5. . The overall architecture:
- [ ] SEBASTIAN. • Who are the actors (nodes) in your solution?
- [ ] SEBASTIAN. • Who are the clients, who is/are the server(s)?
- [x] ADRIAN 6. The flow of information: when and how the messages are sent?
- [x] KNUT. 7.  The type of your protocol:
- [X] KNUT. • Is your protocol connection-oriented or connection-less?
- [X] KNUT. • Is the protocol state-full or state-less?
- [x] DANIEL 8.  The different types and special values (constants) used
- [x] TOBIAS 9.  The message format:
- [x] TOBIAS ? • The allowed message types (sensor messages, command messages)
- [x] TOBIAS • The type of marshalling used (fixed size, separators, TLV?)
- [x] TOBIAS • Which messages are sent by which node? For example, are some messages only sent by the control-panel node?
- [ ] DANIEL 10.  The different errors that can occur and how each node should react on the errors. For example,
what if a message in an unexpected format is received? Is it ignored, or does the recipient send
a reply with an error code?
- [ ] DANIEL 11.  Describe a realistic scenario: what would happen from user perspective and what messages would be sent over the network?
- [x] TOBIAS 12. . The reliability mechanisms in your protocol (handling of network errors), if you have any
- [x] TOBIAS 13.  (Tidligere daniel, men var lett å skrive om begge). The security mechanisms in your protocol, if you have any
- [x] TOBIAS 14.  Må skrive meir om security fordi eg he endra på klassene.

# General

- [x] Adrian use Clients enum constants instead of strings.
    - [x] Intermediary server
    - [x] Control panel
    - [x] Greenhouse

- [x] ADRIAN. Make identifier message follow our protocol.

- [x] Adrian Implement Header/Body messages for control panel 
- [x] Adrian Implement Header/Body messages for intermediary server
- [x] Adrian Implement Header/Body messages for greenhouse
- [x] ADRIAN Handling connection errors and messaging errors properly.
- [x] ADRIAN. gitignore fil.
- [x] ADRIAN. Implementere Message og Command interfacer/klasser
- [x] ADRIAN. Command classes for client identification. 
- [x] ADRIAN. Lage ulike Message/Command underklasser
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
- [x] ADRIAN Test med flere kontrollere koblet til server, også spør om informasjon fra noder samtidig, både fra ulike noder og fra samme node og slikt.
- [ ] TODO Teste å åpne et kontrol panel på localhost pcen, og en annen pc. Koble seg til samme IP... som er IP til PCen som kjører hoved programmet.

## OTHER

- [ ] TOBIAS - Lag tester for alle klasser som kan testes.
- [ ] TODO Test protokoll
- [ ] TOBIAS - Test Message og Command klasser
_
# GREENHOUSE

- [x] ADRIAN One Thread for writing and one for reading.

- [x] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN lage kommunikasjons klasse for greenhouse nodes.
- [x] ADRIAN. Koble til multiple greenhouse nodes til intermediary server.

Each sensor-node can do the following:
- [x] SEBASTIAN/ADRIAN. Support different sensors. For example, one node may report humidity and light, while the other node reports only temperature
- [x] SEBASTIAN. Act as an actuator node as well. That is, each sensor node is a "sensor and actuator node", which can have several actuators attached
- [x] ADRIAN. Support different actuators. For example, fan, heater, window opener, door lock, shower opener.

- [x] SEBASTIAN. Hint: if your protocol will support only one instance of each sensor type on a node (only one temperature sensor per node, one humidity sensor, etc.), it is probably enough to address the sensors by their type. If you want to support multiple instances of the same sensor type per node, you need to introduce the addressing of the sensors (and actuators). For example, temperature sensors 1 and 2 on the sensor node 7, humidity sensors 1, 2 and 3 on sensor node 12, etc.


# INTERMEDIARY SERVER

- [x] ADRIAN One Thread for writing and one for reading.
- [x] ADRIAN Refactor intermediary server
- [x] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. Lage en intermediary server som kan brukes for å tilrettelegge kommunikasjon mellom greenhouse nodes og control panel.
- [x] ADRIAN. Ta i mot klienter som vil koble seg til.
- [x] ADRIAN. Motta meldinger fra klienter.
- [x] ADRIAN. Identifisere klienter.
- [x] ADRIAN. Videre sende melding fra en klient til en/flere andre klienter.
- [x] ADRIAN. Huske hvilke klienter som er koblet til.
- [x] ADRIAN. Etablere kommunikasjon mellom intermediary server og control panel og greenhouse nodes.

# CONTROL PANEL

## GENERAL

- [x] ADRIAN refactor control panel starter
- [x] TOBIAS: Add css.
- [ ] TOBIAS: Håndtere å motta ulike datatyper hoss control panel. Foreløbig mottas kun desimal tall, og vi displayer det som tekst. Men hva om det er et bilde som mottas? En video? Lydopptak? Video med lyd? En kommentar? (for eksempel en advarsel om lavt batteri på noe, ada varsel om farlig temperatur, brann, natta melding, whatever.)
Hvordan kan vi håndtere ulike datatyper uten å hardkode? slik det er lett å utvide løsningen.

## COMMUNICATION

- [x] ADRIAN One Thread for writing and one for reading.

- [x] ADRIAN what to do when creating a new control panel?
    - [X] ADRIAN Connect to server
    - [X] ADRIAN send unique identifier to server so server knows id of control panel (and that it is a control panel)
    - [X] ADRIAN Ask server for nodes
    - [x] ADRIAN Control panel then asks for all their information so that it can show it, does this periodically.
    - [x] ADRIAN How does control panel know what info to ask for?
    - [x] ADRIAN How does control panel know what to do with the info it receives?

- [x] ADRIAN. Håndtere situasjoner hvor feil oppstår.
- [x] ADRIAN. koble til multiple control panels til intermediary server.
- [x] ADRIAN. lage kommunikasjons klasse for control panel
- [x] ADRIAN. Spør hele tiden om informasjon for noden som vises i control panel GUI.
- [x] ADRIAN. Send kommandoer til sensor nodes. For eksempel, spør om hvilke noder som finnes, spør om data, skru på en vifte, skru av en vifte, skru på en varmeovn, skru av en varmeovn, åpne et vindu, lukke et vindu, osv.
- [x] ADRIAN. Receive actuator status data from any sensor node. For example, is a window open or closed, is the fan on or off? 
- [x] For å håndtere dette på en god måte. Hva med at greenhouse nodes hvor actuatoren ble endret, sier fra til server, som videre sier det til alle control panel. Slik unngår vi å måtte spør om status til actuators hele tiden (fra control panel)
- [x] ADRIAN receive sensor data periodically only for current tab in control panel.
- [x] SEBASTIAN. Send image data from greenhouse to control panel. 
- [x] SEBASTIAN Parse image reading in control panel.

## GUI

- [X] KNUT. GUI Hvordan gjøre: "turn off all actuators (heaters, fans, window openers) at sensor node 7; or turn on all fans at sensor nodes 7, 12, and 19." Mulig å skru av spesifikke actuators. Eller mulig å skru av alle actuators (av samme type). Hvordan ser dette ut i GUI?
- [x] KNUT. Utbedre UI for ControlPanelApplication, Sett TurnOffAllActuators button til actuatorPane og fjern nodeSelect
- [X] KNUT. Class to create javafx components containing text, or an image, or whatever, should be genereal. Based on data gotten from SensorReading class or whatever.
- [X] KNUT Implement Component Builder.
- [x] ADRIAN. Make actuator buttons send data.
- [ ] DANIEL. Do the task which was sent in discord. Which was something about gui components for adding more sensors and actuators to a node. Also in the main window of the greenhouse, have components for adding more nodes, with sensors and actuators. Also when a new node is added, connect it to the server and notify the control panels

# EXTRA WORK

- [x] ADRIAN. 1. Resilience in case of network outages. The solution functions when the network connection is temporarily lost. This means buffering data, retransmissions, reconnecting, etc
    - [x] ADRIAN When failing to connect, try again after a few seconds. Do this 3 times. If it fails, show an error message to the user.
    - [x] ADRIAN If the connection is lost, try to reconnect. 
    - [x] ADRIAN Buffer data if the connection is lost. When the connection is reestablished, send the buffered data.


- [x] TOBIAS. 2. Data encryption. You can think of different methods of integrating security into your solution,
either using public-key cryptography or other methods.
- [x] TOBIAS Convert symmetric encryption to RSA.

- [ ] SEBASTIAN 3. Automated generation of unique identifiers (addresses) for sensor nodes. By default, the programmer can assign static addresses to sensor nodes when running them (as a command-line argument). But you can design automated-address assignments as part of your protocol. For example, look at DHCP as an inspiration.

- [x] 4. SEBASTIAN/ADRIAN Images/files as sensor data. 

- [x] 5. ADRIAN Support of more flexible actuator commands. By default, it is expected that a command is sent to a specific sensor node, specific actuator. If you manage to support also either broadcast commands (to all sensor nodes at a time), or multicast (to specific groups of sensor nodes), this is considered an extra.

# VIDEO

- [X] KNUT 1. Introduction of the application: what problem does your solution solve? (1min)
- [ ] SEBASTIAN 2. The information, materials, approach you used, and the research you did. (1min)
- [X] KNUT 3. The work process: how did you organize work throughout the semester? Role of each team
member? How did you work with the sprints? Were there any general themes for the sprints, phases of the project? (1min)
- [ ] DANIEL 4. The architecture of your solution. What nodes are communicating? What is the responsibility
of each? Preferably, include model’s diagrams here. (1-2min)
- [x] TOBIAS OG DANIEL 5. Your communication protocol. Summarize it in a clear yet concise way. (3-4min)

- [ ] ALLE TODO 6. Your solution and result. Explain what is working. Show a demo of the system. (2-4min)


1. run intermidiary server
2. run greenhouse
2. run control panel

- Show that if greenhouse and/or control panel is started before the intermediary server, that they try to reconnect to the server, and then run the server to show they are able to connect whilst trying to reconnect.
- Run multiple control panels. (SHow that if yoy do something in one control panel, it is reflected in the other control panel and geenhosue)
- Press buttons in greenhouse and show that it is reflected in control panel.
- show turn on and turn off all actuators for a node.
- when you close a contorl panel and open it again, it works.

- [ ] 7. SEBASTIAN / TOBIAS Explain what extra work you have done (if any) for this to be considered an excellent project. (1-3min)


## Resilience in case of network outages.

If the client fails to connect to the server, or loses connection to the server, the client will try to re-establish the connection multiple times, each time waiting longer and logner to try and reconnect. If this fails too many times, the client will stop trying to connect and stop. If the client loses connection to the server and then tries to send messages, it will buffer the messages until the connection is re-established and will then send all the buffered messages when reconnected.


## Support of more flexible actuator commands.

In addition to being able to send an actuator state change command to a specific actuator on a specific node, We are able to send a command to turn on all or turn off all actuators for a specific node.

Also, we have a protocol for sending a message to all connected nodes, by specifying a broadcast parameaer in our message. Which means when a broadcast message is sent from a client, to the server, the server will then broadcast the received message to all target clients.

- [x] ADRIAN 8. Reflect on potential improvements and future work. (1 min)
- Currently the messages we send are separated into different parts, like header and body, and each of these parts are further subdivided, we do this by using different delimiters like "-" ";" and such. A better solution which we could call future work is to instead use a well established protocol like JSON or XML. This would make it easier to read the messages, and also easier to parse them. We already have experience with both JSON and XML which means it would not have been a big problem to hypthetically implement one of these protocols in the future.

- Additionally, we are somewhat lacking with catching and managing errors. We have an enum for different Failure reasons, but we don't use many of them and don't have many of them either. Currently we have an enum for if the server is not running, if the server failed to identify a client, and if we have integrity To help with implement better error handling, we would need more and better tests and utilize them further than what we already have.


Note: it is not a big problem if your video is 17 or 18 minutes, but don’t make it to 35 minutes!
Ability to present your ideas and results concisely is a general skill you must master. While the
video creation can be as simple as recording your screen during a Discord call, for better results it is
suggested that you plan your presentation beforehand, try how you would explain the concepts, how
much time that would take, etc. In other terms - prepare for the video presentation in the same way
as you would prepare for a physical presentation in the class.