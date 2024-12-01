
# WORK PROCESS DOCUMENTATION REQUIREMENTS
provide some form of documentation of the following:
- [X] KNUT 1. How the work was planned in terms of iterations (sprints)
- [x] ADRIAN 2. How the tasks were distributed - who was responsible for what. Note - it is NOT OK to have someone in the group responsible only for creating videos. This course is primarily about network protocols and network programming. Every group member must demonstrate expertise within these fields. Perhaps one student works more on the protocol, another on the server programming, and the third on client programming, that is fine. But it is not fine if one student does all the programming, one just writes documentation (sprint reports) and the third creates a video. How are the second and third students showing their computer network expertise?

## How the work was planned in terms of iterations (sprints)

When we received the assignment,our first step was to review both the assignment details and the provided template code. We discussed our vision for the application, identified tasks,explored additional features, and discussed what type of protocol we would use.Once we had a clear idea of what we wanted to achieve we planned our workflow.

We followed a scrum framework where we organized two-week long sprints. Each sprint began with a planning session,where we set goals, defined roles, outlined procedures, and addressed team dynamics following the MRPI (goals, roles, procedure, interpersonal questions) model. We maintained a TODO.md file to track project-related tasks, dividing and updating it as the project continued.

During the sprints we held regular stand-up meetings where we tackled challengers and blockers. At the end of each sprint, we conducted retrospectives to reflect on successes and identify areas for improvement.

## How the tasks were distributed
For programming, after having analyzed what the task required us to do, we wrote down these tasks as TODOs under our version control. These tasks were further subdivided into smaller tasks and distributed between the different members of the group. Whilst working on the project, we naturally wrote down more TODOs. To distribute the TODOs we talked to eachother about which tasks we wanted to complete, and if we completed our TODOs we would assign ourselves to new ones.  

For work process documentation, we were appointed different parts of the documentation to complete, with the work load slightly varying based on workload in other parts of the project. Finally for the video we were all assigned different parts to work on and create, but we also helped eachother with writing scripts. Everyone was assigned a part of the video, but again how much was spoken was also based on how much the person had already done. Still, everyone helped eachother with the video, and everyone had a part in creating the video.

## Who was responsible for what. 

When programming we did not assign people areas to work on. TODOs were not distributed necessarily based on what other TODOs a member worked on, which was to try to ensure that everyone leared about all parts of the project. Also for writing protocol, everyone were given the responsibility of writing some part of the protocol. 

For completing the work process documentation, everyone was assigned different tasks, which ensured that everyone contributed to all aspects of the project. For the final video everyone had some part to create for the video. 

## Retrospective after each sprint

Each sprint has a short retrospective at the end of the sprint.


- [ ] KNUT 3. How was the work broken down into issues? Remember the principles you learned about issue
definition in the System development course. Issues should be small, have descriptive names,
clear "definition of done", etc. For example, an issue like "improve GUI" is a bad issue. What
does it mean? When is it complete?
- [ ] DANIEL 4. How the work was progressing - what did you accomplish in each sprint? Which issues were
planned, and which ones were completed?
- [x] SEBASTIAN 5. A short retrospective after each sprint - what went well, and how can you improve?

It is suggested that you keep the sprint documentation in Git as well, but you are allowed to use
Wiki pages, Jira or other tools if you want.

# String rapports: 

# **Sprint Report 1**

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

# **Sprint Report 2**

Sprint master: Adrian
Sprint stenographer: Tobias

## **Sprint Overview**
- **Sprint Number:** Sprint 2
- **Duration:** 14.10.2024 - 28.10.2024
- **Sprint Goal:** Create a comprehensive TODO list and distribute tasks among team members, complete some TODOs
- **Team Members:**:
    - Tobias Olsen Reiakvam - TobyJavascript
    - Adrian Gjøsund Bjørge - Osterie / Adrian Gjøsund Bjørge
    - Daniel Selbervik - PizzaMachine
    - Sebastian Olsen - Sebastian Olsen
    - Knut Olav Leknes - Knut Olav

---

## **Sprint Summary**
- **Total Planned Work:**
    - Create a comprehensive TODO list and distribute tasks among team members.
    - Implement Message and Command interfaces/classes.
    - Create different Message/Command subclasses.
    - Send messages between intermediary server and clients.
    - Visualize charts.

- **Completed Work:**
    - Created a comprehensive TODO list and distributed tasks.
    - Enhanced functionality for the control panel to handle actuator states and sensor readings.
    - Added Base64 image encoder and corresponding test classes.
    - Added tests for cipher encryption.

- **Carried Over Work:**
    - Unfinished Story Points: [TBD]
    - Carried Over Stories/Tasks: Send messages between intermediary server and clients.

---

