package org.firstinspires.ftc.teamcode.automarker;

// Imports
import android.graphics.Color;
import androidx.annotation.NonNull;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.classes.AutonomousUtil;

@Autonomous (name= "Blue Marker Storage Park", group= "Autonomous")
public class BlueMarkerStoragePark extends AutonomousUtil {
    @Override
    public void runOpMode() {
        // initialize hardware
        hardwareInit();

        /* AUTONOMOUS START **/
        waitForStart();
        // Raise the arm to prevent it from messing with the field
        encoderArm(1,true,4);
        // Move up to the barcode to scan.
        encoderDrive(driveSpeed, 16.25, 16.25);
        // Strafe right to scan with a special speed for scan strafing
        markerStrafe(markerSpeed, "right", 6000);
        // Save where the maker is and move to the wall if not already there. Set to one if non-detected.
        int markerSquare = 1;
        // Square three
        if (found && fLeft.getCurrentPosition() >= 2000) {
            markerSquare = 3;
            encoderStrafe(strafeSpeed, "right", 26);
        }
        // Square two
        else if (found && fLeft.getCurrentPosition() >= 1200) {
            markerSquare = 2;
            encoderStrafe(strafeSpeed, "right", 34);
        }
        // Square one
        else if (found) {
            encoderStrafe(strafeSpeed, "right", 48);
        }
        // Get off of the wall to prevent skidding along it, messing up the rest of the autonomous.
        encoderStrafe(strafeSpeed, "left", 4);
        // Now that we are at the wall, drive forward to line up with the shipping hub.
        encoderDrive(driveSpeed, 24, 24);
        // Once we are lined up, rotate 90 degrees to the left to be straight on with the shipping hub.
        rotate(turnSpeed, "left", 90);
        // Raise the arm to the correct level.
        switch (markerSquare) {
            // Get the arm to the corresponding level and drive forwards to the shipping hub.
            case 1:
                encoderArm(1, false, 2.5);
                encoderDrive(driveSpeed, 29, 29);
                break;
            case 2:
                encoderDrive(driveSpeed, 27, 27);
                break;
            default:
                encoderArm(1, true, 3);
                encoderDrive(driveSpeed, 24, 24);
        }

        // Drop the block
        leftGrabber.setPower(-1);
        rightGrabber.setPower(-1);
        sleep(2000);
        leftGrabber.setPower(0);
        rightGrabber.setPower(0);

        // Move backwards toward the storage zone
        switch (markerSquare) {
            case 1:
                encoderDrive(driveSpeed, -27, -27);
                break;
            case 2:
                encoderDrive(driveSpeed, -28, -28);
                break;
            // bruh
            default:
                encoderDrive(driveSpeed, -29, -29);
        }
        // Strafe to the left to park in the storage zone
        encoderStrafe(strafeSpeed, "left", 14);

        // Lower the arm
        switch (markerSquare) {
            case 1:
                encoderArm(1, false, 1);
                break;
            case 2:
                encoderArm(1, false, 3.5);
                break;
            // bruh
            default:
                encoderArm(1, false, 6.5);
        }
        // Telemetry data for the end of the code.
        // Commenting out the debugging telemetry to optimize the code.
        /*
        while (opModeIsActive()) {
            telemetry.addData("Found", found);
            telemetry.addData("Square Number", markerSquare);
            telemetry.addData("Front Position", "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
            telemetry.addData("Back Position", "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
            telemetry.update();
        }
        */
    }

    @Override
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
                    && !found) {
                // convert the RGB values to HSV values.
                // multiply by the SCALE_FACTOR.
                // then cast it back to int (SCALE_FACTOR is a double)
                Color.RGBToHSV((int) (sensorColor.red() * SCALE_FACTOR),
                        (int) (sensorColor.green() * SCALE_FACTOR),
                        (int) (sensorColor.blue() * SCALE_FACTOR),
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
}