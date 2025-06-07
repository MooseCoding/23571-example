package org.firstinspires.ftc.teamcode.dairy.subsystems

import dev.frozenmilk.mercurial.commands.Lambda
import org.firstinspires.ftc.teamcode.dairy.util.Waiter

class Wait {

    companion object {
        private val waiter = Waiter()

        fun wait(mS: Int): Lambda {
            return Lambda("wait")
                .setRequirements()
                .setInit {
                    waiter.start(mS)
                }
                .setFinish {
                    waiter.isDone
                }
        }
    }
}