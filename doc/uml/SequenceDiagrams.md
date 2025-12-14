# Diagramme de séquence 1 : Inscription et découverte des utilisateurs actifs

```mermaid
sequenceDiagram
participant Utilisateur
participant LoginPanel
participant ContactController
participant ActiveUserList
participant MainController
participant DatabaseManager

Utilisateur->>+LoginPanel: Entrer le nickname
LoginPanel->>+ContactController: Appelle `inscription(userNickname)`
ContactController->>+ActiveUserList: Appelle `addNewUsers(socket, 4000)`
loop Découverte des utilisateurs actifs
    ActiveUserList->>+ActiveUserList: Ajoute les utilisateurs existants à `activeUsers`
    ActiveUserList->>+DatabaseManager: Met à jour la base de données avec `addOrUpdateUser`
    deactivate ActiveUserList
    deactivate DatabaseManager
end
deactivate ActiveUserList
ContactController->>+ActiveUserList: Appelle `nicknameUsed(userNickname)`
alt Nickname déjà pris
    ContactController-->>+LoginPanel: Lève `NicknameAlreadyUsedException`
    deactivate ContactController
    LoginPanel-->>Utilisateur: Message d'erreur (nickname indisponible)
    deactivate LoginPanel
else Nickname disponible
    ContactController->>+ActiveUserList: Ajoute le nickname via `addUser(sender)`
    ActiveUserList->>+DatabaseManager: Appelle `addOrUpdateUser` pour enregistrer
    deactivate ActiveUserList
    deactivate DatabaseManager
    ContactController->>+ContactController: Appelle `sendNickname()`
    deactivate ContactController
    ContactController->>+MainController: Enregistre la session
    MainController-->>Utilisateur: Message de connexion réussie
    deactivate MainController
end
Utilisateur->>+ContactController: Demande la liste des utilisateurs actifs
ContactController->>+ActiveUserList: Appelle `getAllContacts()`
ActiveUserList-->>Utilisateur: Retourne la liste des utilisateurs
deactivate ContactController
deactivate ActiveUserList
```

# Diagramme de séquence 2 : Changement de nickname

```mermaid
sequenceDiagram
participant Utilisateur
participant ContactController
participant ActiveUserList
participant DatabaseManager

Utilisateur->>ContactController: Demande de changer de nickname
activate ContactController
ContactController->>ActiveUserList: Appelle nicknameUsed(newNickname)
activate ActiveUserList
alt Nickname indisponible
    ContactController-->>Utilisateur: Message d'erreur (nickname déjà pris)
    deactivate ActiveUserList
else Nickname disponible
    ContactController->>ContactController: Met à jour le nickname avec setNickname(newNickname)
    activate ContactController
    ContactController->>ActiveUserList: Ajoute l'utilisateur avec addUser(sender)
    activate ActiveUserList
    ActiveUserList->>DatabaseManager: Appelle addOrUpdateUser pour sauvegarder
    activate DatabaseManager
    ContactController->>ContactController: Appelle sendNickname()
    deactivate ActiveUserList
    deactivate DatabaseManager
    ContactController-->>Utilisateur: Succès (nickname mis à jour)
end
deactivate ContactController
```

# Diagramme de séquence 3 : Client TCP - Connexion et envoi de message

```mermaid
sequenceDiagram
    participant Utilisateur
    participant TextingController
    participant TCPSender
    participant Socket
    participant DatabaseManager
    participant Message

    %% Début de la connexion
    Utilisateur->>TextingController: startWithPerson(ip_dest)
    activate TextingController
    TextingController->>TCPSender: startConnection(ip_dest, port)
    activate TCPSender
    TCPSender->>Socket: new Socket(ip_dest, port)
    activate Socket
    Socket-->>TCPSender: Instance créée
    deactivate Socket
    TCPSender->>TCPSender: Initialisation des flux (Input/Output)
    TCPSender-->>TextingController: Connexion établie
    deactivate TCPSender
    TextingController-->>Utilisateur: Confirmation de connexion

    %% Envoi du message
    Utilisateur->>TextingController: sendMessage(msg)
    activate TextingController
    TextingController->>TCPSender: sendMessage(msg)
    activate TCPSender
    TCPSender->>Message: Création d'une instance Message
    activate Message
    Message-->>TCPSender: Instance Message créée
    deactivate Message
    TCPSender->>DatabaseManager: addMessage(Message)
    activate DatabaseManager
    DatabaseManager-->>TCPSender: Message enregistré
    deactivate DatabaseManager
    TCPSender->>Socket: Envoi du message via OutputStream
    activate Socket
    Socket-->>TCPSender: Confirmation de réception
    deactivate Socket
    deactivate TCPSender

    %% Fermeture de la connexion
    Utilisateur->>TextingController: endWithCurrentPerson()
    activate TextingController
    TextingController->>TCPSender: stopConnection()
    activate TCPSender
    TCPSender->>Socket: Fermeture des flux et de la connexion
    activate Socket
    Socket-->>TCPSender: Connexion fermée
    deactivate Socket
    TCPSender-->>TextingController: Ressources libérées
    deactivate TCPSender
    TextingController-->>Utilisateur: Confirmation de déconnexion
    deactivate TextingController

```

# Diagramme de séquence 4 : Serveur TCP - Connexions entrantes et réception de messages

```mermaid
sequenceDiagram
    participant Utilisateur
    participant TextingController
    participant TCPServer
    participant Socket
    participant DatabaseManager
    participant Message
    participant TCPObserver

    Utilisateur->>TextingController: startServer(port)
    activate TextingController
    TextingController->>TCPServer: start(port)
    activate TCPServer
    TCPServer->>Socket: new ServerSocket(port)
    activate Socket
    Socket-->>TCPServer: ServerSocket créé
    deactivate Socket
    TCPServer-->>TextingController: Serveur prêt
    deactivate TCPServer
    TextingController-->>Utilisateur: Serveur démarré
    deactivate TextingController

    loop Écoute continue des messages entrants
        TCPServer->>Socket: accept()
        activate Socket
        Socket-->>TCPServer: Nouvelle connexion client
        deactivate Socket
        TCPServer->>ClientHandler: new ClientHandler(clientSocket)
        activate ClientHandler
        ClientHandler->>Socket: in.readLine()
        activate Socket
        Socket-->>ClientHandler: Message reçu
        deactivate Socket

        ClientHandler->>Message: new Message(inputLine, date, ipSource, ipDest)
        activate Message
        Message-->>ClientHandler: Message créé
        deactivate Message
        ClientHandler->>DatabaseManager: addMessage(message)
        activate DatabaseManager
        DatabaseManager-->>ClientHandler: Message ajouté à la base de données
        deactivate DatabaseManager
        ClientHandler->>TCPObserver: notifyObservers(message)
        activate TCPObserver
        TCPObserver-->>ClientHandler: Observers notifiés
        deactivate TCPObserver
        deactivate ClientHandler
    end

    Utilisateur->>TextingController: stopServer()
    activate TextingController
    TextingController->>TCPServer: stop()
    activate TCPServer
    TCPServer->>Socket: close()
    activate Socket
    Socket-->>TCPServer: ServerSocket fermé
    deactivate Socket
    TCPServer-->>TextingController: Serveur arrêté
    deactivate TCPServer
    TextingController-->>Utilisateur: Serveur arrêté
    deactivate TextingController
```
