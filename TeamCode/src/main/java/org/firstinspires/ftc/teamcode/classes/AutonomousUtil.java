package org.firstinspires.ftc.teamcode.classes;

//Standard Auto Imports
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

//Color Sensor Imports
import android.graphics.Color;
import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Locale;

public abstract class AutonomousUtil extends LinearOpMode {
    // Motors and Servos
    public DcMotor fRight, bRight, fLeft, bLeft, extend, extend2, retract, carousel;
    public CRServo leftGrabber, rightGrabber;

    // Speeds used in the code
    public final double driveSpeed = .75;
    public final double strafeSpeed = .6;
    public final double turnSpeed = .3;
    public final double markerSpeed = .4;

    //Encoder Variable
    public final double COUNTS_PER_INCH = (1440 / (4 * (355 / 113)));

    //Rev V2 Color Sensor
    public ColorSensor sensorColor;
    public ColorSensor sensorColor2;
    public DistanceSensor sensorDistance;
    public DistanceSensor sensorDistance2;

    // The IMU sensor object
    public BNO055IMU imu;

    //Variables used for updating telemetry
    public Orientation angles;
    public float curHeading;

    //Variables for logic
    public boolean found = false;
    public boolean found2 = false;

    //Color Sensor Variables
    // hsvValues is an array that will hold the hue, saturation, and value information.
    public float[] hsvValues = {0F, 0F, 0F};
    // values is a reference to the hsvValues array.
    public final float[] values = hsvValues;
    // sometimes it helps to multiply the raw RGB values with a scale factor
    // to amplify/accentuate the measured values.
    public final double SCALE_FACTOR = 255;

    public void hardwareInit(){
        /* STANDARD AUTONOMOUS MAPPING **/
        // Hardware mapping
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
        // Below is are the directions of each motor and servo
        fLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        bLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        fRight.setDirection(DcMotorSimple.Direction.FORWARD);
        bRight.setDirection(DcMotorSimple.Direction.FORWARD);
        extend.setDirection(DcMotorSimple.Direction.FORWARD);
        extend2.setDirection(DcMotorSimple.Direction.REVERSE);
        retract.setDirection(DcMotorSimple.Direction.REVERSE);
        carousel.setDirection(DcMotorSimple.Direction.FORWARD);
        leftGrabber.setDirection(DcMotorSimple.Direction.REVERSE);
        rightGrabber.setDirection(DcMotorSimple.Direction.FORWARD);

        // Initializing the encoders
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        retract.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        /* COLOR SENSOR SETUP **/
        // Get a reference to the color sensor.
        sensorColor = hardwareMap.get(ColorSensor.class, "Color Sensor");
        sensorColor2 = hardwareMap.get(ColorSensor.class, "Color Sensor2");
        // Get a reference to the distance sensor that shares the same name.
        sensorDistance = hardwareMap.get(DistanceSensor.class, "Color Sensor");
        sensorDistance2 = hardwareMap.get(DistanceSensor.class, "Color Sensor2");

        /* IMU SETUP **/
        // Set up the parameters with which we will use our IMU. Note that integration
        // algorithm here just reports accelerations to the logcat log; it doesn't actually
        // provide positional information.
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample OpMode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }

