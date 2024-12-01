# **Sprint Report**

Sprint master: Daniel  
Sprint stenographer: Tobias

## **Sprint Overview**
- **Sprint Number:** Sprint 1
- **Duration:** 30.09.2024 - 14.10.2024
- **Sprint Goal:** Enhance functionality for sensor node creation and communication between components, and establish foundational classes for encryption and messaging.
- **Team Members:**
  - Tobias Olsen Reiakvam - TobyJavascript
  - Adrian Gjøsund Bjørge - Osterie / Adrian Gjøsund Bjørge
  - Daniel Selbervik - Daniel
  - Sebastian Olsen - Sebastian Olsen
  - Knut Olav Leknes - Knut Olav

---

## **Sprint Summary**
- **Total Planned Work:**
  - Establish test directories for future testing classes.
  - Refactor Sensor classes to improve extensibility (support for new sensor types).
  - Enhance communication between greenhouse simulator, proxy server, and control panel.
  - Implement encryption and hashing functionality.
  - Build base classes for commands and messages.

- **Completed Work:**
  - Test directories created.
  - Sensor and SensorReading classes refactored.
  - Initial communication established between GreenhouseSimulator and ProxyServer.
  - AES encryption and hashing classes implemented.
  - Base classes for commands and messages created.

---

## **Sprint Progress**
| Story/Task                                | Assignee              | Status       | Story Points | Comments                                        |
|-------------------------------------------|-----------------------|--------------|--------------|-------------------------------------------------|
| Add directory for test classes            | Tobias Olsen Reiakvam | Completed    | [2]          | Prepared for unit test integration.             |
| Create sensor nodes in control panel      | Adrian Gjøsund Bjørge | Completed    | [5]          | Enables dynamic addition of sensor nodes.       |
| Refactor Sensor to support PictureReading | Sebastian Olsen       | Completed    | [3]          | Added extensibility for advanced sensors.       |
| Change SensorReading class to Abstract    | Sebastian Olsen       | Completed    | [2]          | Improved design for future subclasses.          |
| Add .gitignore                            | Adrian Gjøsund Bjørge | Completed    | [1]          | Simplifies repository management.               |
| Communication between Greenhouse & Proxy  | Adrian Gjøsund Bjørge | Completed    | [5]          | Facilitates inter-component data transfer.      |
| Add AES encryption class                  | Tobias Olsen Reiakvam | Completed    | [3]          | Added tool for secure communication capability. |
| Add hashing class                         | Tobias Olsen Reiakvam | Completed    | [2]          | Provides tool for data integrity features.      |
| Preparations for greenhouse-control comm  | Adrian Gjøsund Bjørge | In Progress  | [3]          | Initial groundwork for seamless integration.    |
| Base classes for commands and messages    | Tobias Olsen Reiakvam | Completed    | [4]          | Established a foundation for future tasks.      |

---

## **Challenges/Blockers**
- **Challenge 1:** Lack of clarity on communication protocols between greenhouse and control panel.
  - **Impact:** Slowed progress on full implementation of this feature.
  - **Proposed Solution:** Schedule a design meeting to finalize the communication model.

---

## **Risks & Dependencies**
- **Risk 1:** Dependencies between encryption, hashing, and communication modules.
  - **Impact:** Potential delays in end-to-end testing.
- **Dependency 1:** Timely completion of communication preparations for smoother task handoff.

---

## **Retrospective Summary**
- **What Went Well:**
  - Significant progress on foundational features like encryption, hashing, and sensor node creation.
  - Successfully established inter-component communication.

- **What Could Be Improved:**
  - Earlier clarification of complex requirements to prevent bottlenecks.
  - Faster feedback cycles during task implementation.

- **Actions for Next Sprint:**
  - Finalize communication protocols.
  - Conduct end-to-end testing of implemented features.

---

## **Next Steps**
- **Upcoming Goals:**
  - Complete communication functionality between greenhouse and control panels.
  - Expand testing coverage using newly created test directories.
  - Integrate AES encryption and hashing into the communication pipeline.
  - Create a comprehensive TODO list and distribute tasks among team members.
  - Implement Message and Command interfaces/classes.
  - Create different Message/Command subclasses.
  - Send messages between intermediary server and clients.
---