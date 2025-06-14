# Read

All the code that we will be looking at is located at <https://github.com/MooseCoding/23571-example/tree/main/road-runner-quickstart-master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode>

# TeleOP

Teleop code will be located inside the Dairy folder, there will be some documentation I'll make for this so do not worry if its confusing. Main structure is as follows: 

Subsystems houses all the code instiantiating our subsystems (who coulda guessed) and if the subsystem needs any methods (like going to a position for a servo or going to a target for a motor) we set them here

Control is for all our controllers. Right now we only use PID and PIDF controllers, but if we want to make any custom ones, or change stuff around, we have this folder.

Tuning contains test files that allow us to test indiviual subsections or whenever we want to test a few things, we just make a tuner for it. Please leave this alone througout the season.

Util contains our waiter (it allows us to set timers throughout our code) and other vital stuff, I'll update this if needed, but DONT TOUCH.

DairyMain.kt, or Main.kt or MainOpmode or the like will be the MAIN program we run whenever we run TeleOP. IT HAS EVERYTIHNG WE NEED AND NOTHING WE DON'T.


### Auto

I don't really know, I'ma be honest. I'll convert it to actual official updated pedro, but rn its a mix of weird older pedro versions and a lot of hacky fixes.
