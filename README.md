# CustomShell

CustomShell is a simple Unix-like shell simulator implemented in Java. It supports basic shell commands such as navigating directories, managing files, executing external commands, job control, and more.
This is the university project created by following members of the team.

  1. Rutu Shah
  2. Nabin Bhatta
  3. Nischal Joshi

## Features

- **Basic Commands**: 
  - `pwd`: Print the current working directory.
  - `cd <directory>`: Change the current directory.
  - `echo <text>`: Output the provided text.
  - `ls`: List files in the current directory.
  - `mkdir <directory>`: Create a new directory.
  - `rmdir <directory>`: Remove a directory.
  - `rm <filename>`: Remove a file.
  - `touch <filename>`: Create a new empty file or update the timestamp of an existing file.
  
- **Job Control**:
  - `jobs`: List background jobs.
  - `kill <pid>`: Terminate a running background job.
  - `fg <job_id>`: Bring a background job to the foreground.
  - `bg <job_id>`: Resume a stopped job in the background.
  
- **Exit**:
  - `exit`: Exit the shell.

## Requirements

- Java 8 or higher.
- A Unix-like environment (Linux, macOS, or Windows Subsystem for Linux).

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/yourusername/customshell.git
   cd customshell

2. Compile and run java code

    ```bash
      javac com/shellSimulator/*.java
    
3. Run the CustomShell class:

      ```bash
      java com.shellSimulator.CustomShell
      
4. Example
      ```bash
      $ mkdir testDir
      $ cd testDir
      $ touch testFile.txt
      $ echo "Hello, world!" > testFile.txt
      $ cat testFile.txt
      Hello, world!
      $ ls
      testFile.txt
      $ exit
      Exiting shell...
