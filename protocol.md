# Communication protocol

This document describes the protocol used for communication between the different nodes of the
distributed application.

## **Terminology**

* **Greenhouse** - The simulated environment representing the project context, including sensors and actuators.
* **Sensor** - A device which senses the environment and describes it with a value (an integer value in the context of this project). Examples: temperature sensor, humidity sensor.
* **Actuator** - A device which can influence the environment. Examples: a fan, a window opener/closer, door opener/closer, heater.
* **Sensor and actuator node** - A computer which has direct access to a set of sensors, a set of actuators, and is connected to the Internet.
* **Control-panel node** - A device connected to the Internet which visualizes the status of sensor and actuator nodes and sends control commands to them.
* **Graphical User Interface (GUI)** - A graphical interface where users of the system can interact with it.
* **Intermediary for managing commands** - A software layer responsible for handling and routing commands between nodes.
* **AES encryption (symmetric)** - A symmetric encryption method where the same key is used for both encryption and decryption.
* **RSA encryption (asymmetric)** - An encryption method where a public key is used for encryption and a private key for decryption.
* **TCP** - Transmission Control Protocol, a communication standard enabling reliable data transfer over a network.


## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you choose this transport layer protocol?
- TCP 
- Port number: 50500
- We chose TCP because it is a connection-oriented protocol, which means that it guarantees the delivery of packets to the destination node. This is important for our system because we want to make sure that all messages are delivered to the destination node.



## The architecture
TODO - READ THROUGH THIS (MABYE I SHOULD BE MORE SPECIFIC?, ADD PICTURES OF THE ARCHITECTURE? UML DIAGRAMS?)

Note: show the general architecture of your network. Which part is a server? Who are clients? 
Do you have one or several servers? Perhaps include a picture here. 

**Clients**
Clients are the nodes that initiate communication with the server to send requests and receive responses.

**Types of clients in the system:**

1. **Greenhouse nodes:** These are sensor and actuator nodes that send sensor data to the server and receive commands from the server to control the actuators.

2. **Control panels:** These are GUIs that visualize the sensor data and send commands to the server to control the actuators.

**Server**
The central entity managing client connections and routing messages. It is responsible for receiving sensor data from greenhouse nodes, sending commands to greenhouse nodes, and relaying sensor data to control panels. It is represented by the `IntermediaryServer` class, which uses `ClientHandler` to manage individual client connections.


## The flow of information and events

TODO - describe what each network node does and when. Some periodic events? Some reaction on 
incoming packets? Perhaps split into several subsections, where each subsection describes one 
node type (For example: one subsection for sensor/actuator nodes, one for control panel nodes).

## Connection and state

TODO - is your communication protocol connection-oriented or connection-less? Is it stateful or 
stateless? 

## Types, constants

TODO - Do you have some specific value types you use in several messages? They you can describe 
them here.

## Message format

TODO - describe the general format of all messages. Then describe specific format for each 
message type in your protocol.

TODO
What we need
Header.
- Who is the receiver (GREENHOUSE or CONTROL_PANEL)
- ID of the receiver
- Data type

BODY
- Command


Result:
- DST;DST_ID;DATA_TYPE COMMAND
- GREENHOUSE;AllId;STRING GET_NODE_ID

### Error messages

TODO - describe the possible error messages that nodes can send in your system.

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:
1. A sensor node with ID=1 is started. It has a temperature sensor, two humidity sensors. It can
   also open a window.
2. A sensor node with ID=2 is started. It has a single temperature sensor and can control two fans
   and a heater.
3. A control panel node is started.
4. Another control panel node is started.
5. A sensor node with ID=3 is started. It has a two temperature sensors and no actuators.
6. After 5 seconds all three sensor/actuator nodes broadcast their sensor data.
7. The user of the first-control panel presses on the button "ON" for the first fan of
   sensor/actuator node with ID=2.
8. The user of the second control-panel node presses on the button "turn off all actuators".

## Reliability and security

### Security:

**Justification for RSA**: RSA is a well-established cryptographic algorithm offering strong security
for encrypting sensitive data or securely exchanging keys. Its public/private key mechanism
ensures that only authorized users with the private key can decrypt the data.

**Justification for asymmetric encryption**: Unlike symmetric encryption, RSA does not require both
parties to share a secret key beforehand, which simplifies key distribution and enhances
security for scenarios where secure communication is required over untrusted networks.

<p>Note: This class uses 2048-bit RSA keys, providing robust security. For even greater protection,
consider using 3072 or 4096-bit keys, depending on performance and security requirements.

### Reliability:
  - For reliability a hashing algorithm is being used.

  - Justification for hashing algorithm: Hashing algorithms like SHA-256 are used to ensure data integrity. The hash
  function generates a fixed-size output (hash) that uniquely represents the message. When
  transmitting messages over unreliable networks, hashes can verify the integrity of the
  message and ensure it has not been tampered with or altered. This also provides
  lightweight verification without adding computational overhead.

    - Message Integrity Check: By hashing the message on both the sender and receiver
  sides, we ensure that any corruption during transmission can be detected. If the
  received hash doesn't match the hash computed on the receiver's side, the message is
  considered tampered or corrupted, and the sender is notified to resend.