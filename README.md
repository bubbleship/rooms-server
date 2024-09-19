# rooms-server

## Real-Time Chat and Activities Application Server for the Rooms Platform

This is the server component of the Rooms platform built using Java and Spring Boot.
For the client component see [rooms-client](https://github.com/RoyalGucci/rooms-client).

### Features

- Real-time messaging
- User authentication
- Chat rooms
- Private messaging
- Message history
- Activities in the form of games

### Prerequisites

- Java 21

### How to Run

1. **Ensure Java version is 21:**
    ```shell
    java -version
   ```
2. **Clone the repository:**
    ```shell
    git clone https://github.com/bubbleship/rooms-server.git
    cd rooms-server
    ```
3. **Start the server:**
    ```shell
    ./gradlew bootRun
    ```