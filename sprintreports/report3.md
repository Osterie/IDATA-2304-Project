# **Sprint Report**

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

## **Sprint Summary**
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
