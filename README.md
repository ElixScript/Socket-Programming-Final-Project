# RealTime Chat System with Whisper, Block, and List Features 🌐💬

## Overview
The RealTime Chat System is a Java-based client-server chat application that enables multiple users to communicate in real-time. This project includes advanced features like private messaging (whisper), blocking specific users, and listing all online users.

## Features 🚀
1. **Real-Time Messaging**: Users can exchange messages in real-time within a shared chat room.
2. **Private Messaging**: Users can send direct messages to a specific user using the `/whisper` command. 
3. **Blocking Users**: Users can block specific users, preventing them from receiving messages from the blocked user. 
4. **Unblocking Users**: Blocked users can be unblocked using the `/unblock` command. 
5. **List Online Users**: Users can list all currently online users with the `/list` command. 
6. **Concurrent Client Support**: Supports multiple users connecting and chatting simultaneously. 

## Architecture 🏗️
The system is built using three main components:

1. **Server**
   - Handles incoming connections. ⚡
   - Manages connected clients and delegates communication tasks to `ClientHandler` threads.
   - Starts on a specified port (default: `1234`).

2. **ClientHandler**
   - Manages communication between the server and a specific client. 🔄
   - Implements features like message broadcasting, whisper, block, and list.

3. **Client**
   - Provides the user interface for clients to send and receive messages. 🎛️
   - Users can interact with the system by typing commands and messages into the console.

## How It Works 🛠️

### Server
1. Initializes a `ServerSocket` on a specified port. 📡
2. Listens for incoming connections and spawns a new `ClientHandler` thread for each client. 🌐
3. Broadcasts messages to all connected clients, excluding the sender.

### ClientHandler
1. Reads messages from the connected client. 📩
2. Processes commands:
   - `/whisper username message`: Sends a private message to the specified user. 🤝
   - `/block username`: Blocks the specified user. 🚷
   - `/unblock username`: Unblocks the specified user. 🔓
   - `/list`: Displays a list of online users. 📜
3. Handles broadcasting messages, ensuring blocked users are excluded. 🔇

### Client
1. Prompts the user for a username upon connection. 👤
2. Runs two concurrent threads:
   - One for sending messages. 📝
   - Another for listening to incoming messages. 👂
3. Allows users to enter commands and messages directly into the console.

### Running the Application
1. **Start the Server**
   ```bash
   java Server
   ```
   The server will start and listen for client connections on port `1234`. 🔌

2. **Start the Client**
   ```bash
   java Client
   ```
   - Enter a username when prompted. 
   - Use the following commands:
     - `/whisper username message` to send a private message. 
     - `/block username` to block a user. 
     - `/unblock username` to unblock a user. 🔓
     - `/list` to view all online users. 

### Example Commands
- Sending a message to everyone:
  ```
  Hello, everyone! 👋
  ```
- Sending a private message:
  ```
  /whisper John Hi John, how are you? 🤝
  ```
- Blocking a user:
  ```
  /block Jane 🚷
  ```
- Listing all users:
  ```
  /list 📜
  ```

## File Structure 📂
- **Server.java**: Implements the server logic and connection handling. 
- **ClientHandler.java**: Manages individual client connections and message processing.
- **Client.java**: Provides the user interface for clients to connect and chat. 

## Key Functionalities ✨
1. **Private Messaging**:
   - Allows sending direct messages without broadcasting. 
   - Ensures blocked users cannot send whispers. 

2. **Blocking**:
   - Prevents blocked users from sending messages to the blocker. 
   - Blocks persist during the session. 

3. **Online User List**:
   - Provides a list of currently connected users. 
   - Helps users identify whom they can interact with. 

## Enhancements 🔧
- Adding persistent storage for user settings (e.g., blocked users). 🗂️
- GUI implementation for better user interaction. 🖼️
- Encryption for secure messaging. 🔒

## Contributors 🤝
- Bagus Cipta Pratama
- Muhammad Akmal Fauzan
- Kosmas Rio Legowo
- Rafid Nur Huda
- Givari Akbar
