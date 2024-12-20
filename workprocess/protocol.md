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

---

## The underlying transport protocol

TODO - what transport-layer protocol do you use? TCP? UDP? What port number(s)? Why did you choose this transport layer protocol?
- TCP 
- Port number: 50500, if already in use, tries a different port number. Increasing by 20 every time if it fails to connect to given port number.
- We chose TCP because it is a connection-oriented protocol, which means that it guarantees the delivery of packets to the destination node. This is important for our system because we want to make sure that all messages are delivered to the destination node.



## The architecture
![ArchitectureOgApplication](architecture.png)
**Clients**
Clients are the nodes that initiate communication with the server to send requests and receive responses.

**Types of clients in the system:**

1. **Greenhouse nodes:** These are sensor and actuator nodes that send sensor data to the server and receive commands from the server to control the actuators.

2. **Control panels:** These are clients that ask for/recieve sensor data form the server and send commands to the server to control the actuators.


**Server**
The central entity managing client connections and routing messages. It is responsible for receiving sensor data from greenhouse nodes, sending commands to greenhouse nodes, and relaying sensor data to control panels (When it recieves messages it sends the message where the message want to be sent ish.). It is represented by the `IntermediaryServer` class, which uses `ClientHandler` to manage individual client connections.

---

## The flow of information and events

### Intermediary server

The different nodes in the system communicate with each other through the intermediary server. The intermediary server is responsible for managing client connections and routing messages between clients. It uses the `ClientHandler` class to manage individual client connections. When a node, control panel or greenhouse, first conenct to the intermediary server, they send an identification message so that the server can keep track of the nodes. The server then uses this information to route messages between the nodes.

### Control panel

Control panels connect to the intermediary server, which routes the control panel's commands to the greenhouse nodes. The control panel can send commands to the greenhouse nodes to control the actuators. The control panel can also request sensor data from the greenhouse nodes, which the intermediary server retrieves and sends back to the control panel.

The control panel can pull information from the greenhouse nodes at any time by sending commands, which are routed from the server to the correct greenhouse nodes.

In addition to pulling information at will, the control panel periodically sends a command requesting sensor data from the greenhouse node(s).

When the control panel receives a response from its sent command, it handles it differently depending on the command type. For example, if the command was to get sensor data, the control panel would notify listeners about the new sensor data.

When the user interacts with the control panel GUI, the control panel sends commands to the intermediary server, which routes them to the appropriate greenhouse nodes.

### Greenhouse node

Greenhouse nodes connect to the intermediary server, which routes messages between the greenhouse nodes and control panels. The greenhouse nodes send sensor data to the intermediary server when data is requested, which forwards it to the control panels. When the greenhouse node receives a command, it executes the command and sends a response back to the intermediary server, which forwards it to the control panel.

The greenhouse cannot push information.

---

## Connection and state

The protocol used is connection-oriented, as it uses TCP, which establishes a connection between the client and the server before sending data.

The protocol is stateful, as the server maintains the state of the nodes connected to it throughout the session.
The state includes information such as Node ID, ClientTypes, and the connection status.

## Types, constants
Client constants:
- CONTROL_PANEL : Represents the control panel client, established with the value `CONTROL_PANEL`.
- GREENHOUSE : Represents the greenhouse client, established with the value `GREENHOUSE`.
- SERVER : Represents the intermediary server, established with the value `SERVER`.

- BROADCAST : Represents a broadcast message to all clients established with the value `BROADCAST`.
- NOT_PREDEFINED : Represents a specific client ID, established with the value `?`.
- NONE : Represents no client ID, established with the value `NONE`.

Delimiters;
- HEADER_BODY : Delimiter between the header and body of a message, established with the value `-`.
- HEADER_FIELD : Delimiter between fields in the header of a message, established with the value `;`.
- BODY_FIELD : Delimiter between fields in the body of a message, defaulting to the header delimiter.
- BODY_FIELD_PARAMETERS : Delimiter between a fields parameters in the body of a message, established 
with the value `,`.
- BODY_SENSOR_SEPARATOR : Delimiter between sensor data in the body of a message, established with the value `¤`.

---

## Message Format
All messages consist of the following parts:

### **Header**
- `DST`: Destination (e.g., `GREENHOUSE` or `CONTROL_PANEL`)
- `DST_ID`: The ID of the destination (specific node or broadcast ID)
- `DATA_TYPE`: Specifies the type of message (e.g., `COMMAND`, `RESPONSE`)

### **Body**
- Contains the transmission. A transmission can be either a response or a command. A response is a reply to a command, while a command is an instruction to execute an action. The response will contain some data for the executed command, and information about the success or failure of the command. Additionally the response will contain information about what command was executed.

- Transmission:
    - `COMMAND`: A command to execute an action.
      - `GREENHOUSE_COMMAND`: A command for a greenhouse node.
    - `RESPONSE`: A response to a command.
      - `SUCCESS`: The command was executed successfully.
      - `FAILURE`: The command failed to execute.

---

### Message Types

#### **1. Command Messages**
- Sent from `CONTROL_PANEL` to `GREENHOUSE`.
- Examples:
    - `GET_NODE_ID`: Request node ID from a greenhouse node.
    - `ACTUATOR_CHANGE`: Change the state of an actuator.