## **Sprint Progress**
| Task                                         | Assignee  | Status        |
|----------------------------------------------|-----------|---------------|
| Create TODOs                                 | Adrian    | Completed     |
| Add thread for listening in control panel    | Adrian    | Completed     |
| Add CSS to classes                           | Tobias    | Completed     |
| Enable control panel to accept readings      | Sebastian | Completed     |
| Allow control panel to change actuator state | Adrian    | Completed     |
| Retrieve actuators from greenhouse           | Adrian    | Completed     |
| Add Base64 image encoder                     | Tobias    | Completed     |
| Add test class for Base64 encoder            | Tobias    | Completed     |
| Reformat ControlPanelCommunicationChannel    | Adrian    | Completed     |
| Add tests for cipher encryption              | Tobias    | Completed     |
| Visualize charts for sensors                 | Knut      | not Completed |

---

## **Challenges/Blockers**
- **Challenge 1:** Limited clarity on sending messages between intermediary server and clients.
    - **Impact:** Delay in progress for messaging functionality.
    - **Proposed Solution:** Conduct a team meeting to discuss and break down the task into smaller, more actionable subtasks.

---

## **Risks & Dependencies**
- **Risk 1:** [Description and impact on project]
- **Dependency 1:** [External dependency affecting sprint completion]

---

## **Retrospective Summary**
- **What Went Well:**
    - Good collaboration between team members.
    - Successful implementation of new features and corresponding tests.
    - Effective communication during task distribution with new TODO list.

- **What Could Be Improved:**
    - Better documentation for ongoing development tasks to reduce knowledge gaps.
    - Earlier identification of blockers to minimize delays.
---

## **Next Steps**
- Focus on completing messaging functionality.
- Improve communication on cross-functional dependencies.

# **Sprint Report 3**

Sprint master: Tobias
Sprint stenographer: Knut Olav

## **Sprint Overview**
- **Sprint Number:** Sprint 3
- **Duration:** 28.10.2024 - 11.11.2024
- **Sprint Goal:** TODO
- **Team Members:**:
    - Tobias Olsen Reiakvam - TobyJavascript
    - Adrian Gjøsund Bjørge - Osterie / Adrian Gjøsund Bjørge
    - Daniel Selbervik - PizzaMachine
    - Sebastian Olsen - Sebastian Olsen
    - Knut Olav Leknes - Knut Olav

---

## **Sprint Summary 3**
- **Total Planned Work:**
    - Improve the control panel communication by updating the socket system.
    - add threading to control panel.
    - Create header/body for messages
    - refactor intermediary server
    - Tidy up work and write JavaDoc

- **Completed Work:**
    - Control panel socket updated for better communication.
    - Added threading to handle multiple requests in the control panel.
    - Created header/body structure for messages to simplify communication.
    - Intermediary server refactored and well-documented.
    - Built a tool to simplify GUI creation using JavaFX.
    - Refactored sensor classes to make it easier to add new sensor types.
    - Enhanced the structure of commands and messages for better functionality.

- **Carried Over Work:**
    - Write more JavaDoc.
    - Some error handling and testing for commands and messages.

---

## **Sprint Progress**
| Task                                                          | Assignee  | Status      |
|---------------------------------------------------------------|-----------|-------------|
| Create a component builder Class                              | Knut      | Completed   |
| Refactoring of control panel socket                           | Adrian    | Completed   |
| Added thread for listening to request in control panel socket | Adrian    | Completed   |
| Fixed method calls                                            | Adrian    | Completed   |
| implemented of header/body in clienthandler                   | Adrian    | Completed   |
| Refactor intermediary server and classes                      | Daniel    | Completed   |
| Refactor sensor classes                                       | Sebastian | Completed   |
| Add tests for message and intermediary server                 | Daniel    | Completed   |
| Added actuator state updates in control panel                 | Adrian    | Completed   |
| Refactor command structure                                    | Tobias    | Completed   |
| JavaDoc                                                       | Everyone  | In progress |
---

## **Challenges/Blockers**
- **Challenge 1:**
    - **Impact:**
    - **Proposed Solution:**
- **Challenge 2:** [Description, impact, and proposed solution or action]

---

## **Risks & Dependencies**
- **Risk 1:**
    - **Impact:**
- **Dependency 1:** [External dependency affecting sprint completion]

---

## **Retrospective Summary**
- **What Went Well:**
    - The team did well managing the workload and completing the planned tasks.
    - Refactoring made it easier work on the project.
    - Collaboration especially during challenging parts were effective.
- **What Could Be Improved:**
    - Identifying performance bottlenecks earlier to avoid wasting time.
- **Actions for Next Sprint:**
    - Improve documentation to make the system easier for each other to use.
    - Add more tests and improve error handling.

---

## **Next Steps**
- **Upcoming Goals:** [Brief summary of objectives for the next sprint]
- **Key Focus Areas:** [Specific areas the team will prioritize, e.g., technical debt, improving speed]
- Add javadoc to 90% of classes
- Add public encryption and implement it
- Add more tests
- Implement necessary commands and messages
- Implement periodical data requests
- Refactor intermediary server
- Add more error handling
- Add more control panel functionality 

