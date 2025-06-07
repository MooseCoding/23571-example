package org.firstinspires.ftc.teamcode.dairy.subsystems

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.hardware.Servo
import dev.frozenmilk.dairy.core.FeatureRegistrar
import dev.frozenmilk.dairy.core.dependency.Dependency
import dev.frozenmilk.dairy.core.dependency.annotation.SingleAnnotation
import dev.frozenmilk.dairy.core.wrapper.Wrapper
import dev.frozenmilk.mercurial.commands.Lambda
import dev.frozenmilk.mercurial.subsystems.Subsystem
import org.firstinspires.ftc.teamcode.dairy.util.Waiter
import java.lang.annotation.Inherited

@Config
object OuttakeClaw: Subsystem {
    @Retention(AnnotationRetention.RUNTIME)
    @Target(AnnotationTarget.CLASS)
    @MustBeDocumented
    @Inherited
    annotation class Attach

    private val claw by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.servo.get("outtakeClaw")
    }
    private val pitch by subsystemCell {
        FeatureRegistrar.activeOpMode.hardwareMap.servo.get("outtakePitch")
    }

    override var dependency: Dependency<*> = Subsystem.DEFAULT_DEPENDENCY and SingleAnnotation(Attach::class.java)

    override fun postUserInitHook(opMode: Wrapper) {
        //defaultCommand = update()

        //waiter = Waiter()
    }

    override fun postUserLoopHook(opMode: Wrapper) {
        // Additional loop logic can go here
    }

    val claw_open: Double = 0.78
         val claw_close: Double = 0.48
         val pitch_up: Double = 0.07
         val pitch_down: Double = 0.407

        @JvmField var claw_pos: Double = claw_open
        @JvmField var pitch_pos: Double = pitch_down


    fun update(): Lambda {
        var waiter:Waiter = Waiter()

        return Lambda("update outtake claw")
            .setRequirements(OuttakeClaw)
            .setExecute{
                pitch.position = pitch_pos
                claw.position = claw_pos
            }
            .setFinish{false}
    }

    fun pitchUp(): Lambda {
        var waiter:Waiter = Waiter()

        return Lambda("pitch up")
            .setRequirements(pitch)
            .setInit {
                pitch_pos = pitch_up
                pitch.position = pitch_up
                waiter.start(200)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun pitchDown(): Lambda {
        var waiter:Waiter = Waiter()

        return Lambda("pitch down")
            .setRequirements(pitch)
            .setInit {
                pitch_pos = pitch_down
                pitch.position = pitch_down
                waiter.start(500)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun clawClose(): Lambda {
        var waiter:Waiter = Waiter()

        return Lambda("claw close")
            .setRequirements(claw)
            .setInit {
                claw_pos = claw_close
                claw.position = claw_close
                waiter.start(250)
            }
            .setFinish {
                waiter.isDone
            }
    }

    fun clawOpen(): Lambda {
        var waiter:Waiter = Waiter()

        return Lambda("claw open")
            .setRequirements(claw)
            .setInit {
                claw_pos = claw_open
                claw.position = claw_open
                waiter.start(200)
            }
            .setFinish {
                waiter.isDone
            }
    }
}