package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Comp TeleOp",group = "TeleOP")
public class CompTeleOp extends OpMode {

    private DcMotor fRight, bRight, fLeft, bLeft, extend, extend2, retract, carousel;
    private CRServo leftGrabber, rightGrabber;

    @Override
    public void init() {
        //Below is the HardwareMap
        fLeft = hardwareMap.dcMotor.get("Front Left");
        bLeft = hardwareMap.dcMotor.get("Back Left");
        fRight = hardwareMap.dcMotor.get("Front Right");
        bRight = hardwareMap.dcMotor.get("Back Right");
        extend = hardwareMap.dcMotor.get("Extend");
        extend2 = hardwareMap.dcMotor.get("Extend 2");
        retract = hardwareMap.dcMotor.get("Retract");
        carousel = hardwareMap.dcMotor.get("Carousel");
        leftGrabber = hardwareMap.crservo.get("Left Grabber");
        rightGrabber = hardwareMap.crservo.get("Right Grabber");
        //Below is are the directions of each motor and servo
        fLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        bLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        fRight.setDirection(DcMotorSimple.Direction.FORWARD);
        bRight.setDirection(DcMotorSimple.Direction.FORWARD);
        extend.setDirection(DcMotorSimple.Direction.FORWARD);
        extend2.setDirection(DcMotorSimple.Direction.REVERSE);
        retract.setDirection(DcMotorSimple.Direction.REVERSE);
        carousel.setDirection(DcMotorSimple.Direction.FORWARD);
        leftGrabber.setDirection(DcMotorSimple.Direction.FORWARD);
        rightGrabber.setDirection(DcMotorSimple.Direction.REVERSE);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        retract.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extend2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        retract.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void loop() {
        teleOp();
        float armStick = -(Range.clip(gamepad2.right_stick_y, -1, 1));
        if (armStick > 0){
            extend.setPower(armStick);
            extend2.setPower(armStick);
            retract.setPower(-armStick * 0.4);
        }
        else if(armStick < 0){
            extend.setPower(armStick * 0.2);
            extend2.setPower(armStick * 0.2);
            retract.setPower(-armStick);
        }
        else if(gamepad2.x){
            setArm(1,120);
        }
        else if(gamepad2.b){
            extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            extend2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            retract.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            extend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            extend2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            retract.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        else{
            extend.setPower(0);
            extend2.setPower(0);
            retract.setPower(0);
        }
    }

    public void teleOp(){
        //These are the controls for the wheels.
        float leftStick = -(Range.clip(gamepad1.left_stick_y, -1, 1));
        float rightStick = -(Range.clip(gamepad1.right_stick_y, -1, 1));
        //Strafe left
        if (gamepad1.x){
            fLeft.setPower(-1);
            bLeft.setPower(1);
            fRight.setPower(1);
            bRight.setPower(-1);
        }
        //Strafe right
        else if (gamepad1.b){
            fLeft.setPower(1);
            bLeft.setPower(-1);
            fRight.setPower(-1);
            bRight.setPower(1);
        }
        //Tank Controls
        else {
            fLeft.setPower(leftStick);
            bLeft.setPower(leftStick);
            fRight.setPower(rightStick);
            bRight.setPower(rightStick);
        }

        //Carousel controls
        if (gamepad1.right_trigger > 0|| gamepad2.right_trigger > 0){
            carousel.setPower(1);
        }
        else if (gamepad1.left_trigger > 0|| gamepad2.left_trigger > 0){
            carousel.setPower(-1);
        }
        else{
            carousel.setPower(0);
        }

        //Grabber Controls
        if(gamepad2.dpad_down){
            leftGrabber.setPower(.75);
            rightGrabber.setPower(.75);
        }
        else if(gamepad2.dpad_up){
            leftGrabber.setPower(-.75);
            rightGrabber.setPower(-.75);
        }
        else {
            leftGrabber.setPower(0);
            rightGrabber.setPower(0);
        }
        /*
        telemetry.addData("Front Position",  "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
        telemetry.addData("Back Position",  "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
        telemetry.addData("Extend Position", "Running at %7d :%7d", extend.getCurrentPosition(), extend2.getCurrentPosition());
        telemetry.addData("Retract Position", "Running at %7d", retract.getCurrentPosition());
        telemetry.update();
        */
    }

    public void setArm(double speed, int counts) {
        int newExtendTarget, newExtend2Target, newRetractTarget;
        double extendSpeed, retractSpeed;
        //Set to use the encoders while running
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extend2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        retract.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Set targets to
        newExtendTarget = (counts);
        newExtend2Target = (counts);
        newRetractTarget = -(counts);
        extend.setTargetPosition(newExtendTarget);
        extend2.setTargetPosition(newExtend2Target);
        retract.setTargetPosition(newRetractTarget);

        if (extend.getCurrentPosition() < counts) {
            extendSpeed = speed;
            retractSpeed = (speed * 0.5);
        } else {
            extendSpeed = (speed * 0.5);
            retractSpeed = speed;
        }

        // Turn On RUN_TO_POSITION
        extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        extend2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        retract.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Set motors to speed
        extend.setPower(extendSpeed);
        extend2.setPower(extendSpeed);
        retract.setPower(retractSpeed);

        while ((extend.isBusy() || extend2.isBusy() || retract.isBusy())) {
            // Run teleOp to allow driving while the arm moves
            teleOp();
        }

        // Stop all motion;
        extend.setPower(0);
        extend2.setPower(0);
        retract.setPower(0);

        // Turn off RUN_TO_POSITION
        extend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        extend2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        retract.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}