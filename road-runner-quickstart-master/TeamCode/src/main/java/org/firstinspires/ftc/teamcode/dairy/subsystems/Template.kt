package org.firstinspires.ftc.teamcode.dairy.subsystems

import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import java.lang.annotation.Inherited

// Change template out for the subsystem and its name, leave the implementation of Subsystem though the same
object Template : Subsystem {
    // These next 6 lines setup the @Subsystem.Attach function allowing us to use it in our OpModes,
    // just copy and paste into your subsystem
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    @Inherited
    annotation class Attach
    override var dependency: Dependency<*> = Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    // Use this to declare any thing like motor directions or default commands
    override fun preUserInitHook(opMode: Wrapper) {
        // Setting the default command is what runs every loop, so use it for things like a PID updater
        // Or something that would change every loop independent of user control
        // Look at the example code for a PID updater usage
        defaultCommand = command()
    }

    override fun postUserInitHook(opMode: Wrapper) {
        // Additional Logic, but you use postUserInitHook to run similar stuff as preUserInitHook
    }

    // Note: There are other commands like postUserLoopHook that do similar to the other 2 just with the respective step

    fun command(): Lambda { // A lambda is a Mercurial function that allows us to schedule it
        return Lambda("simple")
            .setRequirements(Template) // Always require the object you make in any function using it, its just a failsafe to make sure that you don't have a null call or 2 things running at once
            .setExecute {
                // Whatever you want to run
            }
    }
}