#### **2. Sensor Messages**
- Sent from `GREENHOUSE` to `CONTROL_PANEL`.
- Examples:
    - Status updates for sensors.
    - Actuator state confirmations.

#### **3. Indetification request**
- Sent from `GREENHOUSE` and `GREENHOUSE` to `SERVER`

### Message formats and command types
TODO - describe the general format of all messages. Then describe specific format for each
message type in your protocol.

#### Commands

After a command is executed, the method which does this will return a message, and instead of the body of the message containing a command, it will contain a response which will be handled when the message is sent back to whoever sent it firsst. The response will be either a SuccessResponse or a FailureResponse.

All success responses look like this:
- SUCCESS,COMMAND_TYPE,RESPONSE_DATA

All failure responses look like this:
- FAILURE,COMMAND_TYPE,FAILURE_REASON

For the commands below we will also mention the potential success responses and failure responses, but will only write what the response data might be or the failure reasaon, since the general format of the responses are already mentioned above.


Where:
- **DST**: Destination (`CONTROL_PANEL` or `GREENHOUSE`).
- **DST_ID**: Specific ID of the destination (e.g., `Node123`) or `ALL` for broadcast.
- **DATA_TYPE**: Type of the message (e.g., `COMMAND`, `RESPONSE`).
- **COMMAND**: The command type.
- **PARAMS**: Command-specific parameters, separated by commas if multiple.

##### **1. ActuatorChangeCommand**
**Purpose**: Change the state of a specific actuator.

**Format**: DST;DST_ID;HASH-ACTUATOR_CHANGE,ACTUATOR_ID,STATE

**Success Response**: SUCCESS,ACTUATOR_CHANGE,NODE_ID;ACTUATOR_ID;STATE

NODE_ID = id of node
ACTUATOR_ID = actual id of actuator
STATE = 1 or 0

##### **2. GetNodeCommand**
**Purpose**: Retrieve information about a specific node

**Format**: DST;DST_ID;HASH-GET_NODE

**Success Response**: SUCCESS,GET_NODE,NODE_ID;ACTUATOR_TYPE1¤ACTUATOR_ID1¤ACTUATOR_TURN_ON_TEXT1¤ACTUATOR_TURN_OFF_TEXT1¤ACTUATOR_STATE1,Actuator2,3,4 and so on.

ACTUATOR_TYPE1 = type of actuator
ACTUATOR_ID1 = id of actuator
ACTUATOR_TURN_ON_TEXT1 = text to display when actuator is turned on
ACTUATOR_TURN_OFF_TEXT1 = text to display when actuator is turned off


##### **3. GetNodeIdCommand**
**Purpose**: Retrieve the ID of a specific node.

**Format**: DST;DST_ID;HASH-GET_NODE_ID

**Success Response**: SUCCESS,GET_NODE_ID,NODE_ID;ACTUATOR_ID;NODE_ID

NODE_ID = id of node

##### **4. GetSensorDataCommand**
**Purpose**: Retrieve data from a specific sensor.

**Format**: DST;DST_ID;HASH-GET_SENSOR_DATA

**Success Response**: SUCCESS,GET_SENSOR_DATA,ACTUATOR_ID;(sensor data, a bit differently formatted based on sensor data type and such)


##### **5. TurnOffAllActuatorInNodeCommand**
**Purpose**: Turn off all actuators in a specific node.

**Format**: DST;DST_ID;HASH-TURN_OFF_ALL_ACTUATORS

**Success Response**: SUCCESS,TURN_OFF_ALL_ACTUATORS,TURN_OFF_ALL_ACTUATORS_SUCCESS

##### **6. TurnOnAllActuatorInNodeCommand**
**Purpose**: Turn on all actuators in a specific node.

**Format**: DST;DST_ID;HASH-TURN_ON_ALL_ACTUATORS

**Success Response**: SUCCESS,TURN_ON_ALL_ACTUATORS,TURN_ON_ALL_ACTUATORS_SUCCESS

---

#### Responses

Where:
- **DST**: Destination (`CONTROL_PANEL` or `GREENHOUSE`).
- **DST_ID**: Specific ID of the destination (e.g., `Node123`) or `ALL` for broadcast.
- **DATA_TYPE**: Type of the original message (e.g., `COMMAND`).
- **COMMAND**: The command that triggered the response.
- **RESPONSE_TYPE**: Indicates whether the response is `SUCCESS` or `FAILURE`.
- **RESPONSE_DATA**: Additional details about the success or failure.

##### **1. SuccessResponse**
**Purpose**: Indicates that the transmission was executed successfully.

**Format**: DST;DST_ID;DATA_TYPE;COMMAND;SUCCESS;RESPONSE_DATA

**Response**: CONTROL_PANEL;Node123;COMMAND;GET_NODE_ID;SUCCESS;Node123

##### **2. FailureResponse**
**Purpose**: Indicates that the transmission failed.

**Format**: DST;DST_ID;DATA_TYPE;COMMAND;FAILURE;FAILURE_REASON

