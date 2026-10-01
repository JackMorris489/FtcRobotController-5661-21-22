package org.firstinspires.ftc.teamcode.automarker;

// Auto Imports
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.classes.AutonomousUtil;


@Autonomous (name= "Blue Marker Warehouse Park", group= "Autonomous")
public class BlueMarkerWarehousePark extends AutonomousUtil {
    @Override
    public void runOpMode() {
        hardwareInit();

        //AUTO START
        waitForStart();
        // Move up to the barcode to scan.
        encoderDrive(driveSpeed, 15.25, 15.25);

        encoderStrafe(strafeSpeed, "left", 4);

        // Strafe left to scan with a special speed for scan strafing
        markerStrafe(.35, "right", 3400);

        // Save where the maker is and move to the front of the shipping hub.
        // Set to one (left) if non-detected.
        int markerSquare = 1;

        // Square three (right)
        if (found && fLeft.getCurrentPosition() >= 700) {
            markerSquare = 3;
            // Get to the center
            encoderStrafe(strafeSpeed, "right", 20);
        }
        //Square two (middle)
        else if (found2 && fLeft.getCurrentPosition() >= 200) {
            markerSquare = 2;
            // Get to the center
            encoderStrafe(strafeSpeed, "right", 21);
        }
        else if (found2) {
            encoderStrafe(strafeSpeed, "right", 28.25);
        }

        switch (markerSquare) {
            // Get the arm to the corresponding level and drive forwards to the shipping hub.
            case 1:
                encoderArm(1, true, 1.5);
                encoderDrive(driveSpeed, 3.5, 3.5);
                break;
            case 2:
                encoderArm(1, true, 4);
                encoderDrive(driveSpeed, 2.5, 2.5);
                break;
            default:
                encoderArm(1, true, 7);
                encoderDrive(driveSpeed, 1.5, 1.5);
        }

        // Drop the block
        leftGrabber.setPower(-1);
        rightGrabber.setPower(-1);
        sleep(2000);
        leftGrabber.setPower(0);
        rightGrabber.setPower(0);

        // Move backwards
        encoderDrive(driveSpeed,-6,-6);
        // Raise the arm
        switch (markerSquare) {
            case 1:
                encoderArm(1, true, 4.5);
                break;
            case 2:
                encoderArm(1, true, 2);
                break;
        }
        // Turn 90 degrees to the left.
        rotate(turnSpeed, "left", 90);
        // Drive up to the barrier.
        encoderDrive(driveSpeed,25,25);


        // Debugging Telemetry data for the end of the code.
        while (opModeIsActive()) {
            telemetry.addData("Found", found);
            telemetry.addData("Found2", found2);
            telemetry.addData("Square Number", markerSquare);
            telemetry.addData("Front Position", "Running at %7d :%7d", fLeft.getCurrentPosition(), fRight.getCurrentPosition());
            telemetry.addData("Back Position", "Running at %7d :%7d", bLeft.getCurrentPosition(), bRight.getCurrentPosition());
            // telemetry.addData("Rotation", angles.firstAngle);
            telemetry.update();
        }

    }
}