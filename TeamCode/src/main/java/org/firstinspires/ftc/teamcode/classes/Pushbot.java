package org.firstinspires.ftc.teamcode.classes;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/*
    Basic robot with 4 wheels as a base for future classes
    Motors are called:
    Front Right
    Back Right
    Front Left
    Back Left
 */
public class Pushbot {

    public DcMotor fRight = null;
    public DcMotor bRight = null;
    public DcMotor fLeft = null;
    public DcMotor bLeft = null;

    HardwareMap hwMap = null;

    //ahMap is the name of the hardware map, so it must be hardwareMap, the boolean must be true if you are using encoders.
    public void init(HardwareMap ahwMap, boolean encoders) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        fRight = hwMap.get(DcMotor.class, "Front Right");
        bRight = hwMap.get(DcMotor.class, "Back Right");
        fLeft = hwMap.get(DcMotor.class, "Front Left");
        bLeft = hwMap.get(DcMotor.class, "Back Left");

        if(encoders) {
            fRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        else{
            fRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            fLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }
}
