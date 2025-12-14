# Report
## Tech Stack

- **TCP/UDP**:  
  TCP ensures reliable communication, vital for chat functionality.
  UDP is used for fast, connectionless messaging in contact discovery.

- **SQLite**:  
  Lightweight, embedded database for storing user data and message history.

- **Swing**:  
  Chosen for its cross-platform UI components, providing a smooth user experience.

- **Log4j2**:
  Used for easy configuration and efficient logging.

## Testing Policy

The testing campaign involved extensive **unit tests** for core components and **integration tests** to ensure smooth interaction between the database, UI, and networking.
Tests focus on edge cases like concurrency and network errors.

## Highlights

- **Code Structure**:  
  `MainController` cleanly separates business logic and UI, ensuring modularity.

- **Database**:  
  `DatabaseManager` ensures data consistency and handles errors properly.

- **Thread Safety**:  
  Synchronized access to shared resources prevents concurrency issues.

- **UI**:  
  `View` ensures a responsive interface.
 
- **cs_protocol**:
  All exchanges between machines and messages sent via broadcast always start with `cs_`. Examples :
    - To share the nickname "yvonne": The UDP message `cs_nickname=yvonne` is sent via broadcast.
    - To send a chat message "hello": The message `cs_msg=hello` is sent.

> This protocol ensures that no messages from other systems interfere.  
> It could be expanded to support additional features and adopted by other developers as a standard for building compatible applications.

- **Extras**:
    - **Avatars**: `AvatarLoader` loads images asynchronously without blocking the UI.
    - **AudioPlayer**: `AudioPlayer`efficiently manages audio playback with error handling and resource management.
