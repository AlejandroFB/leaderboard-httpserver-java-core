# Custom HTTP server based game leaderboard

Registers game scores for different users and levels, with the capability to return high score lists per level.
There is also a simple login system in place.

Main functionalities:
- Login based on session keys that expire after 10 minutes
- Post a user's score to a specific level
- Get high scores for a specific level

Technology details:
- Only core Java 8
- No servlet technology used
- Implemented custom HTTP server with hand made URL requests handlers and filters
- Global session service handling concurrent traffic
- Custom dependency injection logic
- JUnit and Mockito for unit tests
- Rest-assured for the server integration tests