# **Sprint Report 4**

Sprint master: Sebastian
Sprint stenographer: Tobias

## **Sprint Overview**
- **Sprint Number:** Sprint 4
- **Duration:** 11.11.2024 - 25.11.2024
- **Sprint Goal:** Get the most important specifications done.
- **Team Members:**:
    - Tobias Olsen Reiakvam - TobyJavascript
    - Adrian Gjøsund Bjørge - Osterie / Adrian Gjøsund Bjørge
    - Daniel Selbervik - PizzaMachine
    - Sebastian Olsen - Sebastian Olsen
    - Knut Olav Leknes - Knut Olav

---

## **Sprint Summary**
- **Total Planned Work:**
    - Add javadoc to 90% of classes
    - Add public encryption and implement it
    - Add more tests
    - Implement necessary commands and messages
    - Implement periodical data requests
    - Refactor intermediary server
    - Add more error handling
    - Add more control panel functionality

- **Completed Work:**
    - Added javadoc to 90%
    - Added public encryption tool
    - Added tests for intermediary server
    - Added tests for nodes and sensors
    - Fixed periodical requests

- **Carried Over Work:**
    - Implement encryption classes

---

## **Sprint Progress**
| Task                                             | Assignee  | Status      |
|--------------------------------------------------|-----------|-------------|
| Create public encryption tools                   | Tobias    | Completed   |
| Implement encryption tools                       | Tobias    | Uncompleted |
| Added tests for intermediary server              | Daniel    | Completed   |
| Implement necessary commands and messages        | Adrian    | Completed   |
| Added periodical data requests for control panel | Adrian    | Completed   |
| Refactor intermediary server                     | Adrian    | Completed   |
| Add error handling for intermediary server       | Knut      | Completed   |
| Add response commands                            | Daniel    | Completed   |
| Added refresh button                             | Tobias    | Completed   |
| Solve problem with port addresses                | Sebastian | Completed   |
| Fixed performance issue                          | Adrian    | Completed   |
| Fixed wrong javadoc                              | Adrian    | Completed   |
| Implement multiple control panels                | Adrian    | Completed   |
| Add tests for nodes                              | Knut      | Completed   |
| Added ability to read images with control panel  | Sebastian | Completed   |
| Added ribbon in gui                              | Tobias    | Completed   |

---

## **Retrospective Summary**
- **What Went Well:**
    - Most of the TODOs was implemented.
    - No large merge conflicts halted progress.

- **What Could Be Improved:**
- TODOs should have been distributed more fairly.

---

## **Next Steps**
- Implement encryption
- Last sprint for finishing product
- Make presentation video
- Finish protocol

# **Sprint Report 5**

Sprint master: Daniel
Sprint stenographer: Tobias

## **Sprint Overview**
- **Sprint Number:** Sprint 5 - Final sprint
- **Duration:** 25.11.2024 - 1.12.2024
- **Sprint Goal:** Finish all TODOs so product is ready for shipping.
- **Team Members:**
    - Tobias Olsen Reiakvam - TobyJavascript
    - Adrian Gjøsund Bjørge - Osterie / Adrian Gjøsund Bjørge
    - Daniel Selbervik - Daniel
    - Sebastian Olsen - Sebastian Olsen
    - Knut Olav Leknes - Knut Olav

---

## **Sprint Summary**
- **Total Planned Work:**
    - Finish all basic TODOs.
    - Finish the extra TODOs if we can.
    - Refactor all code.
    - Write javadoc on all code.
    - Check google check style on all code.

- **Completed Work:**
    - Finished the basic TODOs.
    - Finished almost all extra TODOs.
    - Refactored almost all the code
    - All classes have javadoc.
    - All classes follow google checkstyle.

---

## **Sprint Progress**
| Task                                  | Assignee  | Status       |
|---------------------------------------|-----------|--------------|
| Everyone worked on every story point. | Adrian    | Completed    |
| Everyone worked on every story point. | Tobias    | Completed    |
| Everyone worked on every story point. | Knut      | Completed    |
| Everyone worked on every story point. | Sebastian | Completed    |
| Everyone worked on every story point. | Daniel    | Completed    |

---

## **Challenges/Blockers**
- **Challenge 1:** Too much work for the last week.
    - **Impact:** All members worked many hours per day, and other work got put to the side.
    - **Proposed Solution:** We should have planned better and started earlier.

---

## **Risks & Dependencies**
- **Risk 1:** (Same as last sprint) Dependencies between encryption, hashing, and communication modules.
    - **Impact:** Potential delays in end-to-end testing.
- **Dependency 1:** Timely completion of communication preparations for smoother task handoff.

---

## **Retrospective Summary**
- **What Went Well:**
    - Our application has a lot of functionality both basic and extra.
    - Team members worked like a well oiled machine last days.

- **What Could Be Improved:**
    - Better planning earlier.


- **Actions for Next Sprint:**
    - No more sprints. 




