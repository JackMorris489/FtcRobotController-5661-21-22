package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Disabled
@TeleOp(name = "Test Bot",group = "TeleOP")
public class TestBot extends OpMode {

    //Declare motor objects
    private DcMotor fRight, bRight, fLeft, bLeft;


    @Override
    public void init(){
        //HardwareMap
        fLeft = hardwareMap.dcMotor.get("Front Left");
        bLeft = hardwareMap.dcMotor.get("Back Left");
        fRight = hardwareMap.dcMotor.get("Front Right");
        bRight = hardwareMap.dcMotor.get("Back Right");

        //Motor Directions
        fRight.setDirection(DcMotorSimple.Direction.REVERSE);
        bRight.setDirection(DcMotorSimple.Direction.REVERSE);
        fLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        bLeft.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    public void loop(){
        //Wheel drive
        float leftStick = (float) -(Range.clip(gamepad1.left_stick_y, -1, 1));
        float rightStick = (float) -(Range.clip(gamepad1.right_stick_y, -1, 1));

        //Drive code
        fLeft.setPower(leftStick);
        bLeft.setPower(leftStick);
        fRight.setPower(rightStick);
        bRight.setPower(rightStick);

        //Telemetry
        telemetry.addData("Left Joystick Raw Y Value", gamepad1.left_stick_y);
        telemetry.addData("Left Joystick Raw X Value", gamepad1.left_stick_x);
        telemetry.addData("Right Joystick Raw Y Value", gamepad1.right_stick_y);
        telemetry.addData("Right Joystick Raw X Value", gamepad1.right_stick_x);
        telemetry.addData("Front Position",  "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
        telemetry.addData("Back Position",  "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
        telemetry.update();
    }
}
