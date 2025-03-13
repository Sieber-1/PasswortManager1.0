PASSWORT MANAGER 1.0

Simple but effective password manager – use it to store your passwords for games, websites, etc.


In the PasswortManager.java file, you'll find the code along with the necessary Java JPI classes.


At the start of the code, there's an example hash algorithm you can use to convert your master password into a hash. If your own hash algorithm doesn't work or you can't implement it, I've added a debug output that shows which hash was used and which one was entered. This way, you can figure out what went wrong.

User Interface
If everything is set up correctly, a window should pop up asking you to enter your master password: 
![Screenshot 2025-03-13 201147](https://github.com/user-attachments/assets/c702aca6-00f6-40b6-bd7a-6966d55b8f65)




After that, you can access the manager and sort your passwords:




![Screenshot 2025-03-13 201329](https://github.com/user-attachments/assets/d9271843-7c67-4e2e-8e43-2fba79dacb15)


When you save a password, a text document will appear on your desktop containing your saved password in encrypted form. Don’t panic—it’s safely encrypted, but it’s best to store the file in a secure folder.


Optionally, you can open the code in IntelliJ or Eclipse, export it as a JAR, and even convert it to an EXE if you want. I recommend using the JAR so you can easily launch the manager from your desktop without having to run the code repeatedly. I converted my JAR to an EXE using Launchj, giving my program a simple icon.

![Screenshot 2025-03-13 201733](https://github.com/user-attachments/assets/a5df6b34-b893-41ca-8599-28c87db95be6)