**Failure Reasons**:
- `SERVER_NOT_RUNNING`: The server is not operational.
- `FAILED_TO_IDENTIFY_CLIENT`: The client could not be identified.
- `INTEGRITY_ERROR`: There was an error in message integrity.

**Response**: CONTROL_PANEL;Node123;COMMAND;ACTUATOR_CHANGE;FAILURE;INTEGRITY_ERROR

---

### Message Flow

#### **Control Panel**
- Sends commands like `GET_NODE_ID` or `ACTUATOR_CHANGE` to specific nodes or broadcasts to all nodes.

#### **Greenhouse Node**
- Receives commands, processes them, and optionally sends back responses (e.g., node ID or execution status).

---

### Marshalling in Message Formatting

In this Java application, **marshalling** refers to the process of converting data into a specific format for communication between components (such as the `Control Panel` and `Greenhouse`). This process ensures that data can be serialized into messages with proper delimiters, allowing the message to be easily parsed, transmitted, and interpreted on the receiving end.

#### Delimiters in Marshalling

The `Delimiters` enum in the `no.ntnu.messages` package defines various separators that are crucial for the formatting and parsing of messages. These delimiters ensure that different parts of a message (e.g., header, body, and parameters) are properly separated.

##### **Delimiters for Marshalling**

The following delimiters are defined:

1. **`HEADER_BODY`** (`"-"`)
    - Used to separate the **header** from the **body** of a message.
    - Example: `HEADER-BODY`

2. **`HEADER_FIELD`** (`";"`)
    - Used to separate fields in the **header** of the message.
    - Example: `DST;DST_ID;DATA_TYPE`

3. **`BODY_FIELD`** (`HEADER_FIELD.getValue()`)
    - Default delimiter between fields in the **body** of a message, which is the same as the `HEADER_FIELD` delimiter.
    - Example: `ACTUATOR_CHANGE;ON`

4. **`BODY_FIELD_PARAMETERS`** (`","`)
    - Used to separate **parameters** in the **body** of a message. This delimiter is used specifically when a command or sensor message requires multiple parameters.
    - Example: `1,ON`

5. **`BODY_SENSOR_SEPARATOR`** (`"¤"`)
    - Used to separate **sensor data** in the body of a message.
    - Example: `Temperature¤Humidity¤Soil Moisture`

---

## Errors and Handling
### **Examples of Errors**
1. **Invalid Message Format**:
    - Return a failure response indicating the reason.
2. **Unknown Command**:
    - Ignore the message or log an error.
3. **Client Not Found**:
    - Notify the sender or log the issue.

---

### Error messages

- **SERVER_NOT_RUNNING**: 
- Indicates that the server is not running.


- **Unknown Command**:
- Indicates that the client has received an unknown command.


- **INTEGRITY_ERROR**:
- Indicates that the message integrity check failed.

---

## An example scenario

TODO - describe a typical scenario. How would it look like from communication perspective? When 
are connections established? Which packets are sent? How do nodes react on the packets? An 
example scenario could be as follows:

1. The server gets started, and the server listens for clients.
2. Greenhouse nodes start up with one node with a temperature sensor and a heater actuator.
3. The greenhouse establishes a connection with the server, the greenhouse connects to the port number
4. The greenhouse sends an identification message to the server.
5. Server receives identification message and stores the node in a list of nodes.
6. Control panel starts up and connects to the server.
7. The control panel sends an identification message to the server.
8. Server receives identification message and stores the node in a list of nodes.
9. Control panel sends a message that holds a header with the destination as the client type, which is greenhouse, and with broadcast as the id. Broadcast is a special value witch the server will interpret. While the body holds a command to get sensorId.
10. Server receives message.
11. Server checks the client type and client id in the header, which it used to fetch a stored connection to the greenhouse node. In this case since the id is broadcast, the message will instead be sent to all clients of the given type.
12. Server switches the header client type and client id to instead be the client type and client id client who sent the message to the server, which is control panel.
13. Server sends the message to the greenhouse node.
14. Greenhouse node receives message and parses it, executes the getSensorID.
15. Greenhouse fetches the sensor of with the corresponding id and puts it in a response. 
16. Greenhouse sends the response to the server.
17. Server receives the response, checks header again and does the same as mentioned previously.
18. Server sends the message to the control panel.
19. Control panel receives the response and parses the response.
20. Control panel notifies listeners about the new sensor data.
21. Since the control panel gui is a listener, it is notified and the sensor data is added to the GUI.


## Reliability and security

### Security:

- For our application we wanted to implement RSA encryption, but ended up not using it because of bugs and tight schedule. But the tools for encryption is made.

- Justification for RSA: RSA is a well-established cryptographic algorithm offering strong security
for encrypting sensitive data or securely exchanging keys. Its public/private key mechanism
ensures that only authorized users with the private key can decrypt the data.

- Justification for asymmetric encryption: Unlike symmetric encryption, RSA does not require both
parties to share a secret key beforehand, which simplifies key distribution and enhances
security for scenarios where secure communication is required over untrusted networks.

Note: This class uses 2048-bit RSA keys, providing robust security. For even greater protection,
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