    // encoderDrive, uses encoders to drive. Set the leftInches and rightInches to different values to turn.
    public void encoderDrive(double speed, double leftInches, double rightInches) {
        int newLeftTarget, newLeftTarget2, newRightTarget, newRightTarget2;
        //Set to use the encoders while running
        fLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Reset the encoders
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {
            //Set targets to x inches forwards (positive) or backwards (negative)
            newLeftTarget = fLeft.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newLeftTarget2 = bLeft.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            newRightTarget = fRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            newRightTarget2 = bRight.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            fLeft.setTargetPosition(newLeftTarget);
            bLeft.setTargetPosition(newLeftTarget2);
            fRight.setTargetPosition(newRightTarget);
            bRight.setTargetPosition(newRightTarget2);

            // Turn On RUN_TO_POSITION
            fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //Set motors to speed
            fLeft.setPower(speed);
            bLeft.setPower(speed);
            fRight.setPower(speed);
            bRight.setPower(speed);

            while (opModeIsActive() && (fLeft.isBusy()) && bLeft.isBusy() && fRight.isBusy() && bRight.isBusy()) {
                // Display the encoder data for the driver.
                telemetry.addData("Front Target", "Running to %7d :%7d", newLeftTarget, newRightTarget);
                telemetry.addData("Front Current", "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
                telemetry.addData("Back Target", "Running to %7d :%7d", newLeftTarget2, newRightTarget2);
                telemetry.addData("Back Current", "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            fLeft.setPower(0);
            bLeft.setPower(0);
            fRight.setPower(0);
            bRight.setPower(0);

            // Turn off RUN_TO_POSITION
            fLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            fRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(50);   // optional pause after each move
        }
    }

    /* encoderStrafe, uses encoders to strafe. For direction, put "right" to turn right
     * and anything else to turn left, would still recommend you put left though. */
    public void encoderStrafe(double Speed, @NonNull String direction, double inches) {
        int newLeftTarget, newLeftTarget2, newRightTarget, newRightTarget2;
        //Set to use the encoders while running
        fLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Reset the encoders
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {
            //Set the targets to be x inches in front of it. Change two to negative based on which direction we strafe.
            newLeftTarget = fLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            newLeftTarget2 = bLeft.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            newRightTarget = fRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            newRightTarget2 = bRight.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            /* Set the Back Left and Front Right wheel to rotate opposite to strafe right if direction is
            set to "right" */
            if (direction.equalsIgnoreCase("right")) {
                fLeft.setTargetPosition(newLeftTarget);
                bLeft.setTargetPosition(-newLeftTarget2);
                fRight.setTargetPosition(-newRightTarget);
                bRight.setTargetPosition(newRightTarget2);
            }
            /* Set the Front Left and Back Right wheel to rotate opposite to strafe if direction is
            set to anything but "right", please use "left" for clarity. */
            else {
                fLeft.setTargetPosition(-newLeftTarget);
                bLeft.setTargetPosition(newLeftTarget2);
                fRight.setTargetPosition(newRightTarget);
                bRight.setTargetPosition(-newRightTarget2);
            }

            // Turn On RUN_TO_POSITION
            fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //Set motors to speed
            fLeft.setPower(Speed);
            bLeft.setPower(Speed);
            fRight.setPower(Speed);
            bRight.setPower(Speed);

            while (opModeIsActive() && (fLeft.isBusy()) && bLeft.isBusy() && fRight.isBusy() && bRight.isBusy()) {
                // Display the encoder data for the driver.
                telemetry.addData("Left Target", "Running to %7d :%7d", newLeftTarget, newLeftTarget2);
                telemetry.addData("Left Current", "Running at %7d :%7d", fLeft.getCurrentPosition(), bLeft.getCurrentPosition());
                telemetry.addData("Right Target", "Running to %7d :%7d", newRightTarget, newRightTarget2);
                telemetry.addData("Right Current", "Running at %7d :%7d", fRight.getCurrentPosition(), bRight.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            fLeft.setPower(0);
            bLeft.setPower(0);
            fRight.setPower(0);
            bRight.setPower(0);

            // Turn off RUN_TO_POSITION
            fLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            fRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(50);   // optional pause after each move
        }
    }

    // Variant of encoderStrafe, used to check for the marker while strafing at the same time.
    public void markerStrafe(double Speed, @NonNull String direction, int wall) {
        int newLeftTarget, newLeftTarget2, newRightTarget, newRightTarget2;
        //Set to use the encoders while running
        fLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //Reset the encoders
        fLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {
            /* Set the targets to be x inches to the side of the robot, the side will be determined
             * later on and the targets will be flipped to negative as needed. */
            newLeftTarget = fLeft.getCurrentPosition() + (wall);
            newLeftTarget2 = bLeft.getCurrentPosition() + (wall);
            newRightTarget = fRight.getCurrentPosition() + (wall);
            newRightTarget2 = bRight.getCurrentPosition() + (wall);
            /* Set the Back Left and Front Right wheel to rotate opposite to strafe right if
            direction is set to "right" */
            if (direction.equalsIgnoreCase("right")) {
                fLeft.setTargetPosition(newLeftTarget);
                bLeft.setTargetPosition(-newLeftTarget2);
                fRight.setTargetPosition(-newRightTarget);
                bRight.setTargetPosition(newRightTarget2);
            }
            /* Set the Front Left and Back Right wheel to rotate opposite to strafe left if
            direction is set to anything but "right", please use "left" for clarity. */
            else {
                fLeft.setTargetPosition(-newLeftTarget);
                bLeft.setTargetPosition(newLeftTarget2);
                fRight.setTargetPosition(newRightTarget);
                bRight.setTargetPosition(-newRightTarget2);
            }

            // Turn On RUN_TO_POSITION
            fRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //Set motors to speed
            fLeft.setPower(Speed);
            bLeft.setPower(Speed);
            fRight.setPower(Speed);
            bRight.setPower(Speed);

            while (opModeIsActive() && (fLeft.isBusy() && bLeft.isBusy() && fRight.isBusy() && bRight.isBusy())
                    && (!found && !found2)) {
                // convert the RGB values to HSV values.
                // multiply by the SCALE_FACTOR.
                // then cast it back to int (SCALE_FACTOR is a double)
                Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
                        (int) (sensorColor.green() * SCALE_FACTOR),
                        (int) (sensorColor.blue() * SCALE_FACTOR),
                        hsvValues);

                Color.RGBToHSV((int) (sensorColor2.red() * SCALE_FACTOR),
                        (int) (sensorColor2.green() * SCALE_FACTOR),
                        (int) (sensorColor2.blue() * SCALE_FACTOR),
                        hsvValues);
                // send the info back to driver station using telemetry function.
                /* Commenting out unneeded debugging telemetry to optimize code.
                telemetry.addData("Distance (cm)", String.format(Locale.US, "%.02f", sensorDistance2.getDistance(DistanceUnit.CM)));
                telemetry.addData("Red  ", sensorColor2.red());
                telemetry.addData("Green", sensorColor2.green());
                telemetry.addData("Blue ", sensorColor2.blue());
                telemetry.addData("Hue", hsvValues[0]);

                telemetry.addData("Left Target", "Running to %7d :%7d", newLeftTarget, newLeftTarget2);
                telemetry.addData("Left Current", "Running at %7d :%7d", fLeft.getCurrentPosition(), bLeft.getCurrentPosition());
                telemetry.addData("Right Target", "Running to %7d :%7d", newRightTarget, newRightTarget2);
                telemetry.addData("Right Current", "Running at %7d :%7d", fRight.getCurrentPosition(), bRight.getCurrentPosition());
                telemetry.update();
                 */

                //Check for the marker
                found = ((sensorColor.green() > (1.5 * sensorColor.red())) && (sensorColor.green() > (1.5 * sensorColor.blue())) &&
                        (sensorDistance.getDistance(DistanceUnit.CM)) < 10.0);

                found2 = ((sensorColor2.green() > (1.5 * sensorColor2.red())) && (sensorColor2.green() > (1.5 * sensorColor2.blue())) &&
                        (sensorDistance2.getDistance(DistanceUnit.CM)) < 10.0);
            }

            // Stop all motion;
            fLeft.setPower(0);
            bLeft.setPower(0);
            fRight.setPower(0);
            bRight.setPower(0);

            // Turn off RUN_TO_POSITION
            fLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            fRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            bRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            /*
            while (opModeIsActive()){
                telemetry.addData("Found", found);
                telemetry.addData("Distance (cm)", String.format(Locale.US, "%.02f", sensorDistance.getDistance(DistanceUnit.CM)));
                telemetry.addData("Red  ", sensorColor.red());
                telemetry.addData("Green", sensorColor.green());
                telemetry.addData("Blue ", sensorColor.blue());
                telemetry.addData("Hue", hsvValues[0]);

                telemetry.addData("Found", found2);
                telemetry.addData("Distance (cm)", String.format(Locale.US, "%.02f", sensorDistance2.getDistance(DistanceUnit.CM)));
                telemetry.addData("Red  ", sensorColor2.red());
                telemetry.addData("Green", sensorColor2.green());
                telemetry.addData("Blue ", sensorColor2.blue());
                telemetry.addData("Hue", hsvValues[0]);

                telemetry.update();
            }

             */
        }
    }

    /* encoderArm, uses encoders to move the arm up or down a certain number of inches.
     * Set 'up' to true to raise the arm and 'up' to false to lower the arm. */
    public void encoderArm(double Speed, boolean up, double inches) {
        int newExtendTarget, newExtend2Target, newRetractTarget;
        double extendSpeed, retractSpeed;
        //Set to use the encoders while running
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extend2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        retract.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Reset the encoders
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        retract.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        // Ensure that the OpMode is still active
        if (opModeIsActive()) {
            //Set targets to
            newExtendTarget = extend.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            newExtend2Target = extend2.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);
            newRetractTarget = retract.getCurrentPosition() + (int) (inches * COUNTS_PER_INCH);

            if (up) {
                extend.setTargetPosition(newExtendTarget);
                extend2.setTargetPosition(newExtend2Target);
                retract.setTargetPosition(-newRetractTarget);
                extendSpeed = Speed;
                retractSpeed = (Speed * 0.5);
            } else {
                extend.setTargetPosition(-newExtendTarget);
                extend2.setTargetPosition(-newExtend2Target);
                retract.setTargetPosition(newRetractTarget);
                extendSpeed = (Speed * 0.5);
                retractSpeed = Speed;
            }

            // Turn On RUN_TO_POSITION
            extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            extend2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            retract.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            //Set motors to speed
            extend.setPower(extendSpeed);
            extend2.setPower(extendSpeed);
            retract.setPower(retractSpeed);


            while (opModeIsActive() && (extend.isBusy() && extend2.isBusy() || retract.isBusy())) {
                // Display the encoder data for the driver.
                telemetry.addData("Extend Target", "Running to %7d :%7d", newExtendTarget, newExtend2Target);
                telemetry.addData("Extend Position", "Running at %7d :%7d", extend.getCurrentPosition(), extend2.getCurrentPosition());
                telemetry.addData("Retract Target", "Running to %7d", newRetractTarget);
                telemetry.addData("Retract Position", "Running at %7d", retract.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            extend.setPower(0);
            extend2.setPower(0);
            retract.setPower(0);

            // Turn off RUN_TO_POSITION
            extend.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            extend2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            retract.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            sleep(50);   // optional pause after each move
        }
    }

    // rotates the robot to a certain angle using the IMU
    public void rotate(double Speed, @NonNull String direction, double angle) {
        //Turn right if direction is set to "right"
        if (direction.equalsIgnoreCase("right")) {
            fRight.setPower(-1);
            bRight.setPower(-1);
            fLeft.setPower(1);
            bLeft.setPower(1);
            while (curHeading > -angle*.75){
                checkOrientation();
            }
            fRight.setPower(-Speed);
            bRight.setPower(-Speed);
            fLeft.setPower(Speed);
            bLeft.setPower(Speed);
            //Constantly check the angle so that we can stop
            while (curHeading > -angle) {
                checkOrientation();
            }
        }
        //Turn left if the direction is set to anything else, for clarity, use left
        else {
            fRight.setPower(-1);
            bRight.setPower(-1);
            fLeft.setPower(1);
            bLeft.setPower(1);
            while (curHeading > angle*.75){
                checkOrientation();
            }
            fRight.setPower(Speed);
            bRight.setPower(Speed);
            fLeft.setPower(-Speed);
            bLeft.setPower(-Speed);
            //Constantly check the angle so that we can stop
            while (curHeading < angle) {
                checkOrientation();
            }
        }
        fRight.setPower(0);
        bRight.setPower(0);
        fLeft.setPower(0);
        bLeft.setPower(0);
    }

    public void checkOrientation() {
        // Grab the heading.
        angles = this.imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        // Save the heading
        curHeading = angles.firstAngle;
    }
}
