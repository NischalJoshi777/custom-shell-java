# CustomShell

CustomShell is a simple Unix-like shell simulator implemented in Java. It supports basic shell commands such as navigating directories, managing files, executing external commands, job control, Process Management, Memory Management, User Authenticated login to the shell, and role based file access permission to the users. This project is created by the following members.

  1. Rutu Shah
  2. Nabin Bhatta
  3. Nischal Joshi

## Features

### 1. **Basic Commands**: 
- `pwd`: Prints the current working directory.
- `cd <directory>`: Changes the current working directory.
- `echo <text>`: Outputs the provided text.
- `ls`: Lists files and directories in the current directory.
- `mkdir <directory>`: Creates a new directory.
- `rmdir <directory>`: Removes a directory.
- `rm <filename>`: Removes a file.
- `touch <filename>`: Creates a new empty file or updates the timestamp of an existing file.
- `cat <filename>`: Displays the contents of a file.
- `clear`: Clears the terminal screen.
  
### 2. **Job Control**:
  - `jobs`: List background jobs.
  - `kill <pid>`: Terminate a running background job.
  - `fg <job_id>`: Bring a background job to the foreground.
  - `bg <job_id>`: Resume a stopped job in the background.
  
### 3. **Exit**:
  - `exit`: Exit the shell.

### 4. **Piping**:
- Supports piping (`|`) to pass the output of one command as input to another. For example:
  - `cat file | grep error | sort`: This command reads from a file, filters lines containing "error", and sorts the output.

### 5. **Security Mechanisms**:
- **User Authentication**: The shell implements secure login, allowing users to log in with a username and password.
- **Role-Based Access Control**: Users are assigned roles (such as `admin` or `user`), and each role has different permissions.
- **File Permissions**: Users can only access files based on their roles, enforcing security policies.

### 6. **Process Management**:
- Processes are handled using Java's `ProcessBuilder` and `Thread` classes.
- Supports process scheduling (like Round-Robin and Priority Scheduling).
- Allows job control commands like `fg`, `bg`, and `jobs`.

### 7. **Memory Management**:
- The shell simulates basic memory management techniques, including paging and process memory handling.

### 8. **Advanced Features**:
- **Page Replacement Algorithms**: Supports FIFO and Least Recently Used (LRU) page replacement strategies.
- **Synchronization**: Implements producer-consumer and dining philosopher problems to demonstrate synchronization in multi-threaded environments.


## Requirements

- Java 8 or higher.
- A Unix-like environment (Linux, macOS, or Windows Subsystem for Linux).

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/NischalJoshi777/custom-shell-java.git
   cd customshell

2. Compile and run java code

    ```bash
      javac com/shellSimulator/*.java
    
3. Run the CustomShell class:

      ```bash
      java com.shellSimulator.CustomShell
      
4. Test Cases
      ```bash
      Welcome to Secure Shell!
      Username: admin
      Password: adminpassword
      Login successful! Role: admin
      admin@shell$ ls | grep .txt
      test1.txt
      testfile.txt
      user.txt
      admin@shell$ mkdir hellousers
      admin@shell$ touch hellousers.txt
      hellousers.txt created successfully.
      admin@shell$ pwd
      /Users/rutushah/custom-shell-java
      admin@shell$ producer_consumer
      Produced: 1
      Consumed: 1
      Produced: 2
      Consumed: 2
      Produced: 3
      Consumed: 3
      Produced: 4
      Consumed: 4
      Produced: 5
      Consumed: 5
      admin@shell$ exit
      Exiting shell...

For more help, please reach out to us directly via personal email.
      
